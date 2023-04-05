package co.oril.hellosign.services;

import co.oril.hellosign.exceptions.BadRequestException;
import co.oril.hellosign.exceptions.HelloSignLocalException;
import co.oril.hellosign.models.database.DocumentToSign;
import co.oril.hellosign.models.dto.Callback;
import co.oril.hellosign.models.dto.Signer;
import co.oril.hellosign.models.enums.DocumentType;
import co.oril.hellosign.models.enums.SignerRole;
import co.oril.hellosign.properties.HelloSignProperties;
import co.oril.hellosign.repositories.DocumentToSignRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellosign.sdk.HelloSignClient;
import com.hellosign.sdk.HelloSignException;
import com.hellosign.sdk.resource.EmbeddedRequest;
import com.hellosign.sdk.resource.SignatureRequest;
import com.hellosign.sdk.resource.Template;
import com.hellosign.sdk.resource.TemplateSignatureRequest;
import com.hellosign.sdk.resource.support.CustomField;
import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static co.oril.hellosign.models.enums.SignStatus.PENDING;
import static co.oril.hellosign.models.enums.SignStatus.SIGNED;

@Service
public class HelloSignService {

	private final String FIRST_SIGNER_ROLE = "First signer";
	private final String SECOND_SIGNER_ROLE = "Second signer";
	private final String clientId;
	private final boolean isTest;
	private final HelloSignClient helloSignClient;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final DocumentToSignRepository repository;

	public HelloSignService(HelloSignProperties properties, DocumentToSignRepository repository) {
		helloSignClient = new HelloSignClient(properties.getApiKey());
		clientId = properties.getClientAppId();
		isTest = properties.isTest();
		this.repository = repository;
	}

	public String signDocumentByFirstSigner(DocumentType documentType) {
		return createSignRequest(documentType)
				// Parse the response from HelloSign
				.map(request -> requestToDocument(request, documentType))
				.map(repository::save)
				// Generate signing url for First Signer
				.map(document -> getSigningUrl(document.getFirstSigner().getSignatureId()))
				.orElseThrow(() -> new BadRequestException("Could not get sign url for document " + documentType));
	}

	public String signDocumentBySecondSigner(String requestId) {
		DocumentToSign document = getDocument(requestId);
		if (document.getSecondSigner().getStatus() == SIGNED) {
			throw new BadRequestException("Document already signed");
		}
		// Generate signing url for Second Signer
		return getSigningUrl(document.getSecondSigner().getSignatureId());
	}

	public List<DocumentToSign> getDocumentsToSign() {
		return repository.findAllByStatusIsAndFirstSigner_StatusIs(PENDING, SIGNED);
	}

	public void handleHelloSignEvent(String eventJson) {
		Callback callback;
		try {
			callback = objectMapper.readValue(eventJson, Callback.class);
		} catch (JsonProcessingException e) {
			throw new HelloSignLocalException("Could not parse HelloSign callback", e);
		}

		switch (callback.getEvent().getType()) {
			case "signature_request_signed":
				handleSignEvent(callback);
				break;
			case "signature_request_all_signed":
				handleAllSignedEvent(callback);
				break;
			case "signature_request_downloadable":
				handleCompleteEvent(callback);
				break;
			case "signature_request_declined":
			case "signature_request_canceled":
				handleCanceledEvent(callback);
				break;
			default:
				break;
		}
	}

	private void handleCanceledEvent(Callback callback) {
		repository.findBySignatureRequestId(callback.getSignatureRequestData().getSignatureRequestId()).ifPresent(repository::delete);
	}

	private void handleCompleteEvent(Callback callback) {
		DocumentToSign document = getDocument(callback.getSignatureRequestData().getSignatureRequestId());
		receiveSignedDocument(document);
	}

	private File getFile(String signatureRequestId) {
		try {
			return helloSignClient.getFiles(signatureRequestId, SignatureRequest.SIGREQ_FORMAT_PDF);
		} catch (HelloSignException e) {
			throw new HelloSignLocalException(e);
		}
	}

	private DocumentToSign receiveSignedDocument(DocumentToSign document) {
		final File signedDocument = getFile(document.getSignatureRequestId());
		try {
			document.setSignedDocumentInBase64(Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(signedDocument)));
		} catch (IOException e) {
			throw new BadRequestException("Cannot parse file");
		}
		return repository.save(document);
	}

	private void handleAllSignedEvent(Callback callback) {
		DocumentToSign document = handleSignEvent(callback);
		document.setStatus(SIGNED);
		repository.save(document);
	}

	private DocumentToSign handleSignEvent(Callback callback) {
		DocumentToSign document = getDocument(callback.getSignatureRequestData().getSignatureRequestId());
		document = repository.save(updateDocumentStatus(document, callback));
		return document;
	}

	private DocumentToSign updateDocumentStatus(DocumentToSign document, Callback callback) {
		callback.getSignatureRequestData().getSignatures().stream().filter(it -> it.getStatus().equalsIgnoreCase(SIGNED.name())).forEach(signer -> {
			switch (SignerRole.fromString(signer.getSignerRole())) {
				case SECOND_SIGNER:
					if (document.getSecondSigner().getStatus() != SIGNED) {
						document.getSecondSigner().setStatus(SIGNED);
					}
					break;
				case FIRST_SIGNER:
					if (document.getFirstSigner().getStatus() != SIGNED) {
						document.getFirstSigner().setStatus(SIGNED);
					}
					break;
			}
		});
		return document;
	}

	private DocumentToSign getDocument(String requestId) {
		return repository.findBySignatureRequestId(requestId).orElseThrow(() -> new BadRequestException("Could not find document to sign"));
	}

	private String getSigningUrl(String signatureId) {
		try {
			return helloSignClient.getEmbeddedSignUrl(signatureId).getSignUrl();
		} catch (HelloSignException e) {
			throw new HelloSignLocalException("Could not get sign url", e);
		}
	}

	private Optional<JSONObject> createSignRequest(DocumentType documentType) {
		// Receive a template from the HelloSign portal by name
		Optional<String> templateOptional = getTemplateId(documentType.title);
		if (templateOptional.isEmpty()) {
			return Optional.empty();
		}
		try {
			TemplateSignatureRequest request = new TemplateSignatureRequest();
			request.setTemplateId(templateOptional.get());
			request.setSubject(documentType.title);
			request.setTestMode(isTest);
			request.setMessage("");

			// Fill out the document with the necessary information. Field names must be the same as in the template.
			switch (documentType) {
				case DOCUMENT_ONE_SIGNER:
					fillDocumentWithOneSigner(request);
					break;
				case DOCUMENT_TWO_SIGNERS:
					fillDocumentWithTwoSigners(request);
					break;
				default:
					throw new BadRequestException("Document type is not supported: " + documentType.title);
			}

			EmbeddedRequest embedReq = new EmbeddedRequest(clientId, request);
			// Create the document on the HelloSign portal.
			SignatureRequest response = (SignatureRequest) helloSignClient.createEmbeddedRequest(embedReq);
			return Optional.of(new JSONObject(response.toString()));
		} catch (com.hellosign.sdk.HelloSignException e) {
			throw new HelloSignLocalException(e);
		}
	}

	private DocumentToSign requestToDocument(JSONObject signatureRequest, DocumentType documentType) {
		DocumentToSign document = DocumentToSign.builder()
				.id(ObjectId.get())
				.type(documentType)
				.status(PENDING)
				.build();

		String signatureRequestId = signatureRequest.getString("signature_request_id");
		document.setSignatureRequestId(signatureRequestId);
		switch (documentType) {
			case DOCUMENT_ONE_SIGNER:
				try {
					addSigner(document, signatureRequest.getJSONArray("signatures"), 0);
				} catch (Exception e) {
					throw new HelloSignLocalException("Could not parse HelloSign response");
				}
				break;
			case DOCUMENT_TWO_SIGNERS:
				try {
					addSigners(document, signatureRequest.getJSONArray("signatures"));
				} catch (Exception e) {
					throw new HelloSignLocalException("Could not parse HelloSign response");
				}
				break;
			default:
				throw new BadRequestException("Document type not supported: " + documentType.title);
		}
		return document;
	}

	private void addSigners(DocumentToSign document, JSONArray array) throws JSONException {
		if (array.length() < 2) {
			throw new HelloSignLocalException("Two signers should be present in array");
		}
		addSigner(document, array, 0);
		addSigner(document, array, 1);
	}

	private void addSigner(DocumentToSign document, JSONArray array, int index) throws JSONException {
		JSONObject signerJson = array.getJSONObject(index);
		SignerRole role = SignerRole.fromString(signerJson.getString("signer_role"));

		Signer signer = new Signer(
				signerJson.getString("signer_email_address"),
				signerJson.getString("signature_id"),
				role,
				PENDING
		);

		if (signer.getRole() == SignerRole.FIRST_SIGNER) {
			document.setFirstSigner(signer);
		} else if (signer.getRole() == SignerRole.SECOND_SIGNER) {
			document.setSecondSigner(signer);
		}
	}

	private Optional<String> getTemplateId(final String title) {
		return getTemplates()
				.stream()
				.filter(template -> template.getTitle().equalsIgnoreCase(title))
				.map(Template::getId)
				.findFirst();
	}

	private List<Template> getTemplates() {
		List<Template> templates = new ArrayList<>();
		try {
			helloSignClient.getTemplates().iterator().forEachRemaining(templates::add);
			return templates;
		} catch (com.hellosign.sdk.HelloSignException e) {
			return templates;
		}
	}

	private void fillDocumentWithTwoSigners(TemplateSignatureRequest request) throws HelloSignException {
		request.setSigner(FIRST_SIGNER_ROLE, "first_signer_email@email.co", "First signer Name");
		request.setSigner(SECOND_SIGNER_ROLE, "second_signer_email@email.co", "Second signer Name");
		setDocumentFields(request);
	}

	private void fillDocumentWithOneSigner(TemplateSignatureRequest request) throws HelloSignException {
		request.setSigner(FIRST_SIGNER_ROLE, "first_signer@email.co", "First signer Name");
		setDocumentFields(request);
	}

	private void setDocumentFields(TemplateSignatureRequest request) {
		request.addCustomField(createCustomField("date", new Date().toString()));
		request.addCustomField(createCustomField("principal", "full name"));
		request.addCustomField(createCustomField("address", "city, state, zip"));
	}

	private CustomField createCustomField(String name, String value) {
		CustomField customField = new CustomField();
		customField.setName(name);
		customField.setValue(value);
		customField.setEditor(FIRST_SIGNER_ROLE);
		customField.setIsRequired(false);
		return customField;
	}

}

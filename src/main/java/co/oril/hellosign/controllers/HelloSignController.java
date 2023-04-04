package co.oril.hellosign.controllers;

import co.oril.hellosign.models.database.DocumentToSign;
import co.oril.hellosign.models.enums.DocumentType;
import co.oril.hellosign.services.HelloSignService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1")
@AllArgsConstructor
public class HelloSignController {

	private final HelloSignService helloSignService;

	@GetMapping(value = "/hellosign/document/sign")
	public ResponseEntity<String> signDealershipAgreement(@RequestParam("name") String documentType) {
		DocumentType docType = DocumentType.fromString(documentType);
		return ResponseEntity.ok(helloSignService.signDocument(docType));
	}

	@PostMapping(value = "/hellosign/app/callback")
	public String helloSignAppCallback(HttpServletRequest request) {
		helloSignService.handleHelloSignEvent(request.getParameter("json"));
		return "Hello API Event Received";
	}

	@GetMapping(value = "/hellosign/{requestId}/sign")
	public ResponseEntity<String> signDocumentByBroker(@PathVariable("requestId") String requestId) {
		return ResponseEntity.ok(helloSignService.signDocumentByBroker(requestId));
	}

	@GetMapping(value = "/hellosign/documents")
	public ResponseEntity<List<DocumentToSign>> getDocumentsToSign() {
		return ResponseEntity.ok(helloSignService.getDocumentsToSign());
	}

}

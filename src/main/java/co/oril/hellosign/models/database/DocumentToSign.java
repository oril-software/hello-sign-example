package co.oril.hellosign.models.database;

import co.oril.hellosign.models.dto.Signer;
import co.oril.hellosign.models.enums.DocumentType;
import co.oril.hellosign.models.enums.SignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "documents_to_sign")
public class DocumentToSign implements Serializable {

	@Id
	private ObjectId id;
	private String signatureRequestId;
	private Signer firstSigner;
	private Signer secondSigner;
	private SignStatus status;
	private DocumentType type;
	private String signedDocumentInBase64;

}

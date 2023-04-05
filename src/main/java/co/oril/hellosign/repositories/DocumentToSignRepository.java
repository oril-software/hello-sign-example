package co.oril.hellosign.repositories;


import co.oril.hellosign.models.database.DocumentToSign;
import co.oril.hellosign.models.enums.SignStatus;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentToSignRepository extends MongoRepository<DocumentToSign, ObjectId> {

	Optional<DocumentToSign> findBySignatureRequestId(String signatureRequestId);

	List<DocumentToSign> findAllByStatusIsAndFirstSigner_StatusIs(SignStatus status, SignStatus firstSignerStatus);

}

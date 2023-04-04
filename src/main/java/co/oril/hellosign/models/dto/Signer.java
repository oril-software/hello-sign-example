package co.oril.hellosign.models.dto;

import co.oril.hellosign.models.enums.SignStatus;
import co.oril.hellosign.models.enums.SignerRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Signer implements Serializable {

	private String email;
	private String signatureId;
	private SignerRole role;
	private SignStatus status;

}

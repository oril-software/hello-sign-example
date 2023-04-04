package co.oril.hellosign.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignatureInformation {

	@JsonProperty("signer_name")
	private String signerName;
	@JsonProperty("signature_id")
	private String signatureId;
	@JsonProperty("status_code")
	private String status;
	@JsonProperty("last_viewed_at")
	private Date lastViewedAt;
	@JsonProperty("signer_role")
	private String signerRole;
	@JsonProperty("signed_at")
	private Date signedAt;
	@JsonProperty("signer_email_address")
	private String signerEmail;
	@JsonProperty("last_reminded_at")
	private Date lastRemindedAt;
	@JsonProperty("error")
	private String error;
	@JsonProperty("order")
	private String order;

}

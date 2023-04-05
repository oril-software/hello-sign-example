package co.oril.hellosign.models.enums;

import java.io.Serializable;

public enum SignerRole implements Serializable {

	FIRST_SIGNER, SECOND_SIGNER;

	public static SignerRole fromString(String role) {
		if (role.equalsIgnoreCase(FIRST_SIGNER.name())) {
			return FIRST_SIGNER;
		} else if (role.equalsIgnoreCase(SECOND_SIGNER.name())) {
			return SECOND_SIGNER;
		}

		throw new IllegalArgumentException("Incorrect role argument");
	}

}

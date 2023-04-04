package co.oril.hellosign.models.enums;

import java.io.Serializable;

public enum SignerRole implements Serializable {

	DEALER, BROKER;

	public static SignerRole fromString(String role) {
		if (role.equalsIgnoreCase(DEALER.name())) {
			return DEALER;
		} else if (role.equalsIgnoreCase(BROKER.name())) {
			return BROKER;
		}

		throw new IllegalArgumentException("Incorrect role argument");
	}

}

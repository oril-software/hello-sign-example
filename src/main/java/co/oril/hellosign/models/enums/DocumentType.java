package co.oril.hellosign.models.enums;

import java.io.Serializable;

public enum DocumentType implements Serializable {

	DOCUMENT_ONE_SIGNER("Auto Use New Agreement"),
	DOCUMENT_TWO_SIGNERS("Dealership Agreement 1"),
	UNKNOWN("Unknown");

	public final String title;

	DocumentType(String title) {
		this.title = title;
	}

	public static DocumentType fromString(String type) {
		for (DocumentType value : values()) {
			if (value.name().equalsIgnoreCase(type)) {
				return value;
			}
		}

		return UNKNOWN;
	}

}

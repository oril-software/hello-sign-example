package co.oril.hellosign.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Getter
@EnableConfigurationProperties
@ConfigurationProperties("hello-sign-properties")
public class HelloSignProperties {

	private final String clientAppId;
	private final String apiKey;
	private final boolean isTest;

	public HelloSignProperties(String clientAppId, String apiKey, boolean isTest) {
		this.clientAppId = clientAppId;
		this.apiKey = apiKey;
		this.isTest = isTest;
	}

}

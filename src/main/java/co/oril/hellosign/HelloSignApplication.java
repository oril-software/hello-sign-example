package co.oril.hellosign;

import co.oril.hellosign.properties.HelloSignProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		HelloSignProperties.class
})
public class HelloSignApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloSignApplication.class, args);
	}

}

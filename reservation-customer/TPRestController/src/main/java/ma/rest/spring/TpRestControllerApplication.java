package ma.rest.spring;

import ma.rest.spring.controllers.CustomerSoapService;
import ma.rest.spring.controllers.CustomerWS;
import ma.rest.spring.model.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@SpringBootApplication

public class TpRestControllerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TpRestControllerApplication.class, args);
	}

	@Bean
	RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@Bean
	CustomerSoapService customerSoapService(){
		return new CustomerWS().getCustomerSoapServicePort();
	}
}

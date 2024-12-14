package ma.rest.spring;

import ma.rest.spring.entities.*;
import ma.rest.spring.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class TpRestControllerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TpRestControllerApplication.class, args);
	}

	@Bean
	CommandLineRunner start(CustomerRepository customerRepository) {
		return args -> {
			// Ajouter des clients

			Customer client1 = customerRepository.save(new Customer(null,"aya", "aya@gmail.com"));

			Customer client2 = customerRepository.save(new Customer(null,"douaa", "douaa@gmail.com"));


		};
	}
}

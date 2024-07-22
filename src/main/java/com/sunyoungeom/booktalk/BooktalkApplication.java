package com.sunyoungeom.booktalk;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(servers = {@Server(url = "https://https://3.36.235.110.nip.io", description = "북토크")})
@SpringBootApplication
public class BooktalkApplication {

	public static void main(String[] args) {
		SpringApplication.run(BooktalkApplication.class, args);
	}

}

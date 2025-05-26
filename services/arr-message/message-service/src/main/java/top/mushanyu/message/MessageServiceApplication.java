package top.mushanyu.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "top.mushanyu.message.dao")
@EntityScan(basePackages = "top.mushanyu.message.domain")
@EnableFeignClients
public class MessageServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessageServiceApplication.class, args);
	}

}

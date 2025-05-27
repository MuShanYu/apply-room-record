package top.mushanyu.message;

import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.validation.internal.ValidationExceptionMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import top.mushanyu.config.serialize.exception.AlertExceptionMapper;
import top.mushanyu.config.serialize.exception.JacksonExceptionMapper;
import top.mushanyu.config.serialize.exception.ThrowableExceptionMapper;
import top.mushanyu.config.serialize.exception.WebApplicationExceptionMapper;
import top.mushanyu.config.serialize.jackson.JacksonMapperProvider;
import top.mushanyu.config.session.filter.SessionProviderFilter;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "top.mushanyu.message.dao")
@EntityScan(basePackages = "top.mushanyu.message.domain")
@EnableFeignClients
public class MessageServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessageServiceApplication.class, args);
	}

	@Bean
	public ResourceConfig jerseyConfig() {
		var config = new ResourceConfig();
		config.property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
		config.register(JacksonFeature.withoutExceptionMappers());
		config.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(ValidationExceptionMapper.class).to(ExceptionMapper.class).in(Singleton.class);
			}
		});
		config.register(JacksonExceptionMapper.class);
		config.register(AlertExceptionMapper.class);
		config.register(ThrowableExceptionMapper.class);
		config.register(WebApplicationExceptionMapper.class);
		config.register(JacksonMapperProvider.class);
		config.register(SessionProviderFilter.class);
		config.packages(false,
				"top.mushanyu.message.api");
		return config;
	}
}

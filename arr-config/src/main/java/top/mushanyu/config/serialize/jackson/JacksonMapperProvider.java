package top.mushanyu.config.serialize.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import top.mushanyu.common.exception.AlertException;
import top.mushanyu.common.problem.jackson.ProblemModule;

@Provider
public class JacksonMapperProvider implements ContextResolver<ObjectMapper> {

    final ObjectMapper mapper;

    public JacksonMapperProvider() {
        var objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        objectMapper.registerModule(module);// support jsr310
        objectMapper.registerModule(new Jdk8Module());

        objectMapper.registerModule(new ProblemModule());
        objectMapper.registerSubtypes(AlertException.class);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);
        objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.mapper = objectMapper;
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}

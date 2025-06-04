package top.mushanyu.web.config.converter;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import top.mushanyu.common.exception.AlertException;
import top.mushanyu.common.problem.jackson.ProblemModule;
import top.mushanyu.web.config.serializer.DateTimeSerializer;
import top.mushanyu.web.config.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class WebObjectMapper extends ObjectMapper {


    public WebObjectMapper() {

        super();
        JavaTimeModule module = new JavaTimeModule();

        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        module.addSerializer(DateTime.class, new DateTimeSerializer());

        this.registerModule(module);
        this.registerModule(new ProblemModule());
        this.registerSubtypes(AlertException.class);

        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        this.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        this.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);
        this.configure(SerializationFeature.WRITE_ENUM_KEYS_USING_INDEX, true);
        this.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, false);
        this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}

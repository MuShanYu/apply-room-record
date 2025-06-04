package top.mushanyu.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.function.SingletonSupplier;
import top.mushanyu.common.exception.AlertException;
import top.mushanyu.common.problem.jackson.ProblemModule;

import java.util.List;

@Slf4j
public class JsonUtil {

    private static final SingletonSupplier<ObjectMapper> MAPPER = SingletonSupplier.of(JsonUtil::createAndConfigMapper);

    private JsonUtil() {
    }

    public static String encode(Object obj) {

        try {
            return mapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T decode(String content, Class<T> valueType) {
        try {
            return mapper().readValue(content, valueType);
        } catch (Exception e) {
            log.error("JsonUtils.decode.error.content is [{}] and class is [{}]", content, valueType.getName());
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> decodeToList(String content, Class<T> valueType) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JavaType type = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, valueType);
            return mapper().readValue(content, type);
        } catch (Exception e) {
            log.error("JsonUtils.decodeToList.error.content is [{}] and class is [{}]", content, valueType.getName());
            throw new RuntimeException(e);
        }
    }

    public static <T> T decode(String content, TypeReference<T> typeReference) {
        try {
            return mapper().readValue(content, typeReference);
        } catch (Exception e) {
            log.error("JsonUtils.decode.error.content is [{}] and type reference is [{}]", content, typeReference.getType().getTypeName());

            throw new RuntimeException(e);
        }
    }

    public static <T> T decode(String content, JavaType javaType) {
        try {
            return mapper().readValue(content, javaType);
        } catch (Exception e) {
            log.error("JsonUtils.decode.error.content is [{}] and type is [{}]", content, javaType.getRawClass().getName());
            throw new RuntimeException(e);
        }
    }


    private static ObjectMapper mapper() {
        return MAPPER.get();
    }

    public static ObjectMapper createAndConfigMapper() {
        var objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        objectMapper.registerModule(module);// support jsr310
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new ProblemModule());

        objectMapper.registerSubtypes(AlertException.class); // 注意
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, false);
        objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

}

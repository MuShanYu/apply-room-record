package com.guet.ARC.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * Author: Yulf
 * Date: 2023/11/14
 */
@JsonComponent
public class EnumSerializerComponent<T extends Enum<T>> extends JsonSerializer<T> {
    @Override
    public void serialize(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(t.ordinal());
    }
}

package top.mushanyu.common.problem.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import jakarta.ws.rs.core.Response;

import java.io.IOException;

final class StatusTypeSerializer extends JsonSerializer<Response.StatusType> {

    @Override
    public void serialize(final Response.StatusType status, final JsonGenerator json, final SerializerProvider serializers) throws IOException {
        json.writeNumber(status.getStatusCode());
    }

}

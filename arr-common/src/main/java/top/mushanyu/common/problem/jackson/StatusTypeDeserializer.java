package top.mushanyu.common.problem.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.Map;

final class StatusTypeDeserializer extends JsonDeserializer<Response.StatusType> {

    private final Map<Integer, Response.StatusType> index;

    StatusTypeDeserializer(final Map<Integer, Response.StatusType> index) {
        this.index = index;
    }

    @Override
    public Response.StatusType deserialize(final JsonParser json, final DeserializationContext context) throws IOException {
        final int statusCode = json.getIntValue();
        final Response.StatusType status = index.get(statusCode);
        return status == null ? new UnknownStatus(statusCode) : status;
    }

}

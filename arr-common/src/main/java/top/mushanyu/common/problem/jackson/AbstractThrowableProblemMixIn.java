package top.mushanyu.common.problem.jackson;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.ws.rs.core.Response;
import top.mushanyu.common.problem.AbstractThrowableProblem;
import top.mushanyu.common.problem.ThrowableProblem;

import java.net.URI;

abstract class AbstractThrowableProblemMixIn {

    @JsonCreator
    AbstractThrowableProblemMixIn(
             @JsonProperty("type") final URI type,
             @JsonProperty("title") final String title,
             @JsonProperty("status") final Response.StatusType status,
             @JsonProperty("detail") final String detail,
             @JsonProperty("instance") final URI instance,
             @JsonProperty("cause") final ThrowableProblem cause) {
        // this is just here to see whether "our" constructor matches the real one
        throw new AbstractThrowableProblem(type, title, status, detail, instance, cause) {

        };
    }

    @JsonAnySetter
    abstract void set(final String key, final Object value);

}

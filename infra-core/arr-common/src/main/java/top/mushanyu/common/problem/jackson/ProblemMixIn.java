package top.mushanyu.common.problem.jackson;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.ws.rs.core.Response;
import top.mushanyu.common.problem.DefaultProblem;
import top.mushanyu.common.problem.Problem;

import java.net.URI;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = DefaultProblem.class,
        visible = true)
@JsonInclude(NON_EMPTY)
interface ProblemMixIn extends Problem {

    @JsonProperty("type")
    @JsonSerialize(converter = ProblemTypeConverter.class)
    @Override
    URI getType();

    @JsonProperty("title")
    @Override
    String getTitle();

    @JsonProperty("status")
    @Override
    Response.StatusType getStatus();

    @JsonProperty("detail")
    @Override
    String getDetail();

    @JsonProperty("instance")
    @Override
    URI getInstance();

    @JsonAnyGetter
    @Override
    Map<String, Object> getParameters();

}

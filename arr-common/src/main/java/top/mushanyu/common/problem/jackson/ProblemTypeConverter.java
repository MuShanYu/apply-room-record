package top.mushanyu.common.problem.jackson;

import com.fasterxml.jackson.databind.util.StdConverter;
import top.mushanyu.common.problem.Problem;

import java.net.URI;

public final class ProblemTypeConverter extends StdConverter<URI, URI> {

    @Override
    public URI convert(final URI value) {
        return Problem.DEFAULT_TYPE.equals(value) ? null : value;
    }

}

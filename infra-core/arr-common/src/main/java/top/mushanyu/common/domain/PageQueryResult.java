package top.mushanyu.common.domain;

import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

public record PageQueryResult<T>(List<T> data,
                                 Long total/*总记录数*/,
                                 Integer pages /*总页数*/) implements Serializable {

    public PageQueryResult(List<T> data) {
        this(data, null, null);
    }

    public static <T> PageQueryResult<T> empty() {
        return new PageQueryResult<>(List.of(), 0L, 0);
    }

    public static <T> PageQueryResult<T> of(List<T> data) {
        return new PageQueryResult<>(data);
    }

    public static <T, P> PageQueryResult<T> of(Page<P> page, Function<List<P>, List<T>> mapper) {
        return new PageQueryResult<>(mapper.apply(page.getContent()), page.getTotalElements(), page.getTotalPages());
    }

    public static <T, P> PageQueryResult<T> of(PageQueryResult<P> page, Function<List<P>, List<T>> mapper) {
        return new PageQueryResult<>(mapper.apply(page.data), page.total, page.pages);
    }

}

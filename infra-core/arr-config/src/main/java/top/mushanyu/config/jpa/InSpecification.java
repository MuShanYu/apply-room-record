package top.mushanyu.config.jpa;

import cn.hutool.core.collection.ListUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;

public class InSpecification<T> implements Specification<T> {

    private static final int IN_MAX_SIZE = 1000;


    private final Collection<?> values;
    private final String attribute;

    InSpecification(String attribute, Collection<?> inRes) {
        this.attribute = attribute;
        this.values = inRes;
    }

    @Override
    public Predicate toPredicate(@NotNull Root<T> root, CriteriaQuery<?> query, @NotNull CriteriaBuilder criteriaBuilder) {
        if (values.size() > IN_MAX_SIZE) {
            return criteriaBuilder.or(ListUtil.partition(new ArrayList<>(values), IN_MAX_SIZE).stream()
                    .map(groupRes -> root.get(attribute).in(groupRes)).toArray(Predicate[]::new));
        }
        return root.get(attribute).in(values);
    }
}

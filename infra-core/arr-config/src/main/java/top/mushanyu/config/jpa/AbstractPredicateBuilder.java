package top.mushanyu.config.jpa;

import cn.hutool.core.util.ArrayUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPredicateBuilder<T> {

    private final Predicate.BooleanOperator operator;

    protected final List<Specification<T>> specifications;


    protected AbstractPredicateBuilder(Predicate.BooleanOperator operator) {
        this.operator = operator;
        this.specifications = new ArrayList<>();
    }

    protected Specification<T> buildSpecification() {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate[] predicates = specifications.stream()
                    .map(spec -> spec.toPredicate(root, query, cb)).toArray(Predicate[]::new);
            if (ArrayUtil.isEmpty(predicates)) {
                return null;
            }
            return Predicate.BooleanOperator.OR.equals(operator) ? cb.or(predicates) : cb.and(predicates);
        };
    }


}

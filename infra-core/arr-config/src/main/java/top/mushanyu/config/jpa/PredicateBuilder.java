package top.mushanyu.config.jpa;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.data.jpa.domain.Specification;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.function.Predicate;

public class PredicateBuilder<T> extends AbstractPredicateBuilder<T> {

    public static String escapeLikeValue(String value) {
        if (value == null) {
            return null;
        }
        return value.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }


    // switch 21支持模式匹配，在此处扩展
    Predicate<Object> NO_NULL_PREDICATE = ObjectUtil::isNotEmpty;

    private PredicateBuilder(jakarta.persistence.criteria.Predicate.BooleanOperator operator) {
        super(operator);
    }

    // ======================= >>> 推荐使用
    // ======================= >>> apply by no_null_predicate.
    public PredicateBuilder<T> equal(String attribute, Object value) {
        return this.equal(NO_NULL_PREDICATE, attribute, value);
    }

    public PredicateBuilder<T> notEqual(String attribute, Object value) {
        return this.notEqual(NO_NULL_PREDICATE, attribute, value);
    }

    public PredicateBuilder<T> in(String attribute, Collection<?> collection) {
        return this.in(CollectionUtil::isNotEmpty, attribute, collection);
    }


    // ======================= >>> 推荐使用
    // ======================= >>> apply by predicate.
    public <PP> PredicateBuilder<T> equal(Predicate<PP> pre, String attribute, PP value) {
        return equal(pre.test(value), attribute, value);
    }

    public <PP> PredicateBuilder<T> notEqual(Predicate<PP> pre, String attribute, PP value) {
        return notEqual(pre.test(value), attribute, value);
    }

    public PredicateBuilder<T> in(Predicate<Collection<?>> pre, String attribute, Collection<?> collection) {
        return in(pre.test(collection), attribute, collection);
    }

    public PredicateBuilder<T> like(Predicate<String> pre, String attribute, String value) {
        value = escapeLikeValue(value);
        return like(pre.test(value), attribute, "%" + value + "%");
    }

    public PredicateBuilder<T> suffixLike(Predicate<String> pre, String attribute, String value) {
        value = escapeLikeValue(value);
        return like(pre.test(value), attribute, value + "%");
    }


    // ======================= >>> apply by boolean.
    public PredicateBuilder<T> equal(boolean condition, String attribute, Object value) {
        return this.predicate(condition, (root, query, cb) -> cb.equal(root.get(attribute), value));
    }

    public PredicateBuilder<T> isNull(boolean condition, String attribute) {
        return this.predicate(condition, (root, query, cb) -> cb.isNull(root.get(attribute)));
    }

    public PredicateBuilder<T> isNotNull(boolean condition, String attribute) {
        return this.predicate(condition, (root, query, cb) -> cb.isNotNull(root.get(attribute)));
    }

    public PredicateBuilder<T> notEqual(boolean condition, String attribute, Object value) {
        return this.predicate(condition, (root, query, cb) -> cb.notEqual(root.get(attribute), value));
    }

    public PredicateBuilder<T> like(boolean condition, String attribute, String value) {
        return this.predicate(condition, (root, query, cb) -> cb.like(root.get(attribute), value, '\\'));
    }

    public PredicateBuilder<T> in(boolean condition, String attribute, Collection<?> collection) {
        return this.predicate(condition, new InSpecification<T>(attribute, collection));
    }

    public PredicateBuilder<T> betweenInstant(boolean condition, String attribute, Instant start, Instant end) {
        return this.predicate(condition, (root, query, cb) -> cb.between(root.get(attribute), start, end));
    }

    public PredicateBuilder<T> betweenLocalDate(boolean condition, String attribute, LocalDate start, LocalDate end) {
        return this.predicate(condition, (root, query, cb) -> cb.between(root.get(attribute), start, end));
    }

    public PredicateBuilder<T> betweenLocalDateTime(boolean condition, String attribute, LocalDateTime start, LocalDateTime end) {
        return this.predicate(condition, (root, query, cb) -> cb.between(root.get(attribute), start, end));
    }

    public PredicateBuilder<T> greaterThan(boolean condition, String attribute, Instant value) {
        return this.predicate(condition, (root, query, cb) -> cb.greaterThan(root.get(attribute), value));
    }

    public PredicateBuilder<T> lessThan(boolean condition, String attribute, Instant value) {
        return this.predicate(condition, (root, query, cb) -> cb.lessThan(root.get(attribute), value));
    }

    public PredicateBuilder<T> greaterThanOrEqualTo(boolean condition, String attribute, Instant value) {
        return this.predicate(condition, (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(attribute), value));
    }

    public PredicateBuilder<T> lessThanOrEqualTo(boolean condition, String attribute, Instant value) {
        return this.predicate(condition, (root, query, cb) -> cb.lessThanOrEqualTo(root.get(attribute), value));
    }

    public PredicateBuilder<T> greaterThanOrEqualTo(boolean condition, String attribute, LocalDateTime value) {
        return this.predicate(condition, (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(attribute), value));
    }

    public PredicateBuilder<T> lessThanOrEqualTo(boolean condition, String attribute, LocalDateTime value) {
        return this.predicate(condition, (root, query, cb) -> cb.lessThanOrEqualTo(root.get(attribute), value));
    }

    public Specification<T> build() {
        return buildSpecification();
    }

    public static <T> PredicateBuilder<T> and() {
        return new PredicateBuilder<>(jakarta.persistence.criteria.Predicate.BooleanOperator.AND);
    }

    public static <T> PredicateBuilder<T> or() {
        return new PredicateBuilder<>(jakarta.persistence.criteria.Predicate.BooleanOperator.OR);
    }


    protected PredicateBuilder<T> predicate(boolean condition, Specification<T> specification) {
        if (condition) {
            this.specifications.add(specification);
        }
        return this;
    }
}

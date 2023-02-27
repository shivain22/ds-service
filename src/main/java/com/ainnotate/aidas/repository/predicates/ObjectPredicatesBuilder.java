package com.ainnotate.aidas.repository.predicates;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ObjectPredicatesBuilder {
    private List<SearchCriteria> params;

    public ObjectPredicatesBuilder() {
        params = new ArrayList<>();
    }

    public ObjectPredicatesBuilder with(
      String key, String operation, Object value) {

        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public BooleanExpression build() {
        if (params.size() == 0) {
            return null;
        }

        List predicates = params.stream().map(param -> {
            ObjectPredicate predicate = new ObjectPredicate(param);
            return predicate.getPredicate();
        }).filter(Objects::nonNull).collect(Collectors.toList());

        BooleanExpression result = Expressions.asBoolean(true).isTrue();
        for (Object predicate : predicates) {
            result = result.and((BooleanExpression)predicate);
        }
        return result;
    }
}

package com.ainnotate.aidas.repository.predicates;

import static org.apache.commons.lang3.StringUtils.isNumeric;

import com.ainnotate.aidas.domain.Object;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;

public class ObjectPredicate {
    public ObjectPredicate(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    private SearchCriteria criteria;

    public BooleanExpression getPredicate() {
        PathBuilder<Object> entityPath = new PathBuilder<>(Object.class, "object1");

        if (isNumeric(criteria.getValue().toString())) {
        	if(!criteria.getKey().equals("name")) {
	            NumberPath<Integer> path = entityPath.getNumber(criteria.getKey(), Integer.class);
	            int value = Integer.parseInt(criteria.getValue().toString());
	            switch (criteria.getOperation()) {
	                case ":":
	                    return path.eq(value);
	                case ">":
	                    return path.goe(value);
	                case "<":
	                    return path.loe(value);
	            }
        	}else {
                StringPath path = entityPath.getString(criteria.getKey());
                if (criteria.getOperation().equalsIgnoreCase(":")) {
                    return path.containsIgnoreCase(criteria.getValue().toString());
                }
            }
        }
        else {
            StringPath path = entityPath.getString(criteria.getKey());
            if (criteria.getOperation().equalsIgnoreCase(":")) {
                return path.containsIgnoreCase(criteria.getValue().toString());
            }
        }
        return null;
    }
}

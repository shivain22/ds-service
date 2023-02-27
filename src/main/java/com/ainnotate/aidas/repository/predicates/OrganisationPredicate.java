package com.ainnotate.aidas.repository.predicates;

import com.ainnotate.aidas.domain.Organisation;
import com.ainnotate.aidas.domain.Project;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;

import static org.apache.commons.lang3.StringUtils.isNumeric;

public class OrganisationPredicate {
    public OrganisationPredicate(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    private SearchCriteria criteria;

    public BooleanExpression getPredicate() {
        PathBuilder<Object> entityPath = new PathBuilder<>(Organisation.class, "organisation");

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

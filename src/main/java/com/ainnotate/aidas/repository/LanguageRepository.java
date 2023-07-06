package com.ainnotate.aidas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.Language;
import com.ainnotate.aidas.domain.QLanguage;
import com.ainnotate.aidas.domain.QUser;
import com.ainnotate.aidas.domain.User;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
@Transactional
public interface LanguageRepository extends JpaRepository<Language, Long>,QuerydslPredicateExecutor<Language>, QuerydslBinderCustomizer<QLanguage>{

   @Query(value="select * from language order by name limit 10",nativeQuery = true)
   List<Language> getLanguages();

   @Override
   default public void customize(
       QuerydslBindings bindings, QLanguage root) {
       bindings.bind(String.class)
           .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);

   }
}

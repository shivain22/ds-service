package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
@Transactional
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByName(String name);

    Category findByValue(String value);

    @Query(value ="select * from category where id>1 and id<6 order by id" ,nativeQuery = true)
    List<Category> findAllCategories();

}

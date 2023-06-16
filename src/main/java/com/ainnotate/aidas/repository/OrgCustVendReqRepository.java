package com.ainnotate.aidas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.Language;
import com.ainnotate.aidas.domain.OrgCustVendReq;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
@Transactional
public interface OrgCustVendReqRepository extends JpaRepository<OrgCustVendReq, Long> {

   

}

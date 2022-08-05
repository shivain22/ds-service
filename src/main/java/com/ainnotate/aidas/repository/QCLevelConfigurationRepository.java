package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.QCLevelConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface QCLevelConfigurationRepository extends JpaRepository<QCLevelConfiguration,Long> {


    @Query(value="select p.* from qclevel_configuration p where p.id=?1  ",nativeQuery = true)
    public List<QCLevelConfiguration> findByProejctId(Long projectId);
}

package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectPropertyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectProperty.class);
        ProjectProperty projectProperty1 = new ProjectProperty();
        projectProperty1.setId(1L);
        ProjectProperty projectProperty2 = new ProjectProperty();
        projectProperty2.setId(projectProperty1.getId());
        assertThat(projectProperty1).isEqualTo(projectProperty2);
        projectProperty2.setId(2L);
        assertThat(projectProperty1).isNotEqualTo(projectProperty2);
        projectProperty1.setId(null);
        assertThat(projectProperty1).isNotEqualTo(projectProperty2);
    }
}

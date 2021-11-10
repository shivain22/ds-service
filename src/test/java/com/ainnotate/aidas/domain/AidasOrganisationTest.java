package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AidasOrganisationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AidasOrganisation.class);
        AidasOrganisation aidasOrganisation1 = new AidasOrganisation();
        aidasOrganisation1.setId(1L);
        AidasOrganisation aidasOrganisation2 = new AidasOrganisation();
        aidasOrganisation2.setId(aidasOrganisation1.getId());
        assertThat(aidasOrganisation1).isEqualTo(aidasOrganisation2);
        aidasOrganisation2.setId(2L);
        assertThat(aidasOrganisation1).isNotEqualTo(aidasOrganisation2);
        aidasOrganisation1.setId(null);
        assertThat(aidasOrganisation1).isNotEqualTo(aidasOrganisation2);
    }
}

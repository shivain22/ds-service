package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AidasProjectPropertyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AidasProjectProperty.class);
        AidasProjectProperty aidasProjectProperty1 = new AidasProjectProperty();
        aidasProjectProperty1.setId(1L);
        AidasProjectProperty aidasProjectProperty2 = new AidasProjectProperty();
        aidasProjectProperty2.setId(aidasProjectProperty1.getId());
        assertThat(aidasProjectProperty1).isEqualTo(aidasProjectProperty2);
        aidasProjectProperty2.setId(2L);
        assertThat(aidasProjectProperty1).isNotEqualTo(aidasProjectProperty2);
        aidasProjectProperty1.setId(null);
        assertThat(aidasProjectProperty1).isNotEqualTo(aidasProjectProperty2);
    }
}

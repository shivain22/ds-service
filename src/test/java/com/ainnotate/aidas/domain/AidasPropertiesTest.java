package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AidasPropertiesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AidasProperties.class);
        AidasProperties aidasProperties1 = new AidasProperties();
        aidasProperties1.setId(1L);
        AidasProperties aidasProperties2 = new AidasProperties();
        aidasProperties2.setId(aidasProperties1.getId());
        assertThat(aidasProperties1).isEqualTo(aidasProperties2);
        aidasProperties2.setId(2L);
        assertThat(aidasProperties1).isNotEqualTo(aidasProperties2);
        aidasProperties1.setId(null);
        assertThat(aidasProperties1).isNotEqualTo(aidasProperties2);
    }
}

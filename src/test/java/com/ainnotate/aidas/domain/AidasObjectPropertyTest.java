package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AidasObjectPropertyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AidasObjectProperty.class);
        AidasObjectProperty aidasObjectProperty1 = new AidasObjectProperty();
        aidasObjectProperty1.setId(1L);
        AidasObjectProperty aidasObjectProperty2 = new AidasObjectProperty();
        aidasObjectProperty2.setId(aidasObjectProperty1.getId());
        assertThat(aidasObjectProperty1).isEqualTo(aidasObjectProperty2);
        aidasObjectProperty2.setId(2L);
        assertThat(aidasObjectProperty1).isNotEqualTo(aidasObjectProperty2);
        aidasObjectProperty1.setId(null);
        assertThat(aidasObjectProperty1).isNotEqualTo(aidasObjectProperty2);
    }
}

package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AidasUserAidasObjectMappingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AidasUserAidasObjectMapping.class);
        AidasUserAidasObjectMapping aidasUserAidasObjectMapping1 = new AidasUserAidasObjectMapping();
        aidasUserAidasObjectMapping1.setId(1L);
        AidasUserAidasObjectMapping aidasUserAidasObjectMapping2 = new AidasUserAidasObjectMapping();
        aidasUserAidasObjectMapping2.setId(aidasUserAidasObjectMapping1.getId());
        assertThat(aidasUserAidasObjectMapping1).isEqualTo(aidasUserAidasObjectMapping2);
        aidasUserAidasObjectMapping2.setId(2L);
        assertThat(aidasUserAidasObjectMapping1).isNotEqualTo(aidasUserAidasObjectMapping2);
        aidasUserAidasObjectMapping1.setId(null);
        assertThat(aidasUserAidasObjectMapping1).isNotEqualTo(aidasUserAidasObjectMapping2);
    }
}

package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AidasObjectTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AidasObject.class);
        AidasObject aidasObject1 = new AidasObject();
        aidasObject1.setId(1L);
        AidasObject aidasObject2 = new AidasObject();
        aidasObject2.setId(aidasObject1.getId());
        assertThat(aidasObject1).isEqualTo(aidasObject2);
        aidasObject2.setId(2L);
        assertThat(aidasObject1).isNotEqualTo(aidasObject2);
        aidasObject1.setId(null);
        assertThat(aidasObject1).isNotEqualTo(aidasObject2);
    }
}

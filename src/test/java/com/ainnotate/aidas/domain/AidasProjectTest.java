package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AidasProjectTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AidasProject.class);
        AidasProject aidasProject1 = new AidasProject();
        aidasProject1.setId(1L);
        AidasProject aidasProject2 = new AidasProject();
        aidasProject2.setId(aidasProject1.getId());
        assertThat(aidasProject1).isEqualTo(aidasProject2);
        aidasProject2.setId(2L);
        assertThat(aidasProject1).isNotEqualTo(aidasProject2);
        aidasProject1.setId(null);
        assertThat(aidasProject1).isNotEqualTo(aidasProject2);
    }
}

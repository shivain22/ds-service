package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AidasUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AidasUser.class);
        AidasUser aidasUser1 = new AidasUser();
        aidasUser1.setId(1L);
        AidasUser aidasUser2 = new AidasUser();
        aidasUser2.setId(aidasUser1.getId());
        assertThat(aidasUser1).isEqualTo(aidasUser2);
        aidasUser2.setId(2L);
        assertThat(aidasUser1).isNotEqualTo(aidasUser2);
        aidasUser1.setId(null);
        assertThat(aidasUser1).isNotEqualTo(aidasUser2);
    }
}

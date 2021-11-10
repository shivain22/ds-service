package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AidasUploadTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AidasUpload.class);
        AidasUpload aidasUpload1 = new AidasUpload();
        aidasUpload1.setId(1L);
        AidasUpload aidasUpload2 = new AidasUpload();
        aidasUpload2.setId(aidasUpload1.getId());
        assertThat(aidasUpload1).isEqualTo(aidasUpload2);
        aidasUpload2.setId(2L);
        assertThat(aidasUpload1).isNotEqualTo(aidasUpload2);
        aidasUpload1.setId(null);
        assertThat(aidasUpload1).isNotEqualTo(aidasUpload2);
    }
}

package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AidasUploadMetaDataTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AidasUploadMetaData.class);
        AidasUploadMetaData aidasUploadMetaData1 = new AidasUploadMetaData();
        aidasUploadMetaData1.setId(1L);
        AidasUploadMetaData aidasUploadMetaData2 = new AidasUploadMetaData();
        aidasUploadMetaData2.setId(aidasUploadMetaData1.getId());
        assertThat(aidasUploadMetaData1).isEqualTo(aidasUploadMetaData2);
        aidasUploadMetaData2.setId(2L);
        assertThat(aidasUploadMetaData1).isNotEqualTo(aidasUploadMetaData2);
        aidasUploadMetaData1.setId(null);
        assertThat(aidasUploadMetaData1).isNotEqualTo(aidasUploadMetaData2);
    }
}

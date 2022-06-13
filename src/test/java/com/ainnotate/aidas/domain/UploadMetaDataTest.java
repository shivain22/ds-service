package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UploadMetaDataTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UploadMetaData.class);
        UploadMetaData uploadMetaData1 = new UploadMetaData();
        uploadMetaData1.setId(1L);
        UploadMetaData uploadMetaData2 = new UploadMetaData();
        uploadMetaData2.setId(uploadMetaData1.getId());
        assertThat(uploadMetaData1).isEqualTo(uploadMetaData2);
        uploadMetaData2.setId(2L);
        assertThat(uploadMetaData1).isNotEqualTo(uploadMetaData2);
        uploadMetaData1.setId(null);
        assertThat(uploadMetaData1).isNotEqualTo(uploadMetaData2);
    }
}

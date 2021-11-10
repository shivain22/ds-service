package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AidasVendorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AidasVendor.class);
        AidasVendor aidasVendor1 = new AidasVendor();
        aidasVendor1.setId(1L);
        AidasVendor aidasVendor2 = new AidasVendor();
        aidasVendor2.setId(aidasVendor1.getId());
        assertThat(aidasVendor1).isEqualTo(aidasVendor2);
        aidasVendor2.setId(2L);
        assertThat(aidasVendor1).isNotEqualTo(aidasVendor2);
        aidasVendor1.setId(null);
        assertThat(aidasVendor1).isNotEqualTo(aidasVendor2);
    }
}

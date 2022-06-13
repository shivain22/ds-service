package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AidasUserVendorMappingObjectMappingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserVendorMappingObjectMapping.class);
        UserVendorMappingObjectMapping userVendorMappingObjectMapping1 = new UserVendorMappingObjectMapping();
        userVendorMappingObjectMapping1.setId(1L);
        UserVendorMappingObjectMapping userVendorMappingObjectMapping2 = new UserVendorMappingObjectMapping();
        userVendorMappingObjectMapping2.setId(userVendorMappingObjectMapping1.getId());
        assertThat(userVendorMappingObjectMapping1).isEqualTo(userVendorMappingObjectMapping2);
        userVendorMappingObjectMapping2.setId(2L);
        assertThat(userVendorMappingObjectMapping1).isNotEqualTo(userVendorMappingObjectMapping2);
        userVendorMappingObjectMapping1.setId(null);
        assertThat(userVendorMappingObjectMapping1).isNotEqualTo(userVendorMappingObjectMapping2);
    }
}

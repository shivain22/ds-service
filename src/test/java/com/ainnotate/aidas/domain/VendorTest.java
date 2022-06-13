package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VendorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Vendor.class);
        Vendor vendor1 = new Vendor();
        vendor1.setId(1L);
        Vendor vendor2 = new Vendor();
        vendor2.setId(vendor1.getId());
        assertThat(vendor1).isEqualTo(vendor2);
        vendor2.setId(2L);
        assertThat(vendor1).isNotEqualTo(vendor2);
        vendor1.setId(null);
        assertThat(vendor1).isNotEqualTo(vendor2);
    }
}

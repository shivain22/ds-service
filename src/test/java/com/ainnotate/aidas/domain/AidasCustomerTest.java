package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AidasCustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AidasCustomer.class);
        AidasCustomer aidasCustomer1 = new AidasCustomer();
        aidasCustomer1.setId(1L);
        AidasCustomer aidasCustomer2 = new AidasCustomer();
        aidasCustomer2.setId(aidasCustomer1.getId());
        assertThat(aidasCustomer1).isEqualTo(aidasCustomer2);
        aidasCustomer2.setId(2L);
        assertThat(aidasCustomer1).isNotEqualTo(aidasCustomer2);
        aidasCustomer1.setId(null);
        assertThat(aidasCustomer1).isNotEqualTo(aidasCustomer2);
    }
}

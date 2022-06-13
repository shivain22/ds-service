package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ObjectTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Object.class);
        Object object1 = new Object();
        object1.setId(1L);
        Object object2 = new Object();
        object2.setId(object1.getId());
        assertThat(object1).isEqualTo(object2);
        object2.setId(2L);
        assertThat(object1).isNotEqualTo(object2);
        object1.setId(null);
        assertThat(object1).isNotEqualTo(object2);
    }
}

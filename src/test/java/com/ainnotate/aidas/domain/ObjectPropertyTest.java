package com.ainnotate.aidas.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ainnotate.aidas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ObjectPropertyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ObjectProperty.class);
        ObjectProperty objectProperty1 = new ObjectProperty();
        objectProperty1.setId(1L);
        ObjectProperty objectProperty2 = new ObjectProperty();
        objectProperty2.setId(objectProperty1.getId());
        assertThat(objectProperty1).isEqualTo(objectProperty2);
        objectProperty2.setId(2L);
        assertThat(objectProperty1).isNotEqualTo(objectProperty2);
        objectProperty1.setId(null);
        assertThat(objectProperty1).isNotEqualTo(objectProperty2);
    }
}

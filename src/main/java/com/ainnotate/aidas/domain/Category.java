package com.ainnotate.aidas.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A AidasOrganisation.
 */
@Entity
@Table(name = "category",indexes = {
    @Index(name="idx_cat_name",columnList = "name")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_category_name",columnNames={"name"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "category")
@Audited
public class Category extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @NotNull
    @Size(min = 3, max = 500)
    @Column(name = "name", length = 500, nullable = true, unique = false)
    private String name;
    @Column(name = "value")
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Category{id="+id+",category_name"+this.getName()+",category_value="+this.getValue()+"}";
    }
}

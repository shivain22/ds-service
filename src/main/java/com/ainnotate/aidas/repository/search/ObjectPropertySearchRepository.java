package com.ainnotate.aidas.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ainnotate.aidas.domain.ObjectProperty;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ObjectProperty} entity.
 */
public interface ObjectPropertySearchRepository
    extends ElasticsearchRepository<ObjectProperty, Long>, ObjectPropertySearchRepositoryInternal {}

interface ObjectPropertySearchRepositoryInternal {
    Page<ObjectProperty> search(String query, Pageable pageable);
}

class ObjectPropertySearchRepositoryInternalImpl implements ObjectPropertySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    ObjectPropertySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<ObjectProperty> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<ObjectProperty> hits = elasticsearchTemplate
            .search(nativeSearchQuery, ObjectProperty.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

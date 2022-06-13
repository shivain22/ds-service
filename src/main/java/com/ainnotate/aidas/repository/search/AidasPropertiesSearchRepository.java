package com.ainnotate.aidas.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ainnotate.aidas.domain.Property;
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
 * Spring Data Elasticsearch repository for the {@link Property} entity.
 */
public interface AidasPropertiesSearchRepository
    extends ElasticsearchRepository<Property, Long>, AidasPropertiesSearchRepositoryInternal {}

interface AidasPropertiesSearchRepositoryInternal {
    Page<Property> search(String query, Pageable pageable);
}

class AidasPropertiesSearchRepositoryInternalImpl implements AidasPropertiesSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    AidasPropertiesSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Property> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<Property> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Property.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

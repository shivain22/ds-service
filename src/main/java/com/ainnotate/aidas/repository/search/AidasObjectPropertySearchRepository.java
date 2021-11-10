package com.ainnotate.aidas.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ainnotate.aidas.domain.AidasObjectProperty;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link AidasObjectProperty} entity.
 */
public interface AidasObjectPropertySearchRepository
    extends ElasticsearchRepository<AidasObjectProperty, Long>, AidasObjectPropertySearchRepositoryInternal {}

interface AidasObjectPropertySearchRepositoryInternal {
    Page<AidasObjectProperty> search(String query, Pageable pageable);
}

class AidasObjectPropertySearchRepositoryInternalImpl implements AidasObjectPropertySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    AidasObjectPropertySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<AidasObjectProperty> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<AidasObjectProperty> hits = elasticsearchTemplate
            .search(nativeSearchQuery, AidasObjectProperty.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

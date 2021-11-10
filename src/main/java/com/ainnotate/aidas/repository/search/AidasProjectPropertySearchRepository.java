package com.ainnotate.aidas.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ainnotate.aidas.domain.AidasProjectProperty;
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
 * Spring Data Elasticsearch repository for the {@link AidasProjectProperty} entity.
 */
public interface AidasProjectPropertySearchRepository
    extends ElasticsearchRepository<AidasProjectProperty, Long>, AidasProjectPropertySearchRepositoryInternal {}

interface AidasProjectPropertySearchRepositoryInternal {
    Page<AidasProjectProperty> search(String query, Pageable pageable);
}

class AidasProjectPropertySearchRepositoryInternalImpl implements AidasProjectPropertySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    AidasProjectPropertySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<AidasProjectProperty> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<AidasProjectProperty> hits = elasticsearchTemplate
            .search(nativeSearchQuery, AidasProjectProperty.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

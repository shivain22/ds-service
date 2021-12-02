package com.ainnotate.aidas.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ainnotate.aidas.domain.AidasUserAidasObjectMapping;
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
 * Spring Data Elasticsearch repository for the {@link AidasUserAidasObjectMapping} entity.
 */
public interface AidasUserAidasObjectMappingSearchRepository
    extends ElasticsearchRepository<AidasUserAidasObjectMapping, Long>, AidasUserAidasObjectMappingSearchRepositoryInternal {}

interface AidasUserAidasObjectMappingSearchRepositoryInternal {
    Page<AidasUserAidasObjectMapping> search(String query, Pageable pageable);
}

class AidasUserAidasObjectMappingSearchRepositoryInternalImpl implements AidasUserAidasObjectMappingSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    AidasUserAidasObjectMappingSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<AidasUserAidasObjectMapping> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<AidasUserAidasObjectMapping> hits = elasticsearchTemplate
            .search(nativeSearchQuery, AidasUserAidasObjectMapping.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

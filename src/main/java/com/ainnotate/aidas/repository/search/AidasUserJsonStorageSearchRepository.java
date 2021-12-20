package com.ainnotate.aidas.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ainnotate.aidas.domain.AidasUserJsonStorage;
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
 * Spring Data Elasticsearch repository for the {@link AidasUserJsonStorage} entity.
 */
public interface AidasUserJsonStorageSearchRepository
    extends ElasticsearchRepository<AidasUserJsonStorage, Long>, AidasUserJsonStorageSearchRepositoryInternal {}

interface AidasUserJsonStorageSearchRepositoryInternal {
    Page<AidasUserJsonStorage> search(String query, Pageable pageable);
}

class AidasUserJsonStorageSearchRepositoryInternalImpl implements AidasUserJsonStorageSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    AidasUserJsonStorageSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<AidasUserJsonStorage> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<AidasUserJsonStorage> hits = elasticsearchTemplate
            .search(nativeSearchQuery, AidasUserJsonStorage.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

package com.ainnotate.aidas.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ainnotate.aidas.domain.UserJsonStorage;
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
 * Spring Data Elasticsearch repository for the {@link UserJsonStorage} entity.
 */
public interface AidasUserJsonStorageSearchRepository
    extends ElasticsearchRepository<UserJsonStorage, Long>, AidasUserJsonStorageSearchRepositoryInternal {}

interface AidasUserJsonStorageSearchRepositoryInternal {
    Page<UserJsonStorage> search(String query, Pageable pageable);
}

class AidasUserJsonStorageSearchRepositoryInternalImpl implements AidasUserJsonStorageSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    AidasUserJsonStorageSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<UserJsonStorage> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<UserJsonStorage> hits = elasticsearchTemplate
            .search(nativeSearchQuery, UserJsonStorage.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

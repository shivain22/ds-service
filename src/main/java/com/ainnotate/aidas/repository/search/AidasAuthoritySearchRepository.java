package com.ainnotate.aidas.repository.search;

import com.ainnotate.aidas.domain.AidasAuthority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Spring Data Elasticsearch repository for the {@link AidasAuthority} entity.
 */
public interface AidasAuthoritySearchRepository
    extends ElasticsearchRepository<AidasAuthority, Long>, AidasAuthoritySearchRepositoryInternal {}

interface AidasAuthoritySearchRepositoryInternal {
    Page<AidasAuthority> search(String query, Pageable pageable);
}

class AidasAuthoritySearchRepositoryInternalImpl implements AidasAuthoritySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    AidasAuthoritySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<AidasAuthority> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<AidasAuthority> hits = elasticsearchTemplate
            .search(nativeSearchQuery, AidasAuthority.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

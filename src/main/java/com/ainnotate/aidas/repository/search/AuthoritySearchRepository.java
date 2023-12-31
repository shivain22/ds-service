package com.ainnotate.aidas.repository.search;

import com.ainnotate.aidas.domain.Authority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Spring Data Elasticsearch repository for the {@link Authority} entity.
 */
public interface AuthoritySearchRepository
    extends ElasticsearchRepository<Authority, Long>, AuthoritySearchRepositoryInternal {}

interface AuthoritySearchRepositoryInternal {
    Page<Authority> search(String query, Pageable pageable);
}

class AuthoritySearchRepositoryInternalImpl implements AuthoritySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    AuthoritySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Authority> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(prefixQuery("name",query));
        nativeSearchQuery.setPageable(pageable);
        List<Authority> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Authority.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

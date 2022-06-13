package com.ainnotate.aidas.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ainnotate.aidas.domain.User;
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
 * Spring Data Elasticsearch repository for the {@link User} entity.
 */
public interface AidasUserSearchRepository extends ElasticsearchRepository<User, Long>, AidasUserSearchRepositoryInternal {}

interface AidasUserSearchRepositoryInternal {
    Page<User> search(String query, Pageable pageable);
}

class AidasUserSearchRepositoryInternalImpl implements AidasUserSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    AidasUserSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<User> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<User> hits = elasticsearchTemplate
            .search(nativeSearchQuery, User.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

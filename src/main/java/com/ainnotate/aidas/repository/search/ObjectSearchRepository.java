package com.ainnotate.aidas.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ainnotate.aidas.domain.Object;
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
 * Spring Data Elasticsearch repository for the {@link Object} entity.
 */
public interface ObjectSearchRepository extends ElasticsearchRepository<Object, Long>, ObjectSearchRepositoryInternal {}

interface ObjectSearchRepositoryInternal {
    Page<Object> search(String query, Pageable pageable);
}

class ObjectSearchRepositoryInternalImpl implements ObjectSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    ObjectSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Object> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(prefixQuery("name",query));
        nativeSearchQuery.setPageable(pageable);
        List<Object> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Object.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

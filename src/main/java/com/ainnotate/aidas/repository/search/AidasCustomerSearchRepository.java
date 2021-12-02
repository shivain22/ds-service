package com.ainnotate.aidas.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ainnotate.aidas.domain.AidasCustomer;
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
 * Spring Data Elasticsearch repository for the {@link AidasCustomer} entity.
 */
public interface AidasCustomerSearchRepository
    extends ElasticsearchRepository<AidasCustomer, Long>, AidasCustomerSearchRepositoryInternal {
}

interface AidasCustomerSearchRepositoryInternal {
    Page<AidasCustomer> search(String query, Pageable pageable);
}

class AidasCustomerSearchRepositoryInternalImpl implements AidasCustomerSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    AidasCustomerSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<AidasCustomer> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<AidasCustomer> hits = elasticsearchTemplate
            .search(nativeSearchQuery, AidasCustomer.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

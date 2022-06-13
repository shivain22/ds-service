package com.ainnotate.aidas.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ainnotate.aidas.domain.UserVendorMappingObjectMapping;
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
 * Spring Data Elasticsearch repository for the {@link UserVendorMappingObjectMapping} entity.
 */
public interface AidasUserAidasObjectMappingSearchRepository
    extends ElasticsearchRepository<UserVendorMappingObjectMapping, Long>, AidasUserAidasObjectMappingSearchRepositoryInternal {}

interface AidasUserAidasObjectMappingSearchRepositoryInternal {
    Page<UserVendorMappingObjectMapping> search(String query, Pageable pageable);
}

class AidasUserAidasObjectMappingSearchRepositoryInternalImpl implements AidasUserAidasObjectMappingSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    AidasUserAidasObjectMappingSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<UserVendorMappingObjectMapping> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<UserVendorMappingObjectMapping> hits = elasticsearchTemplate
            .search(nativeSearchQuery, UserVendorMappingObjectMapping.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

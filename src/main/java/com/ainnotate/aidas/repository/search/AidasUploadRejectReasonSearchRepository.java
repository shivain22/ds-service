package com.ainnotate.aidas.repository.search;

import com.ainnotate.aidas.domain.AidasAuthority;
import com.ainnotate.aidas.domain.AidasUploadRejectReason;
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
public interface AidasUploadRejectReasonSearchRepository
    extends ElasticsearchRepository<AidasUploadRejectReason, Long>, AidasUploadRejectReasonSearchRepositoryInternal {}

interface AidasUploadRejectReasonSearchRepositoryInternal {
    Page<AidasUploadRejectReason> search(String query, Pageable pageable);
}

class AidasUploadRejectReasonSearchRepositoryInternalImpl implements AidasUploadRejectReasonSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    AidasUploadRejectReasonSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<AidasUploadRejectReason> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<AidasUploadRejectReason> hits = elasticsearchTemplate
            .search(nativeSearchQuery, AidasUploadRejectReason.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

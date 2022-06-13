package com.ainnotate.aidas.repository.search;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.UploadRejectReason;
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
 * Spring Data Elasticsearch repository for the {@link Authority} entity.
 */
public interface AidasUploadRejectReasonSearchRepository
    extends ElasticsearchRepository<UploadRejectReason, Long>, AidasUploadRejectReasonSearchRepositoryInternal {}

interface AidasUploadRejectReasonSearchRepositoryInternal {
    Page<UploadRejectReason> search(String query, Pageable pageable);
}

class AidasUploadRejectReasonSearchRepositoryInternalImpl implements AidasUploadRejectReasonSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    AidasUploadRejectReasonSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<UploadRejectReason> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<UploadRejectReason> hits = elasticsearchTemplate
            .search(nativeSearchQuery, UploadRejectReason.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

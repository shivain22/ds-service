package com.ainnotate.aidas.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ainnotate.aidas.domain.Upload;
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
 * Spring Data Elasticsearch repository for the {@link Upload} entity.
 */
public interface AidasUploadSearchRepository extends ElasticsearchRepository<Upload, Long>, AidasUploadSearchRepositoryInternal {}

interface AidasUploadSearchRepositoryInternal {
    Page<Upload> search(String query, Pageable pageable);
}

class AidasUploadSearchRepositoryInternalImpl implements AidasUploadSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    AidasUploadSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Upload> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<Upload> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Upload.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

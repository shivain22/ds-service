package com.ainnotate.aidas.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ainnotate.aidas.domain.UploadMetaData;
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
 * Spring Data Elasticsearch repository for the {@link UploadMetaData} entity.
 */
public interface UploadMetaDataSearchRepository
    extends ElasticsearchRepository<UploadMetaData, Long>, UploadMetaDataSearchRepositoryInternal {}

interface UploadMetaDataSearchRepositoryInternal {
    Page<UploadMetaData> search(String query, Pageable pageable);
}

class UploadMetaDataSearchRepositoryInternalImpl implements UploadMetaDataSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    UploadMetaDataSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<UploadMetaData> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<UploadMetaData> hits = elasticsearchTemplate
            .search(nativeSearchQuery, UploadMetaData.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

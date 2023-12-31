package com.ainnotate.aidas.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;
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
public interface UploadSearchRepository extends ElasticsearchRepository<Upload, Long>, UploadSearchRepositoryInternal {}

interface UploadSearchRepositoryInternal {
    Page<Upload> search(String query, Pageable pageable);
}

class UploadSearchRepositoryInternalImpl implements UploadSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    UploadSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Upload> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(prefixQuery("name",query));
        nativeSearchQuery.setPageable(pageable);
        List<Upload> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Upload.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

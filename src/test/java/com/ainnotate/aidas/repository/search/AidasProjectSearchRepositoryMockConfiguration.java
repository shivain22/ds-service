package com.ainnotate.aidas.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link AidasProjectSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class AidasProjectSearchRepositoryMockConfiguration {

    @MockBean
    private AidasProjectSearchRepository mockAidasProjectSearchRepository;
}

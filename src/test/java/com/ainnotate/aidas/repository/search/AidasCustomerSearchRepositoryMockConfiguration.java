package com.ainnotate.aidas.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link AidasCustomerSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class AidasCustomerSearchRepositoryMockConfiguration {

    @MockBean
    private AidasCustomerSearchRepository mockAidasCustomerSearchRepository;
}

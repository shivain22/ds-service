package com.ainnotate.aidas.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link VendorSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class VendorSearchRepositoryMockConfiguration {

    @MockBean
    private VendorSearchRepository mockVendorSearchRepository;
}

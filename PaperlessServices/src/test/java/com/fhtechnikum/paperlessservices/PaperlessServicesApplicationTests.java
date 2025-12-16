package com.fhtechnikum.paperlessservices;

import com.fhtechnikum.paperlessservices.persistence.repository.DocumentRepository;
import com.fhtechnikum.paperlessservices.persistence.repository.ElasticSearchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class PaperlessServicesApplicationTests {

    // 1. Mock Postgres (So it doesn't crash if DB is missing)
    @MockitoBean
    private DocumentRepository documentRepository;

    // 2. Mock Elasticsearch (So it doesn't crash looking for Port 9200)
    @MockitoBean
    private ElasticSearchRepository elasticSearchRepository;

    @Test
    void contextLoads() {
    }

}

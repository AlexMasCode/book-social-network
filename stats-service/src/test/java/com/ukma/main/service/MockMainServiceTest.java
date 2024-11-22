package com.ukma.main.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.ukma.main.service.download.book.stats.DownloadBookRecordClient;
import com.ukma.main.service.download.book.stats.DownloadRecord;
import feign.FeignException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;


import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"eureka.client.enabled=false",
        "spring.profiles.active=test",
        "spring.cloud.openfeign.client.config.main-service.url=http://localhost:8072"})
class MockMainServiceTest {
    @Autowired
    private DownloadBookRecordClient downloadBookRecordClient;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setUpWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8072));
        wireMockServer.start();

        //звичайний запит
        wireMockServer.stubFor(get(urlEqualTo("/api/download-records/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\":1,\"bookId\":1,\"downloadTime\":\"2024-11-20T00:00:00Z\",\"userId\":\"user1\"}]")));

        //запит 404
        wireMockServer.stubFor(get(urlEqualTo("/api/download-records/999"))
                .willReturn(aResponse().withStatus(404)));

        //запит з затримкою
        wireMockServer.stubFor(get(urlEqualTo("/api/download-records/2"))
                .willReturn(aResponse()
                        .withFixedDelay(3000)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\":2,\"bookId\":2,\"downloadTime\":\"2024-11-20T00:00:00Z\",\"userId\":\"user2\"}]")));
    }

    @AfterAll
    static void tearDownWireMock() {
        wireMockServer.stop();
    }



    @Test
    void testFindAllDownloadRecords() {
        List<DownloadRecord> records = downloadBookRecordClient.findAll(1L);

        assertThat(records).isNotEmpty();
        assertThat(records.get(0).getBookId()).isEqualTo(1L);
        assertThat(records.get(0).getUserId()).isEqualTo("user1");

        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/download-records/1")));
    }


    @Test
    void testNotFound() {
        Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() ->
                downloadBookRecordClient.findAll(999L)
        );

        assertThat(exception).isInstanceOf(FeignException.NotFound.class);
    }

    @Test
    void testTimeout() {
        downloadBookRecordClient.findAll(2L);

        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/download-records/2")));
    }
}
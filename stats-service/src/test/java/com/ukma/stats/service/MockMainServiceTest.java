package com.ukma.stats.service;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.ukma.stats.service.config.ClockTestConfiguration;
import com.ukma.stats.service.download.book.stats.DownloadBookRecordClient;
import com.ukma.stats.service.download.book.stats.DownloadBookStatsService;
import com.ukma.stats.service.download.book.stats.DownloadRecord;
import com.ukma.stats.service.download.book.stats.dto.DownloadBookStatsDto;
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
@Import(ClockTestConfiguration.class)
class MockMainServiceTest {
    @Autowired
    private DownloadBookRecordClient downloadBookRecordClient;

    @Autowired
    private DownloadBookStatsService downloadBookStatsService;

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

        //запит з підрахунком кількості завантажень
        wireMockServer.stubFor(get(urlEqualTo("/api/download-records/3"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("["
                                + "{\"id\":1,\"bookId\":3,\"downloadTime\":\"2024-11-21T12:00:00Z\",\"userId\":\"user1\"},"
                                + "{\"id\":2,\"bookId\":3,\"downloadTime\":\"2024-11-20T12:00:00Z\",\"userId\":\"user2\"},"
                                + "{\"id\":3,\"bookId\":3,\"downloadTime\":\"2024-11-20T18:00:00Z\",\"userId\":\"user3\"}"
                                + "]")));
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


    @Test
    void testCountBookDownloadRecordsForLastSevenDays() {
        List<DownloadBookStatsDto> stats = downloadBookStatsService.countBookDownloadRecordsForLastSevenDays(3L);

        assertThat(stats).hasSize(7);

        assertThat(stats.get(6).getDownloadCount()).isEqualTo(0);
        assertThat(stats.get(5).getDownloadCount()).isEqualTo(1);
        assertThat(stats.get(4).getDownloadCount()).isEqualTo(2);

        assertThat(stats.get(4).getDate()).isEqualTo("20.11.2024");

        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/download-records/3")));
    }
}
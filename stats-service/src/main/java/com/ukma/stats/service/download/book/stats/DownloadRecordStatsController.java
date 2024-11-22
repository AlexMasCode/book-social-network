package com.ukma.stats.service.download.book.stats;

import com.ukma.main.service.download.book.stats.dto.DownloadBookStatsDto;
import com.ukma.stats.service.download.book.stats.DownloadBookStatsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/book-download-records-stats")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DownloadRecordStatsController {

    DownloadBookStatsService downloadBookStatsService;
    com.ukma.main.service.download.book.stats.ExcelDownloadBookStatsService excelDownloadBookStatsService;

    @GetMapping("/{bookId}")
    public List<DownloadBookStatsDto> countBookDownloadRecordsForLastSevenDays(@PathVariable Long bookId) {
        return downloadBookStatsService.countBookDownloadRecordsForLastSevenDays(bookId);
    }

    @GetMapping("/{bookId}/excel")
    public void countBookDownloadRecordsForLastSevenDaysAsExcel(@PathVariable Long bookId, HttpServletResponse servletResponse) throws IOException {
        XSSFWorkbook workbook = excelDownloadBookStatsService.generateStatsExcelWithChart(bookId);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        byte[] byteArray = bos.toByteArray();
        InputStream is = new ByteArrayInputStream(byteArray);

        is.transferTo(servletResponse.getOutputStream());

        servletResponse.setHeader("Content-Disposition", "attachment; filename=book-report-" + bookId + ".xlsx");
        servletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }
}

package com.ukma.stats.service.download.book.stats;

import com.ukma.main.service.download.book.stats.dto.DownloadBookStatsDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFLineChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ExcelDownloadBookStatsService {

    DownloadBookStatsService downloadBookStatsService;

    public XSSFWorkbook generateStatsExcelWithChart(Long bookId) {
        List<DownloadBookStatsDto> downloadBookStats = downloadBookStatsService.countBookDownloadRecordsForLastSevenDays(bookId);

        XSSFWorkbook book = getRows(bookId, downloadBookStats);
        XSSFSheet sheet = book.getSheet("stats-report-book-" + bookId);
        createChart(sheet, downloadBookStats.size());

        return book;
    }

    private void createChart(XSSFSheet sheet, int length) {
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5, 10, 15);
        XSSFChart chart = drawing.createChart(anchor);

        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("x");

        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Downloads of a book last week");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        XDDFDataSource<Double> xs = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(0, 0, 0, length - 1));
        XDDFNumericalDataSource<Double> ys1 = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, 1, 0, length - 1));
        XDDFNumericalDataSource<Double> ys2 = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(2, 2, 0, length - 1));
        XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
        XDDFLineChartData.Series series1 = (XDDFLineChartData.Series) data.addSeries(xs, ys1);
        series1.setTitle("2x", null);
        chart.plot(data);
    }

    private XSSFWorkbook getRows(Long bookId, List<DownloadBookStatsDto> downloadBookStats) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("stats-report-book-" + bookId);

        Row row;
        Cell cell;

        row = sheet.createRow((short) 0);
        for (int collIndex = 0; collIndex < downloadBookStats.size(); collIndex++) {
            cell = row.createCell((short) collIndex);
            cell.setCellValue(downloadBookStats.get(collIndex).getDate());
        }

        row = sheet.createRow((short) 1);
        for (int collIndex = 0; collIndex < downloadBookStats.size(); collIndex++) {
            cell = row.createCell((short) collIndex);
            cell.setCellValue(downloadBookStats.get(collIndex).getDownloadCount());
        }

        return wb;
    }
}

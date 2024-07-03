package element.binder.plugin.backend.service;

import element.binder.plugin.backend.entity.Element;
import element.binder.plugin.backend.entity.InnerProject;
import element.binder.plugin.backend.entity.Project;
import element.binder.plugin.backend.exception.ExcelReportException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {

    @SuppressWarnings({"MagicNumber", "OneStatementPerLine"})
    public byte[] generateExcel(List<Element> elements) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Отчет");

            // Создаем стиль для заголовков
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Создаем строку заголовков
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Name", "Article", "Size", "Material Name", "Price", "Inner Project Name", "Project Name"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Заполняем таблицу данными
            int rowNum = 1;
            for (Element element : elements) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(element.getName() != null ? element.getName() : "");
                row.createCell(1).setCellValue(element.getArticle() != null ? element.getArticle() : "");
                row.createCell(2).setCellValue(element.getSize() != null ? element.getSize() : "");
                row.createCell(3).setCellValue(element.getMaterialName() != null ? element.getMaterialName() : "");
                row.createCell(4).setCellValue(element.getPrice() != null ? element.getPrice().toString() : "");

                InnerProject innerProject = element.getInnerProject();
                if (innerProject != null) {
                    row.createCell(5).setCellValue(innerProject.getName() != null ? innerProject.getName() : "");
                    Project project = innerProject.getProject();
                    if (project != null) {
                        row.createCell(6).setCellValue(project.getName() != null ? project.getName() : "");
                    } else {
                        row.createCell(6).setCellValue("");
                    }
                } else {
                    row.createCell(5).setCellValue("");
                    row.createCell(6).setCellValue("");
                }
            }

            // Автоматически подгоняем ширину колонок под содержимое
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new ExcelReportException("Failed to generate Excel report", e);
        }
    }
}

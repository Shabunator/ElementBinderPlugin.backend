package element.binder.plugin.backend.service;

import element.binder.plugin.backend.entity.Element;
import element.binder.plugin.backend.entity.InnerProject;
import element.binder.plugin.backend.exception.ExcelReportException;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Service
public class ExcelService {

    @SuppressWarnings({"MagicNumber", "OneStatementPerLine"})
    public byte[] generateExcel(List<Element> elements) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName("Отчет"));

            // Создание стиля для заголовков
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Создание строки заголовков
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Изображение", "Название", "Артикул", "Размер", "Материал", "Цена", "Внутренний проект"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Заполнение таблицы данными
            int rowNum = 1;
            for (Element element : elements) {
                Row row = sheet.createRow(rowNum++);

                // Вставка изображения
                if (element.getMinioUrl() != null) {
                    try (InputStream webpInputStream = new URL(element.getMinioUrl()).openStream()) {
                        insertImageIntoExcel(workbook, sheet, webpInputStream, rowNum - 1, 0);
                    } catch (IOException e) {
                        System.err.println("Ошибка загрузки изображения: " + e.getMessage());
                    }
                }

                row.createCell(1).setCellValue(element.getName() != null ? element.getName() : "");
                row.createCell(2).setCellValue(element.getArticle() != null ? element.getArticle() : "");
                row.createCell(3).setCellValue(element.getSize() != null ? element.getSize() : "");
                row.createCell(4).setCellValue(element.getMaterialName() != null ? element.getMaterialName() : "");
                row.createCell(5).setCellValue(element.getPrice() != null ? element.getPrice().toString() : "");

                InnerProject innerProject = element.getInnerProject();
                if (innerProject != null) {
                    row.createCell(6).setCellValue(innerProject.getName() != null ? innerProject.getName() : "");
                } else {
                    row.createCell(6).setCellValue("");
                }
            }

            // Автоматическое изменение размера столбцов
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new ExcelReportException("Не удалось создать Excel отчет", e);
        }
    }

    // Метод для чтения WebP изображения
    private BufferedImage readWebP(InputStream inputStream) throws IOException {
        return ImageIO.read(inputStream);
    }

    // Метод для вставки изображения в Excel
    @SuppressWarnings("MagicNumber")
    private void insertImageIntoExcel(Workbook workbook, Sheet sheet, InputStream webpInputStream, int row, int col) throws IOException {
        BufferedImage originalImage = readWebP(webpInputStream);

        // Уменьшение размера изображения до 6%
        BufferedImage resizedImage = Thumbnails.of(originalImage)
                .scale(0.06)
                .asBufferedImage();

        // Преобразование BufferedImage в byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        // Вставка изображения в Excel
        int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
        CreationHelper helper = workbook.getCreationHelper();
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = helper.createClientAnchor();

        // Установка позиции изображения
        anchor.setCol1(col);
        anchor.setRow1(row);

        Picture picture = drawing.createPicture(anchor, pictureIdx);
        picture.resize(); // Автоматическое изменение размера ячейки под изображение
    }
}
package element.binder.plugin.backend.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import element.binder.plugin.backend.entity.Element;
import element.binder.plugin.backend.entity.InnerProject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@SuppressWarnings("MagicNumber")
public class PdfService {

    public byte[] generatePdf(List<Element> elements) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Добавляем контент в PDF
        document.add(new Paragraph("Отчет"));

        // Заголовки таблицы
        String[] headers = {"Name", "Article", "Size", "Material Name", "Price", "Inner Project"};

        // Вычисляем ширину столбцов на основе заголовков
        float[] columnWidths = calculateColumnWidths(headers);

        // Создаем таблицу с вычисленными ширинами колонок
        Table table = new Table(UnitValue.createPointArray(columnWidths));

        // Добавляем заголовки таблицы
        for (String header : headers) {
            Cell headerCell = new Cell().add(new Paragraph(header));
            headerCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            table.addHeaderCell(headerCell);
        }

        // Заполняем таблицу данными
        for (Element element : elements) {
            table.addCell(element.getName());
            table.addCell(element.getArticle());
            table.addCell(element.getSize());
            table.addCell(element.getMaterialName());
            table.addCell(element.getPrice().toString());

            // Получаем InnerProject и Project
            InnerProject innerProject = element.getInnerProject();

            // Добавляем данные о внутреннем проекте и проекте
            table.addCell(innerProject.getName());
        }

        // Добавляем таблицу в документ
        document.add(table);

        // Закрываем документ
        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    private float[] calculateColumnWidths(String[] headers) {
        float[] widths = new float[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length() * 8;
        }
        return widths;
    }
}

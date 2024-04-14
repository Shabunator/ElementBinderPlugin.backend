package element.binder.plugin.backend.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import element.binder.plugin.backend.entity.Element;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    public byte[] generatePdf(List<Element> elements) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Добавляем контент в PDF
        document.add(new Paragraph("Отчет по элементам"));

        // Создаем таблицу с колонками соответствующими вашей сущности Element
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 3, 3, 3, 3, 3})).useAllAvailableWidth();

        // Добавляем заголовки таблицы
        table.addHeaderCell("ID");
        table.addHeaderCell("Name");
        table.addHeaderCell("Article");
        table.addHeaderCell("Size");
        table.addHeaderCell("Material Name");
        table.addHeaderCell("Price");

        // Заполняем таблицу данными
        for (Element element : elements) {
            table.addCell(element.getId().toString());
            table.addCell(element.getName());
            table.addCell(element.getArticle());
            table.addCell(element.getSize());
            table.addCell(element.getMaterialName());
            table.addCell(element.getPrice().toString());
        }

        // Добавляем таблицу в документ
        document.add(table);

        // Закрываем документ
        document.close();

        return byteArrayOutputStream.toByteArray();

    }
}

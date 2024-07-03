package element.binder.plugin.backend.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import element.binder.plugin.backend.entity.Element;
import element.binder.plugin.backend.entity.InnerProject;
import element.binder.plugin.backend.entity.Project;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    @SuppressWarnings("MagicNumber")
    public byte[] generatePdf(List<Element> elements) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Добавляем контент в PDF
        document.add(new Paragraph("Отчет"));

        // Создаем таблицу с колонками соответствующими вашей сущности Element
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 3, 3, 3, 3, 3, 3})).useAllAvailableWidth();

        // Добавляем заголовки таблицы
        table.addHeaderCell("Name");
        table.addHeaderCell("Article");
        table.addHeaderCell("Size");
        table.addHeaderCell("Material Name");
        table.addHeaderCell("Price");
        table.addHeaderCell("Inner Project Name");
        table.addHeaderCell("Project Name");

        // Заполняем таблицу данными
        for (Element element : elements) {
            table.addCell(element.getName());
            table.addCell(element.getArticle());
            table.addCell(element.getSize());
            table.addCell(element.getMaterialName());
            table.addCell(element.getPrice().toString());

            // Получаем InnerProject и Project
            InnerProject innerProject = element.getInnerProject();
            Project project = innerProject.getProject();

            // Добавляем данные о внутреннем проекте и проекте
            table.addCell(innerProject.getName());
            table.addCell(project.getName());
        }

        // Добавляем таблицу в документ
        document.add(table);

        // Закрываем документ
        document.close();

        return byteArrayOutputStream.toByteArray();

    }
}

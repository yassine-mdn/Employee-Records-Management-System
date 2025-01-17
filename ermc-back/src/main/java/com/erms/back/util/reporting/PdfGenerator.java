package com.erms.back.util.reporting;


import com.erms.back.model.Employee;
import com.erms.back.service.EmployeeService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PdfGenerator {

    @Value("${pdf.directory}")
    private String directory;

    @Value("${pdf.file-name}")
    private String fileName;

    @Value("${pdf.company-logo}")
    private String companyLogoPath;


    PdfFont font;

    {
        try {
            font = PdfFontFactory.createFont(StandardFonts.COURIER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    PdfFont bold;

    {
        try {
            bold = PdfFontFactory.createFont(StandardFonts.COURIER_BOLD);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final EmployeeService employeeService;

    public void generatePdf() throws IOException {
        //TODO : change to relative path
        String destination = getPdfNameWithDate();
        PdfDocument pdf = new PdfDocument(new PdfWriter(destination));
        Document document = new Document(pdf);
        addLogo(document);
        addDocTitle(document);
        addTable(document,pdf);
        document.close();
    }

    private void addLogo(Document document) {
        try {
            Image img = new Image(ImageDataFactory.create(companyLogoPath));
            img.scaleToFit(5000, 80);
            document.add(img);
        } catch (PdfException | IOException e) {
            log.error(e.getMessage());
        }
    }

    private void addDocTitle(Document document) throws PdfException {
        String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss"));
        Paragraph p1 = new Paragraph();
        p1.add(new Paragraph(fileName).setFont(bold));
        p1.add(new Text("\n"));
        p1.add(new Paragraph("Report generated on " + localDateString).setFont(font));

        document.add(p1);

    }

    private void addTable(Document document, PdfDocument pdfDocument) {

        List<Employee> recentEmployees = employeeService.getLastMonthsEmployees();

        Style headerStyle = new Style()
                .setBackgroundColor(new DeviceRgb(105, 196, 215))
                .setFont(bold);

        Table table = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
        table.addHeaderCell(new Paragraph().addStyle(headerStyle).add("Full Name"));
        table.addHeaderCell(new Paragraph().addStyle(headerStyle).add("Job Title"));
        table.addHeaderCell(new Paragraph().addStyle(headerStyle).add("Department"));
        table.addHeaderCell(new Paragraph().addStyle(headerStyle).add("Hire Date"));

        if (recentEmployees.isEmpty()) {
            document.add(new Paragraph().add("No data"));
            return;
        }

        for (Employee employee : recentEmployees) {
            table.addCell(employee.getFullName());
            table.addCell(employee.getJobTitle());
            table.addCell(employee.getDepartment().getDisplayName());
            table.addCell(employee.getHireDate().toString());
        }


        document.add(new Paragraph().add("List Of Employees added this past month"));
        document.add(table);
    }


    private String getPdfNameWithDate() {
        String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMMM-yyyy"));
        return fileName+"-"+localDateString+".pdf";
    }

}

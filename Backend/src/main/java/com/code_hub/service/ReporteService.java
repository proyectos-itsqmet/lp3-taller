package com.code_hub.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code_hub.model.Empleado;
import com.code_hub.model.Proyecto;
import com.code_hub.repository.EmpleadoRepository;
import com.code_hub.repository.ProyectoRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class ReporteService {

    private final ProyectoRepository proyectoRepository;
    private final EmpleadoRepository empleadoRepository;

    public ReporteService(ProyectoRepository proyectoRepository,
                          EmpleadoRepository empleadoRepository) {
        this.proyectoRepository = proyectoRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteProyectos() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            document.add(new Paragraph("CodeHub - Reporte de Proyectos", titulo));
            document.add(new Paragraph("Generado: " + LocalDate.now()));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(new float[]{3, 3, 2, 2, 2, 2});
            table.setWidthPercentage(100);

            table.addCell(headerCell("Proyecto"));
            table.addCell(headerCell("Cliente"));
            table.addCell(headerCell("Estado"));
            table.addCell(headerCell("Inicio"));
            table.addCell(headerCell("Fin"));
            table.addCell(headerCell("Presupuesto"));

            for (Proyecto p : proyectoRepository.findAll()) {
                table.addCell(p.getNombre());
                table.addCell(p.getCliente() != null ? p.getCliente().getNombre() : "-");
                table.addCell(p.getEstado());
                table.addCell(String.valueOf(p.getFechaInicio()));
                table.addCell(p.getFechaFin() != null ? String.valueOf(p.getFechaFin()) : "-");
                table.addCell(String.valueOf(p.getPresupuesto()));
            }

            document.add(table);
            document.close();
            return baos.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generando el reporte PDF", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteEmpleados() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            document.add(new Paragraph("CodeHub - Reporte de Empleados", titulo));
            document.add(new Paragraph("Generado: " + LocalDate.now()));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(new float[]{3, 3, 3, 2});
            table.setWidthPercentage(100);

            table.addCell(headerCell("Empleado"));
            table.addCell(headerCell("Cargo"));
            table.addCell(headerCell("Email"));
            table.addCell(headerCell("#Proyectos"));

            for (Empleado e : empleadoRepository.findAll()) {
                table.addCell(e.getNombre() + " " + e.getApellido());
                table.addCell(e.getPerfil() != null ? e.getPerfil().getCargo() : "-");
                table.addCell(e.getEmail());
                table.addCell(String.valueOf(e.getProyectos().size()));
            }

            document.add(table);
            document.close();
            return baos.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generando el reporte PDF", e);
        }
    }

    private PdfPCell headerCell(String texto) {
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setBackgroundColor(new Color(52, 73, 94));
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        return celda;
    }
}

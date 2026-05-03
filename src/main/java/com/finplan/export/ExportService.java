package com.finplan.export;

import com.finplan.transaccion.dto.TransaccionResponse;
import com.finplan.transaccion.service.TransaccionService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final TransaccionService transaccionService;

    public byte[] generarExcel(String email, int anio, int mes) {
        List<TransaccionResponse> transacciones = transaccionService.listarPorMes(email, anio, mes);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Transacciones");

            // Encabezados
            Row header = sheet.createRow(0);
            String[] cols = {"Fecha", "Categoría", "Descripción", "Tipo", "Monto"};
            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }

            // Filas de datos
            int rowNum = 1;
            for (TransaccionResponse t : transacciones) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(t.getFecha() != null ? t.getFecha().toString() : "");
                row.createCell(1).setCellValue(t.getCategoriaNombre());
                row.createCell(2).setCellValue(t.getDescripcion() != null ? t.getDescripcion() : "");
                row.createCell(3).setCellValue(t.getTipo());
                row.createCell(4).setCellValue(t.getMonto().doubleValue());
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel", e);
        }
    }
}

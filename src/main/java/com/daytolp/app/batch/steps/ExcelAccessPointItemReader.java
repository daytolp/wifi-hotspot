package com.daytolp.app.batch.steps;

import com.daytolp.app.models.AccessPoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.batch.item.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;

@Slf4j
public class ExcelAccessPointItemReader implements ItemStreamReader<AccessPoint> {
    private final String filePath;
    private InputStream inputStream;
    private Sheet sheet = null;
    Workbook workbook = null;
    private Iterator<Row> rows;
    private boolean headerSkipped = false;

    public ExcelAccessPointItemReader(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public AccessPoint read() {
        if (rows == null) return null;

        // Saltar encabezado
        if (!headerSkipped && rows.hasNext()) {
            rows.next();
            headerSkipped = true;
        }

        while (rows.hasNext()) {
            Row row = rows.next();
            try {
                return AccessPoint.builder()
                        .id(getCellValueAsString(row.getCell(0)))
                        .program(getCellValueAsString(row.getCell(1)))
                        .latitude(getCellValueAsDouble(row.getCell(2)))
                        .longitude(getCellValueAsDouble(row.getCell(3)))
                        .municipality(getCellValueAsString(row.getCell(4)))
                        .build();
            } catch (Exception ex) {
                continue;
            }
        }
        return null; // fin del archivo
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (filePath == null || filePath.isEmpty()) return;
        try {
            inputStream = new FileInputStream(filePath);
            workbook = WorkbookFactory.create(inputStream);
            sheet = workbook.getSheetAt(0);
            this.rows = sheet.iterator();
            this.headerSkipped = false;
        } catch (IOException e) {
            throw new ItemStreamException("No se pudo abrir el Excel: " + filePath, e);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
    }


    @Override
    public void close() throws ItemStreamException {
        try {
            if (workbook != null) workbook.close();
        } catch (Exception ignored) {}

        try {
            if (inputStream != null) inputStream.close();
        } catch (Exception ignored) {}
    }

    /**
     * Convierte el valor de una celda de Excel a Double, manejando diferentes formatos de datos.
     * @param cell Celda de Excel a convertir. Puede ser null.
     * @return Valor Double extraído de la celda, o null.
     */
    private Double getCellValueAsDouble(Cell cell) {
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }

        if (cell.getCellType() == CellType.STRING) {
            String value = cell.getStringCellValue()
                    .trim()
                    .replaceAll("[^0-9.\\-]", "");

            if (value.isEmpty()) return null;

            try {
                return new BigDecimal(value).doubleValue();
            } catch (NumberFormatException e) {
                throw new RuntimeException("No se pudo parsear el valor: " + cell.getStringCellValue(), e);
            }
        }

        return null;
    }


    /**
     * Convierte el valor de una celda de Excel a String, manejando diferentes tipos de datos.
     *
     * @param cell Celda de Excel a convertir.
     * @return Valor String extraído de la celda, o String vacío.
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return new BigDecimal(cell.getNumericCellValue()).toPlainString();
        }

        return "";
    }

}

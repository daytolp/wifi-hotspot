//package com.daytolp.app.excel;
//
//import com.daytolp.app.models.AccessPoint;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.CellType;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.LinkedList;
//import java.util.List;
//
///**
// * Lector especializado para procesar archivos Excel (.xlsx) que contienen información de puntos de acceso WiFi.
// */
//@Component
//public class ExcelReader {
//
//    /**
//     * Lee un archivo Excel y extrae todos los puntos de acceso WiFi contenidos en él.
//     *
//     * @param sheet hoja del archivo Excel (.xlsx) a procesar.
//     * @return Lista de objetos AccessPoint extraídos del archivo.
//     */
//    public List<AccessPoint> readExcelFile(Sheet sheet) {
//        List<AccessPoint> accessPoints = new LinkedList<>();
//
//        sheet.forEach(row -> {
//            if (row.getRowNum() == 0) return;
//            AccessPoint accessPoint = AccessPoint.builder()
//                    .id(getCellValueAsString(row.getCell(0)))
//                    .program(getCellValueAsString(row.getCell(1)))
//                    .latitude(getCellValueAsDouble(row.getCell(2)))
//                    .longitude(getCellValueAsDouble(row.getCell(3)))
//                    .municipality(getCellValueAsString(row.getCell(4)))
//                    .build();
//            accessPoints.add(accessPoint);
//        });
//
//        return accessPoints;
//    }
//
//    /**
//     * Convierte el valor de una celda de Excel a Double, manejando diferentes formatos de datos.
//     * @param cell Celda de Excel a convertir. Puede ser null.
//     * @return Valor Double extraído de la celda, o null.
//     */
//    private Double getCellValueAsDouble(Cell cell) {
//        if (cell == null) return null;
//
//        if (cell.getCellType() == CellType.NUMERIC) {
//            return cell.getNumericCellValue();
//        }
//
//        if (cell.getCellType() == CellType.STRING) {
//            String value = cell.getStringCellValue()
//                    .trim()
//                    .replaceAll("[^0-9.\\-]", "");
//
//            if (value.isEmpty()) return null;
//
//            try {
//                return new BigDecimal(value).doubleValue();
//            } catch (NumberFormatException e) {
//                throw new RuntimeException("No se pudo parsear el valor: " + cell.getStringCellValue(), e);
//            }
//        }
//
//        return null;
//    }
//
//
//    /**
//     * Convierte el valor de una celda de Excel a String, manejando diferentes tipos de datos.
//     *
//     * @param cell Celda de Excel a convertir.
//     * @return Valor String extraído de la celda, o String vacío.
//     */
//    private String getCellValueAsString(Cell cell) {
//        if (cell == null) return "";
//
//        if (cell.getCellType() == CellType.STRING) {
//            return cell.getStringCellValue().trim();
//        }
//
//        if (cell.getCellType() == CellType.NUMERIC) {
//            return new BigDecimal(cell.getNumericCellValue()).toPlainString();
//        }
//
//        return "";
//    }
//
//
//}

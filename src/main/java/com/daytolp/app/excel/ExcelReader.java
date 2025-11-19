package com.daytolp.app.excel;

import com.daytolp.app.models.AccessPoint;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

@Component
public class ExcelReader {

    public List<AccessPoint> readExcelFile(InputStream file) {
        List<AccessPoint> accessPoints = new LinkedList<>();
        try {
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            sheet.forEach(row -> {
                if (row.getRowNum() == 0) return;
                AccessPoint accessPoint = AccessPoint.builder()
                        .id(row.getCell(0).getStringCellValue())
                        .program(row.getCell(1).getStringCellValue())
                        .latitude(row.getCell(2).getNumericCellValue())
                        .longitude(row.getCell(3).getNumericCellValue())
                        .municipality(row.getCell(4).getStringCellValue())
                        .build();
                accessPoints.add(accessPoint);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return accessPoints;
    }

}

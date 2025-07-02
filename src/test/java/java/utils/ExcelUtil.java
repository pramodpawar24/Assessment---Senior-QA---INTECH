package java.utils;

import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ExcelUtil {
    public static Map<String, String> readTestData(String filePath) {
        Map<String, String> data = new HashMap<>();
        try {
            Workbook workbook = WorkbookFactory.create(new File(filePath));
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Cell key = row.getCell(0);
                Cell value = row.getCell(1);
                if (key != null && value != null)
                    data.put(key.getStringCellValue().trim(), value.getStringCellValue().trim());
            }
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}

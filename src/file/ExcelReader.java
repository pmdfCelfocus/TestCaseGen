package file;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class ExcelReader {

    public static  Map<String , String> parseExcel(byte[] data) throws Exception {
        try {
           // FileInputStream file = new FileInputStream(ByteArrayToFile.insertDataToExcel("excel.xlsx", data));
            FileUtils.writeByteArrayToFile(new File("excel"), data);
            Thread.sleep(5000);
            FileInputStream file = new FileInputStream(new File("excel.xlsx"));
            Map<String , String> tests = new TreeMap<>();
            Cell[] pair = new Cell[2];
            int counter = 0;
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if(cell.getStringCellValue().isEmpty())
                        break;
                    pair[counter++] = cell;
                }
                tests.put(pair[0].getStringCellValue(), pair[1].getStringCellValue());
                counter = 0;
            }
           // file.close();
            return tests;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }


    }
}

package file;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/**
 * @author Pedro Feiteira, n48119, NB24217
 * This is class reads an excel file with two colmuns, the test case name and description
 */
public class ExcelReader {

    /**
     * Load the file, read it and transforms the data into a map with key test name and value description
     * @param path to the excel file
     * @return map with the received information
     */
    public static  Map<String , String> parseExcel(String path) {
        try {
            FileInputStream file = new FileInputStream(new File(path));
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
           file.close();
            return tests;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }


    }
}

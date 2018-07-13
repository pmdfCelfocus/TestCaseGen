package file;

import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.Diagram;
import utils.ResultObj;
import utils.Steps;
import java.io.FileOutputStream;

public class ExcelCreator {

    private static String[] columns = {"Name", "Description", "Step", "Step name", "Expected result"};

    public static String createExcel(byte[] data) {

        String json = processJSON(getBody(data));
        Gson g = new Gson();
        ResultObj result = g.fromJson(json, ResultObj.class);
        return writeExcel(result);
    }

    private static String getBody(byte[] data) {
        String[] split = new String(data).split("\n");
        return split[4];
    }

    private static String writeExcel(ResultObj result) {
        //TODO -> Missing row problem
        Workbook workbook = new XSSFWorkbook();
        CreationHelper creationHelper = workbook.getCreationHelper();

        Sheet sheet = workbook.createSheet("Test Cases");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.DARK_BLUE.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        CellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int firstRow = 1;
        int lastRow = 1;
        for (Diagram d : result.getDiagrams()) {
            String[] split = d.getDesc().split(":");
            int stepCount = 0;
            for (Steps step : d.getNodes()) {
                Row row = sheet.createRow(lastRow++);
                row.createCell(2).setCellValue(stepCount++);
                if (step.getName().toLowerCase().equals("init")) {
                    row.createCell(3).setCellValue("Assumptions & Conditions:");
                    row.createCell(4).setCellValue(split[1]);
                } else {
                    row.createCell(3).setCellValue(step.getName().toLowerCase());
                    if (step.getName().toLowerCase().equals("end")) {
                        row.createCell(4).setCellValue("end");
                    } else {
                        row.createCell(4).setCellValue("Success");
                    }
                }
            }
            Row row = sheet.getRow(firstRow);
            sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow - 1, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow - 1, 1, 1));
            row.createCell(0).setCellValue(d.getName());
            row.createCell(1).setCellValue(split[0]);
            firstRow = lastRow;
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        String path = "D:\\Estagio\\TestCases" + Math.random() + ".xlsx";
        try {

            FileOutputStream fileOutputStream = new FileOutputStream(path);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;

    }


    private static String processJSON(String json) {
        json = json.replace("\\", "");
        json = json.replace("\"{", "{");
        json = json.replace("}\"", "}");
        return json;
    }


}

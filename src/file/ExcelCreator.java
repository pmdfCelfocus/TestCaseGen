package file;

import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.FileHandling;
import utils.ObjectParse.Diagram;
import utils.ObjectParse.MultipleDiagrams;
import utils.ObjectParse.SingleDiagram;
import utils.ObjectParse.Steps;

import java.io.FileOutputStream;

/**
 * @author Pedro Feiteira, n48119
 *
 * This class is used to create an Excel File with the information receives from the diagrams JSON
 * The formdata file is written in the local folder and then loaded as a byte array
 * When loaded, the JSON is extracted from this file and it is transformed to an Object, using GSON
 * Using Apache POI, a workbook is created and saved to a file
 * The path to the written file is returned
 */
public class ExcelCreator {

    private static final String TEST_CONST = "Test Cases";
    private static final String INITIAL_NODE = "init";
    private static final String END_NODE = "end";
    private static final String ASSUMPTIONS_AND_CONDITIONS = "Assumptions & Conditions:";
    private static final String TEST_NAME = "TestCases.xlsx";

    private static String[] columns = {"Name", "Description", "Step", "Step name", "Expected result"};

    /**
     * @param data, the form data as byte array
     *
     * Get the JSON file and transforms it into an Object. If JSON has only one diagram to convert, the array object
     * does not work. So the when it needs, the Single Diagram Class is used.
     * This object will be used to create an Excel file. This file will be written into the path
     * @return path
     */
    public static String createExcel(byte[] data) {
        MultipleDiagrams result = null;
        SingleDiagram diagram = null;
        String json = processJSON(getBody(data));
        Gson g = new Gson();
        try {
            result = g.fromJson(json, MultipleDiagrams.class);
            return writeExcel(result);
        } catch (Exception e) {
            diagram = g.fromJson(json, SingleDiagram.class);
            return writeExcel(diagram);
        }

    }

    /**
     * The form data has a constant structure and when it is splited, the array position 4 corresponds to the data that
     * we need
     * @param data
     * @return body from form data message
     */
    private static String getBody(byte[] data) {
        String[] split = new String(data).split("\n");
        return split[4];
    }

    /**
     * Write an Excel using Apache POI
     * @param result
     * @return file path
     */
    private static String writeExcel(Object result) {
        //Checks if the local folder exists
        FileHandling.checkAndCreateDirectory();

        //Create the Excel Workbook
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet(TEST_CONST);

        //Create header font
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.DARK_BLUE.getIndex());

        //Insert header font into the sheet header style
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        //Center sheet text
        CellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Row headerRow = sheet.createRow(0);

        //Insert the titles to Excel file
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int firstRow = 1;
        int lastRow = 1;

        //If has only one Diagram
        if(result instanceof SingleDiagram){
            SingleDiagram sd = (SingleDiagram) result;
            Diagram d = sd.getDiagram();
            //To get test case description and conditions
            String[] split = d.getDesc().split(":");
            int stepCount = 0;
            //Inserts the steps into the file
            for (Steps step : d.getNodes()) {
                Row row = sheet.createRow(lastRow++);
                row.createCell(2).setCellValue(stepCount++);
                if (step.getName().toLowerCase().equals(INITIAL_NODE)) {
                    //Inserts assumtions and conditions
                    row.createCell(3).setCellValue(ASSUMPTIONS_AND_CONDITIONS);
                    row.createCell(4).setCellValue(split[1]);
                } else {
                    row.createCell(3).setCellValue(step.getName().toLowerCase());
                    if (step.getName().toLowerCase().equals(END_NODE)) {
                        //Writed end when it founds the diagram end node
                        row.createCell(4).setCellValue(END_NODE);
                    } else {
                        row.createCell(4).setCellValue("Success");
                    }
                }
            }
            mergeCells(sheet,firstRow,lastRow,d.getName(),split[0]);
        }else{
            //If has multiple diagrams
            MultipleDiagrams diagrams = (MultipleDiagrams) result;
            for (Diagram d : diagrams.getDiagrams()) {
                String[] split = d.getDesc().split(":");
                int stepCount = 0;
                for (Steps step : d.getNodes()) {
                    Row row = sheet.createRow(lastRow++);
                    row.createCell(2).setCellValue(stepCount++);
                    if (step.getName().toLowerCase().equals(INITIAL_NODE)) {
                        row.createCell(3).setCellValue(ASSUMPTIONS_AND_CONDITIONS);
                        row.createCell(4).setCellValue(split[1]);
                    } else {
                        row.createCell(3).setCellValue(step.getName().toLowerCase());
                        if (step.getName().toLowerCase().equals(END_NODE)) {
                            row.createCell(4).setCellValue(END_NODE);
                        } else {
                            row.createCell(4).setCellValue("Success");
                        }
                    }
                }

                // For not repeat the same name and description in each scenario step
                mergeCells(sheet,firstRow,lastRow,d.getName(),split[0]);
                firstRow = lastRow;
            }
        }


        //Auto size the excel sheet
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        String path = FileHandling.BASE_PATH + TEST_NAME;
        try {
            //Writes the file to the path
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;

    }

    /**
     * Merge excel cells
     * @param sheet excel sheet
     * @param firstRow row where the scenario starts
     * @param lastRow row where the scenario ends
     * @param name scenario name
     * @param desc scenario description
     */
    private static void mergeCells(Sheet sheet,int firstRow, int lastRow, String name, String desc){
        Row row = sheet.getRow(firstRow);
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow - 1, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow - 1, 1, 1));
        row.createCell(0).setCellValue(name);
        row.createCell(1).setCellValue(desc);
    }

    /**
     * Receives de form data JSON and correct it
     * @param json information in message
     * @return json that will be used to transform in object
     */
    private static String processJSON(String json) {
        json = json.replace("\\", "");
        json = json.replace("\"{", "{");
        json = json.replace("}\"", "}");
        return json;
    }


}

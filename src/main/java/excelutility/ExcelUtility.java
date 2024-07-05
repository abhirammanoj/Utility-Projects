package excelutility;

import constants.Constants;
import dto.ScriptDto;
import filegenerator.SqlFileGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ExcelUtility {

    private static final Logger logger = LogManager.getLogger(ExcelUtility.class);
    private ScriptDto scriptDto = new ScriptDto();
    public void preProcessing(String path,String outputDir){
        logger.info("Inside preProcessing");
        scriptDto.setOutputDir(outputDir);
        File files = new File(path);
        try{
            for(File file : files.listFiles()){
                if(file.isFile() && (file.getName().toLowerCase().endsWith(".xlsx") || file.getName().toLowerCase().endsWith(".csv")) && !file.getName().startsWith("~$")){
                    logger.info("Processing Excel "+file.getName());
                    processExcelFile(file);
                }

            }
        }catch(Exception ex){
            logger.error("Caused exception in preProcessing "+ex.getMessage());
//            throw new RuntimeException(ex);
        }
        logger.info("Exit preProcessing");
        logger.info("******** Scripts Successfully generated ********");
    }

    public String getCellValue(Cell cell){
        if(cell == null)return "";
        switch(cell.getCellType()){
            case STRING:return cell.getStringCellValue();
            case NUMERIC: if (DateUtil.isCellDateFormatted(cell))
                            return new SimpleDateFormat("dd-MM-yy").format(cell.getDateCellValue());
                          else
                            return String.format("%.0f",cell.getNumericCellValue());
            case BLANK:return "";
        }
        return null;
    }
    public void processExcelFile(File file){
        logger.info("Inside processExcelFile");
        List<String> pkColumns = new ArrayList<>();
        List<Integer> pkColumnsIdxs = new ArrayList<>();
        List<String> columns = new ArrayList<>();

        HashMap<Integer,List<String>> columnValue = new HashMap<>();
        try (FileInputStream excelFile = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(excelFile)){
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            scriptDto.setTableName(headerRow.getCell(0).getStringCellValue());
            for(int i = headerRow.getFirstCellNum()+1 ; i < headerRow.getLastCellNum() ; ++i){
                Cell cell = headerRow.getCell(i);
                if(cell.getStringCellValue().contains("=")){
                    String[] pks = cell.getStringCellValue().split("=");
                    pkColumns.add(pks[0]);
                    pkColumnsIdxs.add(i-1);
                    columns.add(pks[0]);
                }else{
                    columns.add(headerRow.getCell(i).getStringCellValue());
                }
            }
            for(Row row : sheet){
                if(row.getRowNum() == 0)continue;//skip header row of table name and columns
                List<String> values = new ArrayList<>();
                for(int i = headerRow.getFirstCellNum()+1 ; i < headerRow.getLastCellNum() ; ++i){
                    String cellValue = getCellValue(row.getCell(i));
                    values.add(cellValue);
                }
                columnValue.put(row.getRowNum(),values);
            }
            scriptDto.setColumns(columns);
            scriptDto.setPkColumns(pkColumns);
            scriptDto.setColumnValueMap(columnValue);
            scriptDto.setPkColumnIndexes(pkColumnsIdxs);
            logger.info("Table Name : "+scriptDto.getTableName());
            logger.info("Provided PK : "+scriptDto.getPkColumns());
            logger.info("Provided PK Column Indexes : "+scriptDto.getPkColumnIndexes());
            sqlFileGenerator(scriptDto);

        }catch (IOException e) {
            logger.error("Exception in Excel processing "+e.getMessage());
        }
        logger.info("Exit processExcelFile");
    }

    public void sqlFileGenerator(ScriptDto scriptDto){
        SqlFileGenerator sqlFileGenerator = new SqlFileGenerator();
        sqlFileGenerator.generateSqlFile(scriptDto);
    }

    public void generate(){
        System.out.println("Generated");
    }

}

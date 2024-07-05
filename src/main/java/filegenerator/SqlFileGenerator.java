package filegenerator;

import dto.ScriptDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SqlFileGenerator {

    private static final Logger logger = LogManager.getLogger(SqlFileGenerator.class);
    public String formatColumnName(List<String> columns){
        return "("+String.join(",",columns)+")";
    }
    public String formatValuesString(List<String> values){
        return values.stream().map(s -> {
            if (s.matches("\\d{2}-\\d{2}-\\d{2}"))// Check if the string matches DD-MM-YY pattern
                //Return for Dates
                return "DATE '" + s + "'";
            else if(s.toLowerCase().contains("select") || s.contains("NEXTVAL"))
                //Return for UUID and seqence generators
                 return s;
            else
                //Return for all other cases
                return "'" + s + "'";
        }).collect(Collectors.joining(", "));
    }

    public String formatIdemString(List<String> pkCols, List<Integer> idxs,List<String> values){
        String res = "";
        int i = 0,size = pkCols.size();
        for(String pkCol : pkCols){
            res+= pkCol+" = \'"+values.get(idxs.get(i++))+"\'";
            if(--size > 0)res+=" AND ";
        }
//        logger.info("*** Idempotent String formatted ***"+res);
        return res;

    }
    public void generateSqlFile(ScriptDto scriptDto){
        logger.info("Inside generateSqlFile");
        String directoryPath = scriptDto.getOutputDir();
        String fileName = scriptDto.getTableName()+".sql";
        File sqlFile = new File(directoryPath, fileName);
        int numberOfInserts = scriptDto.getColumnValueMap().size();
        List<String> columns = scriptDto.getColumns();
        String columnNames = formatColumnName(columns);
        String[] values = new String[numberOfInserts];
        String[] idemString = new String[numberOfInserts];
        int k = 0;
        for(List<String> value : scriptDto.getColumnValueMap().values()){
            values[k] = formatValuesString(value);
            idemString[k++] = formatIdemString(scriptDto.getPkColumns(),scriptDto.getPkColumnIndexes(),value);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sqlFile, false))) {
            for (int i = 0; i < numberOfInserts; i++) {
                String insertStatement = "\nINSERT INTO "+scriptDto.getTableName()+" "+columnNames+"\n"+"SELECT "+values[i]+"\n"+"FROM DUAL WHERE NOT EXISTS (SELECT * FROM "+scriptDto.getTableName()+" WHERE "+idemString[i]+");\n";
                writer.write(insertStatement);
            }
            writer.write("\nCOMMIT;");
            logger.info("Script successfully generated for table : "+scriptDto.getTableName());
        } catch (IOException e) {
            logger.error("Exception Caused in generateSqlFile"+e.getMessage());
        }
        logger.info("Exit generateSqlFile");
    }
}

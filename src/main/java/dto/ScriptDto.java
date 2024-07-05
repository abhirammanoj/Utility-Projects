package dto;

import java.util.HashMap;
import java.util.List;

public class ScriptDto {
    private String tableName;
    private String outputDir;

    private List<String> pkColumns;

    private List<Integer> pkColumnIndexes;


    private List<String> columns;


    private HashMap<Integer,List<String>> columnValueMap;
    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public HashMap<Integer, List<String>> getColumnValueMap() {
        return columnValueMap;
    }

    public void setColumnValueMap(HashMap<Integer, List<String>> columnValueMap) {
        this.columnValueMap = columnValueMap;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public List<Integer> getPkColumnIndexes() {
        return pkColumnIndexes;
    }

    public void setPkColumnIndexes(List<Integer> pkColumnIndexes) {
        this.pkColumnIndexes = pkColumnIndexes;
    }


    public List<String> getPkColumns() {
        return pkColumns;
    }

    public void setPkColumns(List<String> pkColumns) {
        this.pkColumns = pkColumns;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

}

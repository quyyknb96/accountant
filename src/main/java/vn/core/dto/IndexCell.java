package vn.core.dto;

public class IndexCell {
    private String colName;
    private Integer rowNum;

    public IndexCell(String colName, Integer rowNum) {
        this.colName = colName;
        this.rowNum = rowNum;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }
}

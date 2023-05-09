package vn.core.accountant.dto;

public class Range {
    private String name;
    private Integer start;
    private Integer end;

    public Range() {
    }

    public Range(String name, Integer start, Integer end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Range{" +
                "name='" + name + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}

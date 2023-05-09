package vn.core.dto;

import java.util.List;

public class SolutionData {
    private String address;
    private List<Integer> list;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }
}

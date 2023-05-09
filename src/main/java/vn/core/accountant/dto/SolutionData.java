package vn.core.accountant.dto;

import java.util.List;

public class SolutionData {
    private String address;
    private List<Long> list;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Long> getList() {
        return list;
    }

    public void setList(List<Long> list) {
        this.list = list;
    }
}

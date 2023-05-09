package vn.core.dto;

import java.util.List;

public class ConvertData {
    private List<Range> values;
    private List<Range> targets;

    public List<Range> getValues() {
        return values;
    }

    public void setValues(List<Range> values) {
        this.values = values;
    }

    public List<Range> getTargets() {
        return targets;
    }

    public void setTargets(List<Range> targets) {
        this.targets = targets;
    }
}

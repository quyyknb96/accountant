package vn.core.accountant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConvertData {
    private String filename;
    private String sheetName;
    private List<Range> values;
    private List<Range> targets;
}

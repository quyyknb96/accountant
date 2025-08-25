package vn.core;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.core.accountant.dto.ConvertData;
import vn.core.accountant.dto.Range;
import vn.core.accountant.dto.SolutionData;
import vn.core.accountant.util.FileUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static vn.core.accountant.util.ExcelUtil.checkColumnName;
import static vn.core.accountant.util.ExcelUtil.getDataBtAddress;

public class Solution {

    public static List<SolutionData> calculator(ConvertData data) throws IOException {
        String fileExcelPath = FileUtil.PATH_TEMP + "/" + FileUtil.FILE_NAME_INPUT + "." + FilenameUtils.getExtension(data.getFilename());
        XSSFWorkbook workbook;
        FileInputStream inputStream = new FileInputStream(fileExcelPath);
        workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet;
        if (Strings.isBlank(data.getSheetName()))
            sheet = workbook.getSheetAt(0);
        else
            sheet = workbook.getSheet(data.getSheetName());

        List<Long> inputList = getValueFromFile(sheet, data.getValues());
        inputList.sort(Long::compare);

        List<SolutionData> response = new LinkedList<>();


        List<Range> ranges = data.getTargets();
        for (Range range : ranges) {
            if (!checkColumnName(range.getName()))
                continue;
            for (int j = range.getStart(); j <= range.getEnd(); j++) {
                SolutionData solutionData = new SolutionData();
                solutionData.setAddress(range.getName() + j);
                Object value = getDataBtAddress(sheet, range.getName() + j);
                if (!(value instanceof Double)) {
                    continue;
                }
                List<Long> result = find(inputList, ((Double) value).longValue());
                if (result != null && !result.isEmpty()) {
                    solutionData.setList(result);
                }
                response.add(solutionData);
            }
        }
        return response;
    }

    private static List<Long> getValueFromFile(XSSFSheet sheet, List<Range> ranges) {
        List<Long> list = new LinkedList<>();
        for (int i = 0; i < ranges.size(); i++) {
            Range range = ranges.get(i);
            if (!checkColumnName(range.getName()))
                continue;
            for (int j = range.getStart(); j <= range.getEnd(); j++) {
                Object value = getDataBtAddress(sheet, range.getName() + j);
                if (value instanceof Long)
                    list.add((Long) value);
                if (value instanceof Double)
                    list.add(((Double) value).longValue());
            }
        }
        return list;
    }


    public static List<Long> find(List<Long> nums, long target) {
        for (int i = 0; i < nums.size(); i++) {
            List<Long> list = find(i, nums, target);
            if (list != null)
                return list;
        }
        return null;
    }

    public static List<Long> find(int level, List<Long> nums, long target) {
        List<Long> list;
        switch (level) {
            case 0:
                list = sum1(nums, target);
                if (list != null)
                    return list;
                break;
            case 1:
                list = sum2(nums, target);
                if (list != null)
                    return list;
                break;
            case 2:
                list = sum3(nums, target);
                if (list != null)
                    return list;
                break;
            case 3:
                list = sum4(nums, target);
                if (list != null)
                    return list;
                break;
            default:
                int n = nums.size();
                for (int i = 0; i < n - level + 1; i++) {
                    for (int j = i + 1; j < n - level + 2; j++) {
                        long sum = target - nums.get(i) - nums.get(j);
                        List<Long> subResult = find(level - 2, nums.subList(j + 1, n), sum);
                        if (subResult != null) {
                            List<Long> preResult = new ArrayList<>(Arrays.asList(nums.get(i), nums.get(j)));
                            preResult.addAll(subResult);
                            return preResult;
                        }
                    }
                }

        }
        return null;
    }

    public static List<Long> sum1(List<Long> nums, long target) {
        if (nums.contains(target))
            return Collections.singletonList(target);
        return null;
    }

    public static List<Long> sum2(List<Long> nums, long target) {
        int l, h;
        long sum;
        l = 0;
        h = nums.size() - 1;

        while (l < h) {
            sum = nums.get(l) + nums.get(h);
            if (sum == target) {
                return Arrays.asList(nums.get(l), nums.get(h));
            } else if (sum < target) {
                l++;
            } else {
                h--;
            }
        }
        return null;
    }

    public static List<Long> sum3(List<Long> nums, long target) {
        int l, h;
        long sum;

        for (int i = 0; i < nums.size(); i++) {
            l = i + 1;
            h = nums.size() - 1;

            while (l < h) {
                sum = nums.get(i) + nums.get(l) + nums.get(h);
                if (sum == target) {
                    return Arrays.asList(nums.get(i), nums.get(l), nums.get(h));
                } else if (sum < target) {
                    l++;
                } else {
                    h--;
                }
            }
        }
        return null;
    }

    public static List<Long> sum4(List<Long> nums, long target) {
        int n = nums.size();
        for (int i = 0; i < n - 3; i++) {
            for (int j = i + 1; j < n - 2; j++) {
                int low = j + 1;
                int high = n - 1;
                long sum = target - nums.get(i) - nums.get(j);
                while (low < high) {
                    if (nums.get(low) + nums.get(high) == sum) {
                        return Arrays.asList(nums.get(i), nums.get(j), nums.get(low), nums.get(high));
                    } else if (nums.get(low) + nums.get(high) < sum) {
                        low++;
                    } else
                        high--;
                }
            }
        }
        return null;
    }

    public static List<Long> sum5(List<Long> nums, long target) {
        int n = nums.size();
        for (int i = 0; i < n - 3; i++) {
            for (int j = i + 1; j < n - 2; j++) {
                int low = j + 1;
                int high = n - 1;
                long sum = target - nums.get(i) - nums.get(j);
                while (low < high) {
                    if (nums.get(low) + nums.get(high) == sum) {
                        return Arrays.asList(nums.get(i), nums.get(j), nums.get(low), nums.get(high));
                    } else if (nums.get(low) + nums.get(high) < sum) {
                        low++;
                    } else
                        high--;
                }
            }
        }
        return null;
    }


}
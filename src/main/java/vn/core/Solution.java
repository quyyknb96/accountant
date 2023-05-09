package vn.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

class Solution {

    public static void main(String[] args) throws IOException {
        File inputFile = new File("opt/read.txt");
        if (!inputFile.exists()) {
            System.out.println("[solution] can not found file input");
            return;
        }
        int n, m = 0;
        List<Long> inputList = new ArrayList<>();
        List<Long> targetList = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(inputFile);
            while (scanner.hasNextLine()) {
                n = scanner.nextInt();
                for (int i = 0; i < n; i++) {
                    inputList.add(scanner.nextLong());
                }
                inputList.sort(Long::compare);
                m = scanner.nextInt();
                for (int i = 0; i < m; i++) {
                    targetList.add(scanner.nextLong());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("[solution] can not read file input");
            return;
        }
        File outputFile = new File("opt/write.txt");
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            System.out.println("[solution] can not create file output");
        }
        FileWriter writer = new FileWriter(outputFile, false);
        Solution solution = new Solution();
        for (int i = 0; i < m; i++) {
            List<Long> result = solution.find(inputList, targetList.get(i));
            if (result != null && !result.isEmpty()) {
                writer.write(Integer.toString(result.size()));
                writer.write(" ");
                writer.write(result.stream().map(Object::toString).collect(Collectors.joining(" ")));
                writer.write("\n");
            } else {
                writer.write("0");
                writer.write("\n");
            }
        }
        writer.close();
    }

    public List<Long> find(List<Long> nums, long target) {
        for (int i = 0; i < nums.size(); i++) {
            List<Long> list = find(i, nums, target);
            if (list != null)
                return list;
        }
        return null;
    }

    public List<Long> find(int level, List<Long> nums, long target) {
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

    public List<Long> sum1(List<Long> nums, long target) {
        if (nums.contains(target))
            return Collections.singletonList(target);
        return null;
    }

    public List<Long> sum2(List<Long> nums, long target) {
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

    public List<Long> sum3(List<Long> nums, long target) {
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

    public List<Long> sum4(List<Long> nums, long target) {
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
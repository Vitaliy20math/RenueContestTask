package Utils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class CsvReader {

    public static Map<String, List<String[]>> readCsvFile(String csvFilePath) {
        Map<String, List<String[]>> hashMap = new HashMap<>();
        String commaDelimiter = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = replaceCommasInQuotes(line);
                String[] dataArray = line.split(commaDelimiter, 14);
                String key = dataArray[1];
                List<String[]> dataList;
                if (hashMap.containsKey(key)) {
                    dataList = hashMap.get(key);
                } else {
                    dataList = new ArrayList<>();
                    hashMap.put(key, dataList);
                }
                dataList.add(dataArray);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    private static String replaceCommasInQuotes(String line) {
        StringBuilder newLine = new StringBuilder(line.length());
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '\"') {
                inQuotes = !inQuotes;
            }
            if (c == ',' && inQuotes) {
                newLine.append("");
            } else {
                newLine.append(c);
            }
        }

        return newLine.toString();
    }
}

/*public class CsvReader {

    private static final int EXPECTED_LIST_SIZE = 14;
    private static final String COMMA_DELIMITER = ",";

    public static Map<String, List<String[]>> readCsvFile(String csvFilePath) {


        Map<String, List<String[]>> hashMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {

                // заменяем ", " на "" для строк в кавычках
                line = replaceCommasInQuotes(line);

                // используем StringTokenizer для разбиения строки
                StringTokenizer tokenizer = new StringTokenizer(line, COMMA_DELIMITER);
                List<String> data = new ArrayList<>(EXPECTED_LIST_SIZE);
                while (tokenizer.hasMoreTokens()) {
                    data.add(tokenizer.nextToken());
                }

                String key = data.get(1);
                hashMap.putIfAbsent(key, new ArrayList<>());
                hashMap.get(key).add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    private static String replaceCommasInQuotes(String line) {
        StringBuilder newLine = new StringBuilder(line.length());
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '\"') {
                inQuotes = !inQuotes;
            }
            if (c == ',' && inQuotes) {
                newLine.append("");
            } else {
                newLine.append(c);
            }
        }

        return newLine.toString();
    }
}*/


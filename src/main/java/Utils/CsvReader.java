package Utils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
                if (!isValidUnicode(dataArray[1])) { // проверяем, является ли символ юникодным
                    dataArray[1] = encodeToUTF8(dataArray[1]); // если символ не юникодный, кодируем его в UTF-8
                }
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

        for (int i = 0; i < line.length(); i++) {
            byte symbol = (byte) line.charAt(i);
            if (symbol == '\"') {
                inQuotes = !inQuotes;
            }
            if (symbol == ',' && inQuotes) {
                newLine.append("");
            } else {
                newLine.append((char) symbol);
            }
        }

        return newLine.toString();
    }

    public static boolean isValidUnicode(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) > 0xFFFD) {
                return false;
            }
        }
        return true;
    }

    public static String encodeToUTF8(String str) {
        byte[] utf8Bytes = null;
        utf8Bytes = str.getBytes(StandardCharsets.UTF_8);
        return new String(utf8Bytes, StandardCharsets.UTF_8);
    }
}



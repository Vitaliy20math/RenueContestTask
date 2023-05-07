import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CsvReader {
    public static Map<String, List<List<Object>>> readCsvFile(String csvFilePath) {
        Map<String, List<List<Object>>> hashMap = new HashMap<>();
        List<Object> data;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String newLine = line.replaceAll("[a-zA-Z],[\\s]*[a-zA-Z]","");
                String[] columns;
                columns = newLine.split(",");
                data = new LinkedList<>(Arrays.asList(columns));
                String key = columns[1];
                if (hashMap.containsKey(key)) {
                    hashMap.get(key).add(data);
                } else {
                    List<List<Object>> list = new LinkedList<>();
                    list.add(data);
                    hashMap.put(key, list);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hashMap;
    }
}

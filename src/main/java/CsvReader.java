import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CsvReader {
    public static Map<String, List<List<?>>> readCsvFile(String csvFilePath) {
        Map<String, List<List<?>>> hashMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {

                String newLine = line.replaceAll(",\\s(?=[a-zA-Z])","");

                String[] columns;
                columns = newLine.split(",");
                List<?> data = new ArrayList<>(Arrays.asList(columns));

                String key = columns[1];
                hashMap.putIfAbsent(key, new ArrayList<>());
                hashMap.get(key).add(data);
                /*if (hashMap.containsKey(key)) {
                    hashMap.get(key).add(data);
                } else {
                    List<List<?>> list = new LinkedList<>();
                    list.add(data);
                    hashMap.put(key, list);
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hashMap;
    }
}

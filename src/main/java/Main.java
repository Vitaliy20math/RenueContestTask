import Utils.CsvReader;
import Utils.Filter;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Ахкямиев Виталий, май 2023
 * Тестовое задание
 */

public class Main {
    private static final String CSV_FILE_PATH = "D://airports.csv";

    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        Map<String, List<String[]>> hashMap = CsvReader.readCsvFile(CSV_FILE_PATH);

        while (true) {
            System.out.println("Please enter your filter: ");
            String filter = reader.readLine();
            if (filter.startsWith("!quit")) {
                break;
            }
            System.out.println("Please enter name airport: ");

            String nameAirport = reader.readLine();
            if (nameAirport.isEmpty()) {
                System.out.println("Имя аэропорта должно быть!");
                continue;
            }
            AtomicLong countGoodLine = new AtomicLong(0);
            List<String[]> airportDataMap = new ArrayList<>();
            List<String[]> rows = getRowsForAirport(nameAirport, hashMap);

            long startTime = System.currentTimeMillis();
            if (filter.isEmpty()) {
                countGoodLine.set(rows.size());
                rows.stream()
                        .forEach(row -> System.out.println(row[1] + Arrays.stream(row).toList()));
            } else {
                List<String> tokens = convertToRPN(filter);
                for (String[] row : rows) {
                    try {
                        if (Filter.evaluate(tokens, row)) {
                            countGoodLine.incrementAndGet();
                            airportDataMap.add(row);
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
            }
            for (String[] airportData : airportDataMap) {
                System.out.println(airportData[1] + "" + Arrays.stream(airportData).toList());
            }
            long endTime = System.currentTimeMillis();
            long elapsedTimeMs = endTime - startTime;
            System.out.printf("Количество найденных строк: %d Время выполнения программы: %d мс %n", countGoodLine.get(), elapsedTimeMs);
        }
    }

    public static List<String> convertToRPN(String expression) {
        Map<String, Integer> precedence = Map.ofEntries(
                Map.entry("(", 1),
                Map.entry("&", 2),
                Map.entry("||", 3)
        );
        Stack<String> stack = new Stack<>();
        List<String> list;
        String[] tokens = expression.split("(?<=[\\|\\|]{2})|(?=[\\|\\|]{2})|(?<=[\\&])|(?=[\\&])|(?<=[\\(])|(?=[\\(])|(?<=[\\)])|(?=[\\)])");
        StringBuilder output = new StringBuilder();

        for (String token : tokens) {
            switch (token) {
                case "(": {
                    stack.push(token);
                    break;
                }
                case ")": {
                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        output.append(stack.pop()).append(" ");
                    }
                    stack.pop();
                    break;
                }
                case "&":
                case "||": {
                    while (!stack.isEmpty() && !stack.peek().matches("[&|]+") &&
                            precedence.get(stack.peek()) >= precedence.get(token)) {
                        output.append(stack.pop()).append(" ");
                    }
                    stack.push(token);
                    break;
                }
                default: {
                    String newToken = token.replaceAll("\s+", "");
                    output.append(newToken).append(" ");
                    break;
                }
            }
        }
        while (!stack.isEmpty()) {
            output.append(stack.pop()).append(" ");
        }
        return Arrays.stream(output.toString().split("\\s+"))
                .map(o -> o.replaceAll("<>", "!"))
                .collect(Collectors.toList());
    }

    private static List<String[]> getRowsForAirport(String airportName, Map<String, List<String[]>> data) {
        String airportNameLower = airportName.toLowerCase();
        return data.entrySet().stream()
                .filter(entry -> {
                    String keyNormalized = java.text.Normalizer.normalize(entry.getKey(), java.text.Normalizer.Form.NFD);
                    String keyLower = keyNormalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").toLowerCase();
                    return keyLower.startsWith("\"" + airportNameLower);
                })
                .flatMap(entry -> entry.getValue().stream())  // здесь используем flatMap, чтобы получить список строк
                .filter(row -> row != null && row.length > 0)
                .collect(Collectors.toList());
    }
}

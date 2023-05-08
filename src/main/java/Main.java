import Utils.CsvReader;
import Utils.Filter;

import java.io.*;
import java.util.*;

/**
 * Ахкямиев Виталий, май 2023
 * Тестовое задание в компанию Renue
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
            int countGoodLine = 0;
            List<String[]> airportDataMap = new ArrayList<>();
            List<String[]> rows = getRowsForAirport(nameAirport, hashMap);
            long startTime = System.currentTimeMillis();

            if (filter.isEmpty()) {
                for (String[] row : rows) {
                    ++countGoodLine;
                    System.out.printf("%s%s%n", row[1], Arrays.toString(row));
                }
            } else {
                List<String> tokens = convertToRPN(filter);
                for (String[] row : rows) {
                    try {
                        if (Filter.evaluate(tokens, row)) {
                            ++countGoodLine;
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
            System.out.printf("Количество найденных строк: %d Время выполнения программы: %d мс %n", countGoodLine, elapsedTimeMs);
        }
    }

    public static List<String> convertToRPN(String expression) {
        Map<String, Integer> precedence = Map.of(
                "(", 1,
                "&", 2,
                "||", 3
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

        list = new ArrayList<>(Arrays.asList(output.toString().split("\\s+")));
        return list.stream().map(o -> o.replaceAll("<>", "!")).toList();
    }

    private static List<String[]> getRowsForAirport(String airportName, Map<String, List<String[]>> data) {
        List<String[]> rows = new ArrayList<>();
        for (Map.Entry<String, List<String[]>> entry : data.entrySet()) {
            if (entry.getKey().replaceAll("\"", "").startsWith(airportName)) {
                rows.addAll(entry.getValue());
            }
        }
        return rows;
    }
}

import java.io.*;
import java.util.*;

/**
 * Ахкямиев Виталий, май 2023
 * Тестовое задание в компанию Renue
 */

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Please enter your filter: ");
            String filter = scanner.nextLine();
            if (filter.startsWith("!quit")) {
                break;
            }
            System.out.println("Please enter name airport: ");
            String nameAirport = scanner.nextLine();

            String csvFilePath = "D://airports.csv";
            List<Object> data;
            Map<String, List<List<Object>>> hashMap = CsvReader.readCsvFile("D://airports.csv");

            int countGoodLine = 0;
            HashMap<String, List<List<Object>>> solve = new HashMap<>();
            long startTime = System.currentTimeMillis();
            for (Map.Entry<String, List<List<Object>>> entry : hashMap.entrySet()) {
                String key = entry.getKey();
                if (key.replaceAll("\"", "").startsWith(nameAirport)) {
                    List<List<Object>> rows = entry.getValue();
                    if (filter.isEmpty()) {
                        for (List<Object> row : rows) {
                            ++countGoodLine;
                            System.out.println(key + row);
                        }
                    } else {
                        List<String> tokens = convertToRPN(filter);
                        for (List<Object> row : rows) {
                            if (Filter.evaluate(tokens, row)) {
                                ++countGoodLine;
                                if (!solve.containsKey(key)) {
                                    solve.put(key, new ArrayList<>());
                                }
                                solve.get(key).add(row);
                            }
                        }
                    }
                }
            }

            for (Map.Entry<String, List<List<Object>>> s : solve.entrySet()) {
                System.out.println(s.getKey() + s.getValue());
            }
            long endTime = System.currentTimeMillis();
            long elapsedTimeMs = endTime - startTime;
            System.out.printf("Количество найденных строк: %d Время выполнения программы: %d мс %n", countGoodLine, elapsedTimeMs);
        }
    }

    private static final Map<String, Integer> precedence = Map.of(
            "(", 1,
            "&", 2,
            "||", 3
    );

    public static List<String> convertToRPN(String expression) {
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
        return list;
    }
}

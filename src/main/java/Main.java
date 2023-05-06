import org.w3c.dom.ls.LSOutput;

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
            if (filter.contains("!quit")) {
                break;
            }
            System.out.println("Please enter name airport: ");
            String nameAirport = scanner.nextLine();
            HashMap<String, List<List<Object>>> hashMap = new HashMap<>();

            String csvFilePath = "D://airports.csv";
            List<Object> data;

            try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String newLine = line.trim();
                    String[] columns;
                    columns = newLine.split(",");
                    data = new ArrayList<>(Arrays.asList(columns));
                    if (hashMap.containsKey(columns[1])) {
                        hashMap.get(columns[1]).add(data);
                    } else {
                        List<List<Object>> list = new ArrayList<>();
                        list.add(data);
                        hashMap.put(columns[1], list);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            int countGoodLine = 0;
            long elapsedTime = 0l;
            HashMap<String, List<List<Object>>> solve = new HashMap<>();
            long startTime = System.nanoTime();
            for (String key : hashMap.keySet()) {
                if (key.replaceAll("\"", "").startsWith(nameAirport)) {
                    if (filter.isEmpty()) {
                        ++countGoodLine;
                        System.out.println(hashMap.get(key).size() + " " + key + hashMap.get(key));
                    } else {
                        List<String> tokens = convertToRPN(filter);

                        Stack<Boolean> stack = new Stack<>();
                        List<Integer> numLine = new ArrayList<>();
                        int columnIndex;

                        for (int i = 0; i < hashMap.get(key).size(); ++i) {
                            for (String token : tokens) {

                                if (!token.equals("||") && !token.equals("&")) {

                                    columnIndex = Integer.parseInt(token.substring(token.indexOf("[") + 1, token.indexOf("]")).trim()) - 1;
                                    if (token.contains("=")) {
                                        String value = token.substring(token.indexOf("=") + 1).replaceAll("['’\"]", "").trim();
                                        String valueInCollection = ((String) hashMap.get(key).get(i).get(columnIndex)).replaceAll("\"", "").replaceAll("\s+","");

                                        if (valueInCollection.equalsIgnoreCase(value)) {
                                            stack.push(true);
                                            //System.out.println("here");
                                        } else {
                                            //System.out.println("again here");
                                            stack.push(false);
                                        }
                                    }
                                    if (token.contains("<>")) {
                                        String value = token.substring(token.indexOf("<>") + 1).trim();
                                        if (hashMap.get(key).get(i).get(columnIndex) != value.replaceAll("\"", "")) {
                                            stack.push(true);
                                        } else {
                                            stack.push(false);
                                        }
                                    }
                                    if (token.contains(">")) {
                                        String value = token.substring(token.indexOf(">") + 1).trim();

                                        if (Double.parseDouble((String) hashMap.get(key).get(i).get(columnIndex)) > Double.parseDouble(value.replaceAll("\"", ""))) {
                                            stack.push(true);
                                        } else {
                                            stack.push(false);
                                        }
                                    }
                                    if (token.contains("<")) {
                                        String value = token.substring(token.indexOf("<") + 1).trim();
                                        if (Double.parseDouble((String) hashMap.get(key).get(i).get(columnIndex)) < Double.parseDouble(value.replaceAll("\"", ""))) {
                                            stack.push(true);
                                        } else {
                                            stack.push(false);
                                        }
                                    }
                                } else if (token.equals("||")) {
                                    boolean right = stack.pop();
                                    boolean left = stack.pop();
                                    stack.push(left || right);
                                } else {
                                    boolean right = stack.pop();
                                    boolean left = stack.pop();
                                    stack.push(left && right);
                                }
                            }
                            if (stack.pop()) {
                                ++countGoodLine;
                                if (!solve.containsKey(key)) {
                                    List<List<Object>> goodLinesForKey = new ArrayList<>();
                                    List<Object> list = hashMap.get(key).get(i);
                                    goodLinesForKey.add(list);
                                    solve.put(key, goodLinesForKey);
                                } else {
                                    solve.get(key).add(hashMap.get(key).get(i));
                                }
                            }
                        }
                    }
                }
            }
            for (Map.Entry<String, List<List<Object>>> s : solve.entrySet()) {
                System.out.println(s.getKey() + s.getValue());
            }
            long endTime = System.nanoTime();
            elapsedTime = endTime - startTime;
            System.out.printf("Количество найденных строк: %d", countGoodLine);
            System.out.println();
            System.out.println("Время выполнения программы: " + elapsedTime / Math.pow(10, 6) + "мс");
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
        //System.out.println(Arrays.toString(tokens));
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
        list = Arrays.stream(output.toString().split("\\s+")).toList();
        //System.out.println(Arrays.toString(list.toArray()));
        return list;
    }
}

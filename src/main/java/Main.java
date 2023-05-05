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
        //System.out.println("Please enter your filter: ");
        while(true) {
            String filter = scanner.nextLine();
            if (filter.contains("!quit")) {
                break;
            }

            String nameAirport = scanner.nextLine();
            System.out.println(nameAirport.toString());
            HashMap<String, List<List<Object>>>hashMap = new HashMap<>();

            String csvFilePath = "D://airports.csv";
            List<Object> data;

            try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String newLine = line.trim();
                    String[] columns;
                    columns =newLine.split(",");
                    data = new ArrayList<>(Arrays.asList(columns));
                    if (hashMap.containsKey(columns[1])) {
                        hashMap.get(columns[1]).add(data);
                    } else {
                        List<List<Object>> list = new ArrayList<>();
                        list.add(data);
                        hashMap.put(columns[1],list);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (String key : hashMap.keySet()) {
                if (key.replaceAll("\"", "").startsWith(nameAirport)) {
                    if (filter.isEmpty()) {
                        System.out.println(hashMap.get(key).size() + " " + key + hashMap.get(key));
                    } else {
                        List<String> tokens = convertToRPN(filter);
                        System.out.println("Our tokens: ");
                        System.out.println(tokens);
                        //String output = convertToRPN(filter);
                        //System.out.println(output);

                        /*List<List<Object>> list = hashMap.get(key);
                        for (int i = 0; i < list.size(); ++i) {
                            list.get(i).
                        }*/
                    }
                }
            }

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
        //System.out.println(Arrays.toString(Arrays.stream(tokens).toArray()));

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
                default: { // операнд
                    output.append(token).append(" ");
                    break;
                }
            }
        }
        while (!stack.isEmpty()) {
            output.append(stack.pop()).append(" ");
        }
        list = Arrays.stream(output.toString().split("\\s+")).toList();
        return list;
        //return output.toString().trim();
    }

}

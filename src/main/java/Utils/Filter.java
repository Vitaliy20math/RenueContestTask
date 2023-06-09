package Utils;

import java.util.List;
import java.util.Stack;

public class Filter {
    public static boolean evaluate(List<String> tokens, String[] data) {
        Stack<Boolean> stack = new Stack<>();
        for (String token : tokens) {
            boolean right;
            boolean left;
            switch (token) {
                case "&":
                case "&&":
                    right = stack.pop();
                    left = stack.pop();
                    stack.push(left && right);
                    break;
                case "|":
                case "||":
                    right = stack.pop();
                    left = stack.pop();
                    stack.push(left || right);
                    continue;
                default:
                    String TEXT_FORMAT = "Убедитесь в корректности сравнения, например типы данных для значений: %s и %s расходятся%n";
                    int columnIndex = Integer.parseInt(token.substring(token.indexOf("[") + 1, token.indexOf("]")).trim()) - 1;
                    String valueInCollectionWithQuotes = data[columnIndex];
                    String valueInCollection = valueInCollectionWithQuotes.replaceAll("[\"',’\\s]+", "");
                    if (token.contains("=")) {
                        String value = token.substring(token.indexOf("=") + 1).replaceAll("[,'’\\s]+", "");
                        try {
                            if (valueInCollection.equalsIgnoreCase(value)) {
                                stack.push(true);
                            } else {
                                stack.push(false);
                            }
                        } catch (NumberFormatException e) {
                            System.out.printf(TEXT_FORMAT, value, data[columnIndex]);
                        }
                    } else if (token.contains("!")) {
                        String value = token.substring(token.indexOf("!") + 1).replaceAll("[\"',’\\s]+", "");
                        try {
                            if (!valueInCollection.equalsIgnoreCase(value)) {
                                stack.push(true);
                            } else {
                                stack.push(false);
                            }
                        } catch (NumberFormatException e) {
                            System.out.printf(TEXT_FORMAT, value, data[columnIndex]);
                        }
                    } else if (token.contains(">")) {
                        String value = token.substring(token.indexOf(">") + 1).trim();
                        try {
                            if (Double.parseDouble(data[columnIndex]) > Double.parseDouble(value.replaceAll("\"", ""))) {
                                stack.push(true);
                            } else {
                                stack.push(false);
                            }
                        } catch (NumberFormatException e) {
                            System.out.printf(TEXT_FORMAT, value, data[columnIndex]);
                        }
                    } else if (token.contains("<")) {
                        String value = token.substring(token.indexOf("<") + 1).trim();
                        try {
                            if (Double.parseDouble(data[columnIndex]) < Double.parseDouble(value.replaceAll("\"", ""))) {
                                stack.push(true);
                            } else {
                                stack.push(false);
                            }
                        } catch (NumberFormatException e) {
                            System.out.printf(TEXT_FORMAT, value, data[columnIndex]);
                        }
                    }
            }
        }
        return stack.pop();
    }
}

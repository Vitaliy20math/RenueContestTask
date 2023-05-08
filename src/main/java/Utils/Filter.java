package Utils;
import java.util.List;
import java.util.Stack;

public class Filter {
    public static boolean evaluate(List<String> tokens, List<?> data) {
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
                    int columnIndex = Integer.parseInt(token.substring(token.indexOf("[") + 1, token.indexOf("]")).trim()) - 1;
                    String valueInCollectionWithQuotes = (String) data.get(columnIndex);
                    String valueInCollection = valueInCollectionWithQuotes.replace("\"", "").replace(",", "").replace(" ", "");
                    if (token.contains("=")) {
                        String value = token.substring(token.indexOf("=") + 1).replaceAll("['’\", ]", "").trim();
                        try {
                            if (valueInCollection.equalsIgnoreCase(value)) {
                                stack.push(true);
                            } else {
                                stack.push(false);
                            }
                        } catch (NumberFormatException e) {
                            System.out.printf("Убедитесь в корректности сравнения: %s и %s%n", value, (String) data.get(columnIndex));
                        }
                    } else if (token.contains("!")) {
                        String value = token.substring(token.indexOf("!") + 1).replaceAll("['’\", ]", "").trim();
                        try {
                            if (!valueInCollection.equalsIgnoreCase(value)) {
                                stack.push(true);
                            } else {
                                stack.push(false);
                            }
                        } catch (NumberFormatException e) {
                            System.out.printf("Убедитесь в корректности сравнения: %s и %s%n", value, (String) data.get(columnIndex));
                        }
                    } else if (token.contains(">")) {
                        String value = token.substring(token.indexOf(">") + 1).trim();
                        try {
                            if (Double.parseDouble((String) data.get(columnIndex)) > Double.parseDouble(value.replaceAll("\"", ""))) {
                                stack.push(true);
                            } else {
                                stack.push(false);
                            }
                        } catch (NumberFormatException e) {
                            System.out.printf("Убедитесь в корректности сравнения: %s и %s%n", value, (String) data.get(columnIndex));
                        }
                    } else if (token.contains("<")) {
                        String value = token.substring(token.indexOf("<") + 1).trim();
                        try {
                            if (Double.parseDouble((String) data.get(columnIndex)) < Double.parseDouble(value.replaceAll("\"", ""))) {
                                stack.push(true);
                            } else {
                                stack.push(false);
                            }
                        } catch (NumberFormatException e) {
                            System.out.printf("Убедитесь в корректности сравнения: %s и %s%n", value, (String) data.get(columnIndex));
                        }
                    }
            }
        }
        return stack.pop();
    }
}

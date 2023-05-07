import java.util.ArrayList;
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
                    break;
                default:
                    int columnIndex = Integer.parseInt(token.substring(token.indexOf("[") + 1, token.indexOf("]")).trim()) - 1;
                    if (token.contains("=")) {
                        String value = token.substring(token.indexOf("=") + 1).replaceAll("['’\", ]", "").trim();
                        String valueInCollection = ((String) data.get(columnIndex)).replaceAll("\"", "").replaceAll("[, ]", "");

                        if (valueInCollection.equalsIgnoreCase(value)) {
                            //System.out.println(value + " " + valueInCollection);
                            stack.push(true);
                        } else {
                            stack.push(false);
                        }
                    }
                    if (token.contains("!")) {
                        String value = token.substring(token.indexOf("!") + 1).replaceAll("['’\", ]", "").trim();
                        String valueInCollection = ((String) data.get(columnIndex)).replaceAll("\"", "").replaceAll("[, ]", "");
                        if (!valueInCollection.equalsIgnoreCase(value)) {
                            stack.push(true);
                        } else {
                            stack.push(false);
                        }
                    }
                    if (token.contains(">")) {
                        String value = token.substring(token.indexOf(">") + 1).trim();

                        if (Double.parseDouble((String) data.get(columnIndex)) > Double.parseDouble(value.replaceAll("\"", ""))) {
                            stack.push(true);
                        } else {
                            stack.push(false);
                        }
                    }
                    if (token.contains("<")) {
                        String value = token.substring(token.indexOf("<") + 1).trim();
                        if (Double.parseDouble((String) data.get(columnIndex)) < Double.parseDouble(value.replaceAll("\"", ""))) {
                            stack.push(true);
                        } else {
                            stack.push(false);
                        }
                    }

            }
            /*if (token.equals("&") || token.equals("&&")) {
                boolean right = stack.pop();
                boolean left = stack.pop();
                stack.push(left && right);
            } else if (token.equals("|") || token.equals("||")) {
                boolean right = stack.pop();
                boolean left = stack.pop();
                stack.push(left || right);
            } else {
                int columnIndex = Integer.parseInt(token.substring(token.indexOf("[") + 1, token.indexOf("]")).trim()) - 1;
                if (token.contains("=")) {
                    String value = token.substring(token.indexOf("=") + 1).replaceAll("['’\", ]", "").trim();
                    String valueInCollection = ((String) data.get(columnIndex)).replaceAll("\"", "").replaceAll("[, ]","");

                    if (valueInCollection.equalsIgnoreCase(value)) {
                        //System.out.println(value + " " + valueInCollection);
                        stack.push(true);
                    } else {
                        stack.push(false);
                    }
                }
                if (token.contains("!")) {
                    String value = token.substring(token.indexOf("<>") + 1).trim();
                    if (data.get(columnIndex) != value.replaceAll("\"", "")) {
                        stack.push(true);
                    } else {
                        stack.push(false);
                    }
                }
                if (token.contains(">")) {
                    String value = token.substring(token.indexOf(">") + 1).trim();

                    if (Double.parseDouble((String)data.get(columnIndex)) > Double.parseDouble(value.replaceAll("\"", ""))) {
                        stack.push(true);
                    } else {
                        stack.push(false);
                    }
                }
                if (token.contains("<")) {
                    String value = token.substring(token.indexOf("<") + 1).trim();
                    if (Double.parseDouble((String) data.get(columnIndex)) < Double.parseDouble(value.replaceAll("\"", ""))) {
                        stack.push(true);
                    } else {
                        stack.push(false);
                    }
                }
            }*/
        }
        return stack.pop();
    }
}

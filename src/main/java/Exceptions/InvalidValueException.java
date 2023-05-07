package Exceptions;

public class InvalidValueException extends NumberFormatException {
    public InvalidValueException(String s) {
        super(s);
    }

    @Override
    public String getMessage() {
        return "Некорректное значение таблицы: " + super.getMessage() + ", ожидалось число.";
    }
}

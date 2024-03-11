package org.example.validators;

import java.util.*;

public class MathEquationValidator {

    public static boolean isValidEquation(String equation) {
        equation = equation.replaceAll("\\s+", "");

        int equalsSignCount = equation.length() - equation.replace("=", "").length();
        if (equalsSignCount != 1) {
            return false;
        }

        String[] parts = equation.split("=");
        if (parts.length != 2) {
            return false;
        }

        return isValidBrackets(parts[0]) && isValidSigns(parts[0]) && isValidDecimalPoints(parts[0]) &&
               isValidBrackets(parts[1]) && isValidSigns(parts[1]) && isValidDecimalPoints(parts[1]);
    }

    private static boolean isValidSigns(String expression) {
        boolean lastWasOperator = true;
        boolean lastWasOperand = false;

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (Character.isDigit(ch) || ch == '.' || ch == 'x' || ch == '(' || ch == ')') {
                lastWasOperand = true;
                lastWasOperator = false;
            } else if (ch == '+'  || ch == '*' ||  ch == '/' || (ch == '-' && lastWasOperand)) {
                if (lastWasOperator && ch != '-') {
                    return false;
                }
                lastWasOperator = true;
                lastWasOperand = false;
            } else if (ch == '-' && (i == 0 || expression.charAt(i-1) == '+' || expression.charAt(i-1) == '*' || expression.charAt(i-1) == '/' ||  expression.charAt(i-1) == '(')) {
                continue;
            } else {
                return false;
            }
        }

        return !lastWasOperator;
    }

    private static boolean isValidBrackets(String equation) {
        Map<Character, Character> bracketsMap = new HashMap<>();
        bracketsMap.put(')', '(');

        Stack<Character> stack = new Stack<>();

        for(char ch : equation.toCharArray()) {
            if(bracketsMap.containsValue(ch)) {
                stack.push(ch);
            } else if(bracketsMap.containsKey(ch)) {
                if(stack.isEmpty() || stack.pop() != bracketsMap.get(ch)) {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }

    private static boolean isValidDecimalPoints(String expression) {
        String[] numbers = expression.split("[^\\d.]");
        for (String number : numbers) {
            if (!number.isEmpty() && number.indexOf('.') != number.lastIndexOf('.')) {
                return false;
            }
        }
        return true;
    }
}
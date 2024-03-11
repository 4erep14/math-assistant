package org.example.services;

import org.example.models.Operator;

import java.util.*;

public class ExpressionEvaluator {

    private static final Map<String, Operator> operators = new HashMap<>() {
        {
            put("+", Operator.PLUS);
            put("-", Operator.MINUS);
            put("*", Operator.MULTIPLY);
            put("/", Operator.DIVIDE);
            put("~", Operator.UNARY_MINUS);
        }
    };

    public static Optional<String> transformInfixToPostfix(String input) {
        input = input.replaceAll(" ", "");
        input = input.replaceAll("(?<![\\d)])-", "~");
        char[] infix = input.toCharArray();
        Stack<String> opStack = new Stack<>();
        StringJoiner postfix = new StringJoiner(" ");

        StringBuilder operand = new StringBuilder();

        for (int i = 0; i < infix.length; i++) {
            if (!operators.containsKey(String.valueOf(infix[i])) && infix[i] != '(' && infix[i] != ')') {
                operand.append(infix[i]);
                if (i < infix.length - 1) {
                    continue;
                }
            }

            if (!operand.isEmpty()) {
                postfix.add(operand.toString());
                operand = new StringBuilder();
            }

            if (infix[i] == '(') {
                opStack.push("(");
            } else if (infix[i] == ')') {
                while (!opStack.isEmpty() && !opStack.peek().equals("(")) {
                    postfix.add(opStack.pop());
                }
                if (!opStack.isEmpty()) {
                    opStack.pop(); // Pop the '('
                }
            } else if (operators.containsKey(String.valueOf(infix[i]))) {
                while (!opStack.isEmpty() && !opStack.peek().equals("(") &&
                        (operators.get(opStack.peek()).getPriority() >= operators.get(String.valueOf(infix[i])).getPriority())) {
                    postfix.add(opStack.pop());
                }
                opStack.push(String.valueOf(infix[i]));
            }
        }

        if (!operand.isEmpty()) {
            postfix.add(operand.toString());
        }

        while (!opStack.isEmpty()) {
            postfix.add(opStack.pop());
        }

        return Optional.of(postfix.toString());
    }

    public static double solvePostfix(String input) throws NumberFormatException {
        String[] in = input.split(" ");
        Stack<Double> stack = new Stack<>();
        for (String o : in) {
            try {
                double operand = Double.parseDouble(o);
                stack.push(operand);
            } catch (NumberFormatException ex) {
                if (!operators.containsKey(o)) {
                    throw ex;
                }

                Operator op = operators.get(o);
                if (op.isUnary()) {
                    if (stack.isEmpty()) {
                        throw new NumberFormatException("Invalid expression: expected a number before unary operator");
                    }
                    double value = stack.pop();
                    double result = op.compute(value);
                    stack.push(result);
                } else {
                    if (stack.size() < 2) {
                        throw new NumberFormatException("Invalid expression: not enough operands for binary operator");
                    }
                    double right = stack.pop();
                    double left = stack.pop();
                    double result = op.compute(left, right);
                    stack.push(result);
                }
            }
        }
        if (stack.size() != 1) {
            throw new NumberFormatException("Invalid expression: multiple numbers left in the stack");
        }
        return stack.pop();
    }


    public static boolean checkEquationRoot(String equation, double root) {
        equation = equation.replaceAll("[xX]", Double.toString(root));
        String[] parts = equation.split("=");

        double leftPart =  solvePostfix(transformInfixToPostfix(parts[0]).get());
        double rightPart = solvePostfix(transformInfixToPostfix(parts[1]).get());

        return Math.abs(leftPart - rightPart) < 1e-9;
    }
}
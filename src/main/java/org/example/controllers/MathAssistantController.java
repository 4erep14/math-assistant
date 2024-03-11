package org.example.controllers;

import org.example.repository.DatabaseHelper;
import org.example.services.ExpressionEvaluator;
import org.example.utils.StringToDoubleConverter;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.example.validators.MathEquationValidator.isValidEquation;

public class MathAssistantController {

    private final static Scanner SCANNER = new Scanner(System.in);

    public static Scanner getScanner() { return SCANNER; }

    public static void addEquationToDB() {
        boolean valid = false;
        boolean running = true;

        while (!valid && running) {
            System.out.print("Enter the equation: ");
            SCANNER.nextLine();
            String equation = SCANNER.nextLine();
            valid = isValidEquation(equation);
            if (valid && running) {
                System.out.println("Equation is valid.");
                try {
                    DatabaseHelper.saveEquation(equation);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                boolean optionValid = false;
                while (!optionValid) {
                    System.out.println("[1] Check your solution");
                    System.out.println("[2] Back to main menu");
                    System.out.print("Choose an option: ");
                    int choice = SCANNER.nextInt();
                    switch (choice) {
                        case 1:
                            try {
                                checkRoot(equation);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                            optionValid = true;
                            running = false;
                            break;
                        case 2:
                            optionValid = true;
                            running = false;
                            break;
                        default:
                            System.out.println("Invalid option. Try again.");
                            break;
                    }
                }
            } else {
                System.out.println("Equation is invalid.");
                System.out.println("[1] Try again");
                System.out.println("[2] Back to main menu");
                System.out.print("Choose an option: ");
                int choice = SCANNER.nextInt();
                if (choice != 1) {
                    running = false;
                }
            }
        }
    }

    public static void findEquationsByRoots() {
            System.out.print("Enter roots separated by whitespace: ");
            SCANNER.nextLine();
            List<Double> roots = Arrays.stream(SCANNER.nextLine().split("\\s+")).map(StringToDoubleConverter::convert).toList();
            System.out.println("List of equations that contain one of these roots: ");
        List<String> equations = null;
        try {
            equations = DatabaseHelper.findEquationsByRoots(roots);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < equations.size(); i++) {
                System.out.printf("[%d] %s", i+1, equations.get(i));
                System.out.println();
            }
    }

    public static void findUnsolvedEquations() {
        System.out.println("List of unsolved equations: ");
        List<String> equations = null;
        try {
            equations = DatabaseHelper.findUnsolvedEquations();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < equations.size(); i++) {
            System.out.printf("[%d] %s", i+1, equations.get(i));
            System.out.println();
        }
        System.out.println("\n[1] Choose equation to solve");
        System.out.println("[2] Back to main menu");
        System.out.print("Select an option: ");
        int option = SCANNER.nextInt();
        if(option == 1) {
            boolean optionValid = false;
            while (!optionValid) {
                System.out.print("Select equation to solve: ");
                int equationNumber = SCANNER.nextInt();
                if (equationNumber < 1 || equationNumber > equations.size()) {
                    System.out.println("Incorrect option, Try again.");
                } else {
                    optionValid = true;
                    try {
                        checkRoot(equations.get(equationNumber - 1));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }


    public static void checkRoot(String equation) throws SQLException {
        System.out.print("Enter your answer: ");
        SCANNER.nextLine();
        double x = StringToDoubleConverter.convert(SCANNER.nextLine());
        if(ExpressionEvaluator.checkEquationRoot(equation, x)) {
            DatabaseHelper.saveRoot(equation, x);
            System.out.println("Your answer is correct!!!");
            System.out.println("Press enter for going back to main menu");
            SCANNER.nextLine();
        } else {
            System.out.println("Your answer is incorrect");
            System.out.println("[1] Try again");
            System.out.println("[2] Back to main menu");
            System.out.print("Choose an option: ");
            if(SCANNER.nextInt() == 1) {
                checkRoot(equation);
            }
        }
    }

    public static void terminateProgram() {
        SCANNER.close();
        System.exit(0);
    }
}
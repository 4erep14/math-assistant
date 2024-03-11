package org.example;

import org.example.controllers.MathAssistantController;
import org.example.services.ExpressionEvaluator;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class MathAssistant {
    public static void main(String[] args) throws SQLException {
        run();
    }

    private static void run() {
        Scanner scanner = MathAssistantController.getScanner();
        boolean running = true;
        Map<Integer, Runnable> actionMap = new HashMap<>(){
            {
                put(1, MathAssistantController::addEquationToDB);
                put(2, MathAssistantController::findEquationsByRoots);
                put(3, MathAssistantController::findUnsolvedEquations);
                put(4, MathAssistantController::terminateProgram);
            }
        };
        while (running) {
            System.out.println("Main Menu:");
            System.out.println("[1] Enter the equation");
            System.out.println("[2] Find equations by roots");
            System.out.println("[3] Find unsolved equations");
            System.out.println("[4] Exit");
            System.out.print("Select an option by pressing the corresponding key: ");
            int input = -1;
            if(scanner.hasNextInt()) {
                input = scanner.nextInt();
            } else {
                scanner.nextLine();
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }
            Runnable action = actionMap.get(input);

            if(Objects.nonNull(action)) {
                action.run();
            } else {
                System.out.println("Incorrect option, try again");
            }
        }

        scanner.close();

    }
}
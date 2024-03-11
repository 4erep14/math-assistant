package org.example.controllers;

import org.example.repository.DatabaseHelper;
import org.example.services.ExpressionEvaluator;
import org.example.validators.MathEquationValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * WARNING: Due to shared static state and the use of system resources (System.in and System.out),
 * tests in this class should be run individually and not together as a suite. Running tests together
 * may lead to interference and false failures. This limitation is noted for maintenance purposes and
 * reflects the static design of the tested components.
 */

@ExtendWith(MockitoExtension.class)
public class MathAssistantControllerTest {

    private MockedStatic<DatabaseHelper> mockedDatabaseHelper;
    private MockedStatic<MathEquationValidator> mockedMathEquationValidator;
    private MockedStatic<ExpressionEvaluator> mockedExpressionEvaluator;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private ByteArrayInputStream inContent;

    @BeforeEach
    void setUp() {
        mockedDatabaseHelper = mockStatic(DatabaseHelper.class);
        mockedMathEquationValidator = mockStatic(MathEquationValidator.class);
        mockedExpressionEvaluator = mockStatic(ExpressionEvaluator.class);
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() throws IOException {
        mockedDatabaseHelper.close();
        mockedMathEquationValidator.close();
        mockedExpressionEvaluator.close();
        System.setOut(originalOut);
        if (inContent != null) {
            inContent.close();
        }
    }

    void provideInput(String data) {
        inContent = new ByteArrayInputStream(data.getBytes());
        System.setIn(inContent);
    }

    @Test
    void addEquationToDB_ValidEquation_AddedSuccessfully() throws SQLException {
        provideInput("\nx*2-4\n2\n");
        mockedMathEquationValidator.when(() -> MathEquationValidator.isValidEquation(anyString())).thenReturn(true);

        mockedDatabaseHelper.when(() -> DatabaseHelper.saveEquation(anyString())).then(invocation -> null);

        MathAssistantController.addEquationToDB();
        assertTrue(outContent.toString().contains("Equation is valid."));
    }

    @Test
    void addEquationToDB_InvalidEquation_InvalidEquationMessage() {
        provideInput("\ninvalid\n2\n");
        mockedMathEquationValidator.when(() -> MathEquationValidator.isValidEquation(anyString())).thenReturn(false);

        MathAssistantController.addEquationToDB();

        assertTrue(outContent.toString().contains("Equation is invalid."));
    }

    @Test
    void findEquationsByRoots_CorrectData_FoundSuccessfully() throws SQLException {
        provideInput("\n1 -2\n");
        List<Double> roots = Arrays.asList(1.0, -2.0);
        List<String> equations = Arrays.asList("x*2 - x - 2 = 0", "x*2 + 2 = 0");
        mockedDatabaseHelper.when(() -> DatabaseHelper.findEquationsByRoots(anyList())).thenReturn(equations);

        MathAssistantController.findEquationsByRoots();

        assertTrue(outContent.toString().contains("List of equations that contain one of these roots:"));
        assertTrue(outContent.toString().contains("[1] x*2 - x - 2 = 0"));
        assertTrue(outContent.toString().contains("[2] x*2 + 2 = 0"));
    }

    @Test
    void addEquationToDB_InvalidEquation_EquationIsInvalidMsg() {
        provideInput("\ninvalid\n2\n");
        when(MathEquationValidator.isValidEquation(anyString())).thenReturn(false);

        MathAssistantController.addEquationToDB();

        assertTrue(outContent.toString().contains("Equation is invalid."));
    }

    @Test
    void findEquationsByRoots_CorrectRoots_FoundSuccessfully() throws SQLException {
        List<Double> roots = Arrays.asList(1.0, -2.0);
        List<String> equations = Arrays.asList("x*2 - x - 2 = 0", "x*2 + 3x + 2 = 0");
        mockedDatabaseHelper.when(() -> DatabaseHelper.findEquationsByRoots(anyList())).thenReturn(equations);

        provideInput("\n1 -2\n");
        MathAssistantController.findEquationsByRoots();

        assertTrue(outContent.toString().contains("List of equations that contain one of these roots:"));
        assertTrue(outContent.toString().contains("[1] x*2 - x - 2 = 0"));
        assertTrue(outContent.toString().contains("[2] x*2 + 3x + 2 = 0"));
    }

    @Test
    void findUnsolvedEquations_equationsFound_FoundSuccessfully() throws SQLException {
        List<String> equations = Arrays.asList("x*3 - 1 = 0", "x*2 - 4 = 0");
        mockedDatabaseHelper.when(DatabaseHelper::findUnsolvedEquations).thenReturn(equations);

        provideInput("\n1\n1\n2.0\n\n");

        mockedExpressionEvaluator.when(() -> ExpressionEvaluator.checkEquationRoot(anyString(), anyDouble())).thenReturn(true);

        MathAssistantController.findUnsolvedEquations();

        assertTrue(outContent.toString().contains("List of unsolved equations:"));
        assertTrue(outContent.toString().contains("[1] x*3 - 1 = 0"));
        assertTrue(outContent.toString().contains("Your answer is correct!!!"));
    }


    @Test
    void checkRoot_CorrectAnswer_ReturnAnswerIsCorrect() throws SQLException {
        provideInput("\n2.0\n\n");

        mockedExpressionEvaluator.when(() -> ExpressionEvaluator.checkEquationRoot(anyString(), eq(2.0))).thenReturn(true);

        MathAssistantController.checkRoot("x*2 - 4 = 0");

        assertTrue(outContent.toString().contains("Your answer is correct!!!"));
    }

    @Test
    void CheckRoot_IncorrectAnswer_returnAnswerIsIncorrect() throws SQLException {
        provideInput("\n3.0\n2\n");
        mockedExpressionEvaluator.when(() -> ExpressionEvaluator.checkEquationRoot(anyString(), eq(3.0))).thenReturn(false);

        MathAssistantController.checkRoot("x*2 - 4 = 0");

        assertTrue(outContent.toString().contains("Your answer is incorrect"));
    }


}
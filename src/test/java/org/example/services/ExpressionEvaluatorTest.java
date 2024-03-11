package org.example.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionEvaluatorTest {

    @Test
    void transformInfixToPostfix_BasicOperations() {
        assertEquals("3 4 +", ExpressionEvaluator.transformInfixToPostfix("3 + 4").get());
        assertEquals("3 4 -", ExpressionEvaluator.transformInfixToPostfix("3 - 4").get());
        assertEquals("3 4 *", ExpressionEvaluator.transformInfixToPostfix("3 * 4").get());
        assertEquals("3 4 /", ExpressionEvaluator.transformInfixToPostfix("3 / 4").get());
    }

    @Test
    void transformInfixToPostfix_WithUnaryMinus() {
        assertEquals("3 ~ 4 +", ExpressionEvaluator.transformInfixToPostfix("-3 + 4").get());
    }

    @Test
    void transformInfixToPostfix_WithParentheses() {
        assertEquals("3 4 + 5 *", ExpressionEvaluator.transformInfixToPostfix("(3 + 4) * 5").get());
        assertEquals("3 4 5 + *", ExpressionEvaluator.transformInfixToPostfix("3 * (4 + 5)").get());
    }

    @Test
    void transformInfixToPostfix_ComplexExpression() {
        assertEquals("3 4 + 5 * 6 -", ExpressionEvaluator.transformInfixToPostfix("(3 + 4) * 5 - 6").get());
    }

    @Test
    void solvePostfix_SimpleOperations() {
        assertEquals(7.0, ExpressionEvaluator.solvePostfix("3 4 +"));
        assertEquals(-1.0, ExpressionEvaluator.solvePostfix("3 4 -"));
        assertEquals(12.0, ExpressionEvaluator.solvePostfix("3 4 *"));
        assertEquals(0.75, ExpressionEvaluator.solvePostfix("3 4 /"));
    }

    @Test
    void solvePostfix_UnaryMinus() {
        assertEquals(-3.0, ExpressionEvaluator.solvePostfix("3 ~"));
    }

    @Test
    void solvePostfix_ComplexExpression() {
        assertEquals(29.0, ExpressionEvaluator.solvePostfix("3 4 + 5 * 6 -"));
    }

    @Test
    void solvePostfix_InvalidExpression() {
        assertThrows(NumberFormatException.class, () -> ExpressionEvaluator.solvePostfix("3 +"));
    }

    @Test
    void checkEquationRoot_CorrectRoot() {
        assertTrue(ExpressionEvaluator.checkEquationRoot("x*2-4=0", 2.0));
    }

    @Test
    void checkEquationRoot_IncorrectRoot() {
        assertFalse(ExpressionEvaluator.checkEquationRoot("x*2-4=0", 3.0));
    }

    @Test
    void checkEquationRoot_ComplexExpression() {
        assertTrue(ExpressionEvaluator.checkEquationRoot("x*2+2=0", -1.0));
    }

}


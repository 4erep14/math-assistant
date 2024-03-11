package org.example.validators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MathEquationValidatorTest {

    @Test
    void isValidEquation_CorrectEquation_ReturnsTrue() {
        assertTrue(MathEquationValidator.isValidEquation("x+2=3"));
        assertTrue(MathEquationValidator.isValidEquation("2*x-3=2*(1+x)"));
        assertTrue(MathEquationValidator.isValidEquation("-x=-1"));
        assertTrue(MathEquationValidator.isValidEquation("3.5x+2=3.5"));
        assertTrue(MathEquationValidator.isValidEquation("(x+2)=(3-1)"));
    }

    @Test
    void isValidEquation_IncorrectEquation_ReturnsFalse() {
        assertFalse(MathEquationValidator.isValidEquation("x++2=3"));
        assertFalse(MathEquationValidator.isValidEquation("2*x-=3"));
        assertFalse(MathEquationValidator.isValidEquation("x+=3"));
        assertFalse(MathEquationValidator.isValidEquation("=x+2"));
        assertFalse(MathEquationValidator.isValidEquation("x+2=3=4"));
        assertFalse(MathEquationValidator.isValidEquation("x+2=3.5.2"));
    }

    @Test
    void isValidEquation_InvalidBrackets_ReturnsFalse() {
        assertFalse(MathEquationValidator.isValidEquation("(x+2=3"));
        assertFalse(MathEquationValidator.isValidEquation("x+(2=3)"));
        assertFalse(MathEquationValidator.isValidEquation("x+2)=3"));
        assertFalse(MathEquationValidator.isValidEquation("(x+2)=(3)-"));
    }

    @Test
    void isValidEquation_MissingOperand_ReturnsFalse() {
        assertFalse(MathEquationValidator.isValidEquation("x+=2"));
        assertFalse(MathEquationValidator.isValidEquation("=x+2"));
    }

    @Test
    void isValidEquation_UnaryMinus_ReturnsTrue() {
        assertTrue(MathEquationValidator.isValidEquation("-x+2=-1"));
        assertTrue(MathEquationValidator.isValidEquation("x*-1=2"));
    }

    @Test
    void isValidEquation_IncorrectUseOfUnaryMinus_ReturnsFalse() {
        assertFalse(MathEquationValidator.isValidEquation("x--=2"));
        assertFalse(MathEquationValidator.isValidEquation("x*-=2"));
    }
}

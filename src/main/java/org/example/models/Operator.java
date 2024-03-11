package org.example.models;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public enum Operator {

    PLUS(1, Double::sum),
    MINUS(1, (l, r) -> l - r),
    MULTIPLY(2, (l, r) -> l * r),
    DIVIDE(2, (l, r) -> l / r),
    UNARY_MINUS(3, null, v -> -v);

    private final int priority;
    private final DoubleBinaryOperator binaryOperator;
    private final DoubleUnaryOperator unaryOperator;

    Operator(final int priority, final DoubleBinaryOperator binaryOperator) {
        this(priority, binaryOperator, null);
    }

    Operator(final int priority, final DoubleUnaryOperator unaryOperator) {
        this(priority, null, unaryOperator);
    }

    private Operator(final int priority, final DoubleBinaryOperator binaryOperator, final DoubleUnaryOperator unaryOperator) {
        this.priority = priority;
        this.binaryOperator = binaryOperator;
        this.unaryOperator = unaryOperator;
    }

    public double compute(final double left, final double right) {
        if (binaryOperator == null) {
            throw new UnsupportedOperationException("Not a binary operator");
        }
        return binaryOperator.applyAsDouble(left, right);
    }

    public double compute(final double value) {
        if (unaryOperator == null) {
            throw new UnsupportedOperationException("Not a unary operator");
        }
        return unaryOperator.applyAsDouble(value);
    }

    public int getPriority() {
        return this.priority;
    }

    public boolean isUnary() {
        return unaryOperator != null;
    }
}
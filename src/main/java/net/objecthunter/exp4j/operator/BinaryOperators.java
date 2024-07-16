package net.objecthunter.exp4j.operator;

import java.util.function.DoubleBinaryOperator;

import static net.objecthunter.exp4j.operator.Operators.*;

public enum BinaryOperators implements Operator {

    ADDITION("+", true, PRECEDENCE_ADDITION, Double::sum),
    SUBTRACTION("-", true, PRECEDENCE_SUBTRACTION, (a, b) -> a - b),
    MULTIPLICATION("*", true, PRECEDENCE_MULTIPLICATION, (a, b) -> a * b),
    DIVISION("/", true, PRECEDENCE_DIVISION, (a, b) -> a / b),
    POWER("^", false, PRECEDENCE_POWER, Math::pow),
    MODULO("%", true, PRECEDENCE_MODULO, (a, b) -> a % b);

    private final String symbol;
    private final boolean leftAssociative;
    private final int precedence;
    private final DoubleBinaryOperator operator;

    BinaryOperators(String symbol, boolean leftAssociative, int precedence, DoubleBinaryOperator operator) {
        this.symbol = symbol;
        this.leftAssociative = leftAssociative;
        this.precedence = precedence;
        this.operator = operator;
    }

    @Override
    public boolean isLeftAssociative() {
        return leftAssociative;
    }

    @Override
    public int getPrecedence() {
        return precedence;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public int getNumOperands() {
        return 2;
    }

    @Override
    public double apply(double... doubles) {
        return operator.applyAsDouble(doubles[0], doubles[1]);
    }
}

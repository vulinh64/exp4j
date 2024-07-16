package net.objecthunter.exp4j;

import net.objecthunter.exp4j.operator.Operator;

public class AbstractOperator implements Operator {

    public AbstractOperator(String symbol, int numOperands, boolean leftAssociative, int precedence) {
        this.symbol = symbol;
        this.numOperands = numOperands;
        this.leftAssociative = leftAssociative;
        this.precedence = precedence;
    }

    private final String symbol;
    private final int numOperands;
    private final boolean leftAssociative;
    private final int precedence;

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
        return numOperands;
    }

    @Override
    public double apply(double... doubles) {
        return 0;
    }
}

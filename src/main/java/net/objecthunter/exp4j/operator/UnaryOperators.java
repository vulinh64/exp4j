package net.objecthunter.exp4j.operator;

import java.util.function.DoubleUnaryOperator;

import static net.objecthunter.exp4j.operator.Operators.PRECEDENCE_UNARY_MINUS;
import static net.objecthunter.exp4j.operator.Operators.PRECEDENCE_UNARY_PLUS;

public enum UnaryOperators implements Operator {
  UNARY_MINUS("-", PRECEDENCE_UNARY_MINUS, a -> -a),
  UNARY_PLUS("+", PRECEDENCE_UNARY_PLUS, a -> a);

  private final String symbol;
  private final int precedence;
  private final DoubleUnaryOperator operator;

  UnaryOperators(String symbol, int precedence, DoubleUnaryOperator operator) {
    this.symbol = symbol;
    this.precedence = precedence;
    this.operator = operator;
  }

  @Override
  public boolean isLeftAssociative() {
    return false;
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
    return 1;
  }

  @Override
  public double apply(double... doubles) {
    return operator.applyAsDouble(doubles[0]);
  }
}

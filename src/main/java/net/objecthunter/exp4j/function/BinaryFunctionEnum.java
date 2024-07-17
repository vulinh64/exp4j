package net.objecthunter.exp4j.function;

import java.util.function.DoubleBinaryOperator;

public enum BinaryFunctionEnum implements Function {
  POW("pow", Math::pow),
  LOGB("logb", (a, b) -> Math.log(a) / Math.log(b));

  private final String functionName;
  private final DoubleBinaryOperator function;

  BinaryFunctionEnum(String functionName, DoubleBinaryOperator function) {
    this.functionName = functionName;
    this.function = function;
  }

  @Override
  public String getName() {
    return functionName;
  }

  @Override
  public int getNumArguments() {
    return 2;
  }

  @Override
  public double apply(double... args) {
    return function.applyAsDouble(args[0], args[1]);
  }
}

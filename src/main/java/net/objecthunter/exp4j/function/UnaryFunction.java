package net.objecthunter.exp4j.function;

import java.util.function.DoubleUnaryOperator;

/**
 * Function that takes a single double operand
 */
public class UnaryFunction extends AbstractFunction {

    private final DoubleUnaryOperator function;

    protected UnaryFunction(String name, DoubleUnaryOperator function) {
        super(name);
        this.function = function;
    }

    @Override
    public double apply(double... operands) {
        return function.applyAsDouble(operands[0]);
    }
}

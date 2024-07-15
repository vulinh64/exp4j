package net.objecthunter.exp4j.function;

import java.util.function.DoubleUnaryOperator;

public class UnaryFunction extends AbstractFunction {

    private final DoubleUnaryOperator function;

    protected UnaryFunction(String name, DoubleUnaryOperator function) {
        super(name);
        this.function = function;
    }

    @Override
    public double apply(double... args) {
        return function.applyAsDouble(args[0]);
    }
}

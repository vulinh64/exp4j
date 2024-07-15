package net.objecthunter.exp4j.function;

import java.util.function.DoubleBinaryOperator;

public class BinaryFunction extends AbstractFunction {

    private final DoubleBinaryOperator function;

    protected BinaryFunction(String name, DoubleBinaryOperator function) {
        super(name,2);
        this.function = function;
    }

    @Override
    public double apply(double... args) {
        return function.applyAsDouble(args[0], args[1]);
    }
}

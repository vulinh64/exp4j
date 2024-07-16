package net.objecthunter.exp4j.function;

import java.util.function.DoubleUnaryOperator;

public enum UnaryFunctionEnum implements Function {
    SIN("sin", Math::sin),
    COS("cos", Math::cos),
    TAN("tan", Math::tan),
    COT("cot", a -> 1d / Math.tan(a)),
    LOG("log", Math::log),
    LOG2("log2", a -> Math.log(a) / Math.log(2d)),
    LOG10("log10", Math::log10),
    LOG1P("log1p", Math::log1p),
    ABS("abs", Math::abs),
    ACOS("acos", Math::acos),
    ASIN("asin", Math::asin),
    ATAN("atan", Math::atan),
    CBRT("cbrt", Math::cbrt),
    FLOOR("floor", Math::floor),
    SINH("sinh", Math::sinh),
    SQRT("sqrt", Math::sqrt),
    TANH("tanh", Math::tanh),
    COSH("cosh", Math::cosh),
    CEIL("ceil", Math::ceil),
    EXP("exp", Math::exp),
    EXPM1("expm1", Math::expm1),
    SIGNUM("signum", a -> a > 0 ? 1 : signumLt0(a)),
    CSC("csc", a -> 1d / Math.sin(a)),
    SEC("sec", a -> 1d / Math.cos(a)),
    CSCH("csch", a -> 1d / Math.sinh(a)),
    SECH("sech", a -> 1d / Math.cosh(a)),
    COTH("coth", a -> Math.cosh(a) / Math.sinh(a)),
    TO_RADIAN("toradian", Math::toRadians),
    TO_DEGREE("todegree", Math::toDegrees);

    private final String functionName;
    private final DoubleUnaryOperator function;

    UnaryFunctionEnum(String functionName, DoubleUnaryOperator function) {
        this.functionName = functionName;
        this.function = function;
    }

    @Override
    public String getName() {
        return functionName;
    }

    @Override
    public int getNumArguments() {
        return 1;
    }

    @Override
    public double apply(double... args) {
        return function.applyAsDouble(args[0]);
    }

    private static int signumLt0(double a) {
        return a < 0 ? -1 : 0;
    }
}

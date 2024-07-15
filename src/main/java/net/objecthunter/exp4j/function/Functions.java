/*
 * Copyright 2014 Frank Asseg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.objecthunter.exp4j.function;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class representing the builtin functions available for use in expressions
 */
public class Functions {

    private Functions() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    private static final int INDEX_SIN = 0;
    private static final int INDEX_COS = 1;
    private static final int INDEX_TAN = 2;
    private static final int INDEX_CSC = 3;
    private static final int INDEX_SEC = 4;
    private static final int INDEX_COT = 5;
    private static final int INDEX_SINH = 6;
    private static final int INDEX_COSH = 7;
    private static final int INDEX_TANH = 8;
    private static final int INDEX_CSCH = 9;
    private static final int INDEX_SECH = 10;
    private static final int INDEX_COTH = 11;
    private static final int INDEX_ASIN = 12;
    private static final int INDEX_ACOS = 13;
    private static final int INDEX_ATAN = 14;
    private static final int INDEX_SQRT = 15;
    private static final int INDEX_CBRT = 16;
    private static final int INDEX_ABS = 17;
    private static final int INDEX_CEIL = 18;
    private static final int INDEX_FLOOR = 19;
    private static final int INDEX_POW = 20;
    private static final int INDEX_EXP = 21;
    private static final int INDEX_EXPM1 = 22;
    private static final int INDEX_LOG10 = 23;
    private static final int INDEX_LOG2 = 24;
    private static final int INDEX_LOG = 25;
    private static final int INDEX_LOG1P = 26;
    private static final int INDEX_LOGB = 27;
    private static final int INDEX_SGN = 28;
    private static final int INDEX_TO_RADIAN = 29;
    private static final int INDEX_TO_DEGREE = 30;

    private static final Function[] BUILT_IN_FUNCTIONS = new Function[31];

    static {
        BUILT_IN_FUNCTIONS[INDEX_SIN] = new UnaryFunction("sin", Math::sin);
        BUILT_IN_FUNCTIONS[INDEX_COS] = new UnaryFunction("cos", Math::cos);
        BUILT_IN_FUNCTIONS[INDEX_TAN] = new UnaryFunction("tan", Math::tan);
        BUILT_IN_FUNCTIONS[INDEX_COT] = new UnaryFunction("cot", a -> 1d / Math.tan(a));
        BUILT_IN_FUNCTIONS[INDEX_LOG] = new UnaryFunction("log", Math::log);
        BUILT_IN_FUNCTIONS[INDEX_LOG2] = new UnaryFunction("log2", a -> Math.log(a) / Math.log(2d));
        BUILT_IN_FUNCTIONS[INDEX_LOG10] = new UnaryFunction("log10", Math::log10);
        BUILT_IN_FUNCTIONS[INDEX_LOG1P] = new UnaryFunction("log1p", Math::log1p);
        BUILT_IN_FUNCTIONS[INDEX_ABS] = new UnaryFunction("abs", Math::abs);
        BUILT_IN_FUNCTIONS[INDEX_ACOS] = new UnaryFunction("acos", Math::acos);
        BUILT_IN_FUNCTIONS[INDEX_ASIN] = new UnaryFunction("asin", Math::asin);
        BUILT_IN_FUNCTIONS[INDEX_ATAN] = new UnaryFunction("atan", Math::atan);
        BUILT_IN_FUNCTIONS[INDEX_CBRT] = new UnaryFunction("cbrt", Math::cbrt);
        BUILT_IN_FUNCTIONS[INDEX_FLOOR] = new UnaryFunction("floor", Math::floor);
        BUILT_IN_FUNCTIONS[INDEX_SINH] = new UnaryFunction("sinh", Math::sinh);
        BUILT_IN_FUNCTIONS[INDEX_SQRT] = new UnaryFunction("sqrt", Math::sqrt);
        BUILT_IN_FUNCTIONS[INDEX_TANH] = new UnaryFunction("tanh", Math::tanh);
        BUILT_IN_FUNCTIONS[INDEX_COSH] = new UnaryFunction("cosh", Math::cosh);
        BUILT_IN_FUNCTIONS[INDEX_CEIL] = new UnaryFunction("ceil", Math::ceil);
        BUILT_IN_FUNCTIONS[INDEX_POW] = new BinaryFunction("pow", Math::pow);
        BUILT_IN_FUNCTIONS[INDEX_EXP] = new UnaryFunction("exp", Math::exp);
        BUILT_IN_FUNCTIONS[INDEX_EXPM1] = new UnaryFunction("expm1", Math::expm1);
        BUILT_IN_FUNCTIONS[INDEX_SGN] = new UnaryFunction("signum", a -> a > 0 ? 1 : signumLt0(a));
        BUILT_IN_FUNCTIONS[INDEX_CSC] = new UnaryFunction("csc", a -> 1d / Math.sin(a));
        BUILT_IN_FUNCTIONS[INDEX_SEC] = new UnaryFunction("sec", a -> 1d / Math.cos(a));
        BUILT_IN_FUNCTIONS[INDEX_CSCH] = new UnaryFunction("csch", a -> 1d / Math.sinh(a));
        BUILT_IN_FUNCTIONS[INDEX_SECH] = new UnaryFunction("sech", a -> 1d / Math.cosh(a));
        BUILT_IN_FUNCTIONS[INDEX_COTH] = new UnaryFunction("coth", a -> Math.cosh(a) / Math.sinh(a));
        BUILT_IN_FUNCTIONS[INDEX_LOGB] = new BinaryFunction("logb", (a, b) -> Math.log(a) / Math.log(b));
        BUILT_IN_FUNCTIONS[INDEX_TO_RADIAN] = new UnaryFunction("toradian", Math::toRadians);
        BUILT_IN_FUNCTIONS[INDEX_TO_DEGREE] = new UnaryFunction("todegree", Math::toDegrees);
    }

    private static final Map<String, Function> MAPS = Arrays.stream(BUILT_IN_FUNCTIONS)
            .collect(Collectors.toMap(
                    Function::getName,
                    java.util.function.Function.identity()));

    /**
     * Get the builtin function for a given name
     *
     * @param name te name of the function
     * @return a Function instance
     */
    public static Function getBuiltinFunction(String name) {
        return MAPS.get(name);
    }

    private static int signumLt0(double a) {
        return a < 0 ? -1 : 0;
    }
}

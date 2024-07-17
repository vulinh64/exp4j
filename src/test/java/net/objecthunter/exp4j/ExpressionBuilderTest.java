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

package net.objecthunter.exp4j;

import net.objecthunter.exp4j.function.AbstractFunction;
import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.operator.AbstractOperator;
import net.objecthunter.exp4j.operator.Operator;
import net.objecthunter.exp4j.operator.Operators;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Math.*;
import static org.junit.jupiter.api.Assertions.*;

class ExpressionBuilderTest {

    @Test
    void testExpressionBuilder1() {
        double result = new ExpressionBuilder("2+1")
                .build()
                .evaluate();
        assertEquals(3d, result, 0d);
    }

    @Test
    void testExpressionBuilder2() {
        double result = new ExpressionBuilder("cos(x)")
                .variables("x")
                .build()
                .setVariable("x", Math.PI)
                .evaluate();
        assertEquals(-1d, result, 0d);
    }

    @Test
    void testExpressionBuilder3() {
        double x = Math.PI;
        double result = new ExpressionBuilder("sin(x)-log(3*x/4)")
                .variables("x")
                .build()
                .setVariable("x", x)
                .evaluate();

        double expected = sin(x) - log(3 * x / 4);
        assertEquals(expected, result, 0d);
    }

    @Test
    void testExpressionBuilder4() {
        Function log2 = new AbstractFunction("log2") {

            @Override
            public double apply(double... args) {
                return Math.log(args[0]) / Math.log(2);
            }
        };
        double result = new ExpressionBuilder("log2(4)")
                .function(log2)
                .build()
                .evaluate();

        double expected = 2;
        assertEquals(expected, result, 0d);
    }

    @Test
    void testExpressionBuilder5() {
        Function avg = new AbstractFunction("avg", 4) {

            @Override
            public double apply(double... args) {
                double sum = 0;
                for (double arg : args) {
                    sum += arg;
                }
                return sum / args.length;
            }
        };
        double result = new ExpressionBuilder("avg(1,2,3,4)")
                .function(avg)
                .build()
                .evaluate();

        double expected = 2.5d;
        assertEquals(expected, result, 0d);
    }

    @Test
    void testExpressionBuilder6() {
        Operator factorial = new AbstractOperator("!", 1, true, Operators.PRECEDENCE_POWER + 1) {

            @Override
            public double apply(double... args) {
                int arg = (int) args[0];
                if ((double) arg != args[0]) {
                    throw new IllegalArgumentException("Operand for factorial has to be an integer");
                }
                if (arg < 0) {
                    throw new IllegalArgumentException("The operand of the factorial can not be less than zero");
                }
                double result = 1;
                for (int i = 1; i <= arg; i++) {
                    result *= i;
                }
                return result;
            }
        };

        double result = new ExpressionBuilder("3!")
                .operator(factorial)
                .build()
                .evaluate();

        double expected = 6d;
        assertEquals(expected, result, 0d);
    }

    @Test
    void testExpressionBuilder7() {
        ValidationResult res = new ExpressionBuilder("x")
                .variables("x")
                .build()
                .validate();
        assertFalse(res.isValid());
        assertEquals(1, res.getErrors().size());
    }

    @Test
    void testExpressionBuilder8() {
        ValidationResult res = new ExpressionBuilder("x*y*z")
                .variables("x", "y", "z")
                .build()
                .validate();
        assertFalse(res.isValid());
        assertEquals(3, res.getErrors().size());
    }

    @Test
    void testExpressionBuilder9() {
        ValidationResult res = new ExpressionBuilder("x")
                .variables("x")
                .build()
                .setVariable("x", 1d)
                .validate();
        assertTrue(res.isValid());
    }

    @Test
    void testValidationDocExample() {
        Expression e = new ExpressionBuilder("x")
                .variables("x")
                .build();
        ValidationResult res = e.validate();
        assertFalse(res.isValid());
        assertEquals(1, res.getErrors().size());

        e.setVariable("x", 1d);
        res = e.validate();
        assertTrue(res.isValid());
    }

    @Test
    void testExpressionBuilder10() {
        double result = new ExpressionBuilder("1e1")
                .build()
                .evaluate();
        assertEquals(10d, result, 0d);
    }

    @Test
    void testExpressionBuilder11() {
        double result = new ExpressionBuilder("1.11e-1")
                .build()
                .evaluate();
        assertEquals(0.111d, result, 0d);
    }

    @Test
    void testExpressionBuilder12() {
        double result = new ExpressionBuilder("1.11e+1")
                .build()
                .evaluate();
        assertEquals(11.1d, result, 0d);
    }

    @Test
    void testExpressionBuilder13() {
        double result = new ExpressionBuilder("-3^2")
                .build()
                .evaluate();
        assertEquals(-9d, result, 0d);
    }

    @Test
    void testExpressionBuilder14() {
        double result = new ExpressionBuilder("(-3)^2")
                .build()
                .evaluate();
        assertEquals(9d, result, 0d);
    }

    @Test
    void testExpressionBuilder15() {
        double result = new ExpressionBuilder("-3/0")
                .build()
                .evaluate();

        assertEquals(Double.NEGATIVE_INFINITY, result, 0.0);
    }

    @Test
    void testExpressionBuilder16() {
        new ExpressionBuilder("log(x) - y * (sqrt(x^cos(y)))")
                .variables("x", "y")
                .build()
                .setVariable("x", 1d)
                .setVariable("y", 2d)
                .evaluate();
    }

    @Test
    void testExpressionBuilder17() {
        Expression e = new ExpressionBuilder("x-y*")
                .variables("x", "y")
                .build();
        ValidationResult res = e.validate(false);
        assertFalse(res.isValid());
        assertEquals(1, res.getErrors().size());
        assertEquals("Too many operators", res.getErrors().get(0));
    }

    @Test
    void testExpressionBuilder18() {
        Expression e = new ExpressionBuilder("log(x) - y *")
                .variables("x", "y")
                .build();
        ValidationResult res = e.validate(false);
        assertFalse(res.isValid());
        assertEquals(1, res.getErrors().size());
        assertEquals("Too many operators", res.getErrors().get(0));
    }

    @Test
    void testExpressionBuilder19() {
        Expression e = new ExpressionBuilder("x - y *")
                .variables("x", "y")
                .build();
        ValidationResult res = e.validate(false);
        assertFalse(res.isValid());
        assertEquals(1, res.getErrors().size());
        assertEquals("Too many operators", res.getErrors().get(0));
    }

    /* legacy tests from earlier exp4j versions */

    @Test
    void testFunction1() {
        Function custom = new AbstractFunction("timespi") {

            @Override
            public double apply(double... values) {
                return values[0] * Math.PI;
            }
        };
        Expression e = new ExpressionBuilder("timespi(x)")
                .function(custom)
                .variables("x")
                .build()
                .setVariable("x", 1);
        double result = e.evaluate();
        assertEquals(PI, result, 0.0);
    }

    @Test
    void testFunction2() {
        Function custom = new AbstractFunction("loglog") {

            @Override
            public double apply(double... values) {
                return Math.log(Math.log(values[0]));
            }
        };
        Expression e = new ExpressionBuilder("loglog(x)")
                .variables("x")
                .function(custom)
                .build()
                .setVariable("x", 1);
        double result = e.evaluate();
        assertEquals(result, log(log(1)), 0.0);
    }

    @Test
    void testFunction3() {
        Function custom1 = new AbstractFunction("foo") {

            @Override
            public double apply(double... values) {
                return values[0] * Math.E;
            }
        };
        Function custom2 = new AbstractFunction("bar") {

            @Override
            public double apply(double... values) {
                return values[0] * Math.PI;
            }
        };
        Expression e = new ExpressionBuilder("foo(bar(x))")
                .function(custom1)
                .function(custom2)
                .variables("x")
                .build()
                .setVariable("x", 1);
        double result = e.evaluate();
        assertEquals(result, 1 * E * PI, 0.0);
    }

    @Test
    void testFunction4() {
        Function custom1 = new AbstractFunction("foo") {

            @Override
            public double apply(double... values) {
                return values[0] * Math.E;
            }
        };
        double varX = 32.24979131d;
        Expression e = new ExpressionBuilder("foo(log(x))")
                .variables("x")
                .function(custom1)
                .build()
                .setVariable("x", varX);
        double result = e.evaluate();
        assertEquals(result, log(varX) * E, 0.0);
    }

    @Test
    void testFunction5() {
        Function custom1 = new AbstractFunction("foo") {

            @Override
            public double apply(double... values) {
                return values[0] * Math.E;
            }
        };
        Function custom2 = new AbstractFunction("bar") {

            @Override
            public double apply(double... values) {
                return values[0] * Math.PI;
            }
        };
        double varX = 32.24979131d;
        Expression e = new ExpressionBuilder("bar(foo(log(x)))")
                .variables("x")
                .function(custom1)
                .function(custom2)
                .build()
                .setVariable("x", varX);
        double result = e.evaluate();
        assertEquals(result, log(varX) * E * PI, 0.0);
    }

    @Test
    void testFunction6() {
        Function custom1 = new AbstractFunction("foo") {

            @Override
            public double apply(double... values) {
                return values[0] * Math.E;
            }
        };
        Function custom2 = new AbstractFunction("bar") {

            @Override
            public double apply(double... values) {
                return values[0] * Math.PI;
            }
        };
        double varX = 32.24979131d;
        Expression e = new ExpressionBuilder("bar(foo(log(x)))")
                .variables("x")
                .functions(custom1, custom2)
                .build()
                .setVariable("x", varX);
        double result = e.evaluate();
        assertEquals(result, log(varX) * E * PI, 0.0);
    }

    @Test
    void testFunction7() {
        Function custom1 = new AbstractFunction("half") {

            @Override
            public double apply(double... values) {
                return values[0] / 2;
            }
        };
        Expression e = new ExpressionBuilder("half(x)")
                .variables("x")
                .function(custom1)
                .build()
                .setVariable("x", 1d);
        assertEquals(0.5d, e.evaluate(), 0.0);
    }

    @Test
    void testFunction10() {
        Function custom1 = new AbstractFunction("max", 2) {

            @Override
            public double apply(double... values) {
                return Math.max(values[0], values[1]);
            }
        };
        Expression e =
                new ExpressionBuilder("max(x,y)")
                        .variables("x", "y")
                        .function(custom1)
                        .build()
                        .setVariable("x", 1d)
                        .setVariable("y", 2d);
        assertEquals(2, e.evaluate(), 0.0);
    }

    @Test
    void testFunction11() {
        Function custom1 = new AbstractFunction("power", 2) {

            @Override
            public double apply(double... values) {
                return Math.pow(values[0], values[1]);
            }
        };
        Expression e =
                new ExpressionBuilder("power(x,y)")
                        .variables("x", "y")
                        .function(custom1)
                        .build()
                        .setVariable("x", 2d)
                        .setVariable("y",
                                4d);
        assertEquals(pow(2, 4), e.evaluate(), 0.0);
    }

    @Test
    void testFunction12() {
        Function custom1 = new AbstractFunction("max", 5) {

            @Override
            public double apply(double... values) {
                double max = values[0];
                for (int i = 1; i < numArguments; i++) {
                    if (values[i] > max) {
                        max = values[i];
                    }
                }
                return max;
            }
        };
        Expression e = new ExpressionBuilder("max(1,2.43311,51.13,43,12)")
                .function(custom1)
                .build();
        assertEquals(51.13d, e.evaluate(), 0.0);
    }

    @Test
    void testFunction13() {
        Function custom1 = new AbstractFunction("max", 3) {

            @Override
            public double apply(double... values) {
                double max = values[0];
                for (int i = 1; i < numArguments; i++) {
                    if (values[i] > max) {
                        max = values[i];
                    }
                }
                return max;
            }
        };
        double varX = Math.E;
        Expression e = new ExpressionBuilder("max(log(x),sin(x),x)")
                .variables("x")
                .function(custom1)
                .build()
                .setVariable("x", varX);
        assertEquals(varX, e.evaluate(), 0.0);
    }

    @Test
    void testFunction14() {
        Function custom1 = new AbstractFunction("multiply", 2) {

            @Override
            public double apply(double... values) {
                return values[0] * values[1];
            }
        };
        double varX = 1;
        Expression e = new ExpressionBuilder("multiply(sin(x),x+1)")
                .variables("x")
                .function(custom1)
                .build()
                .setVariable("x", varX);
        double expected = Math.sin(varX) * (varX + 1);
        double actual = e.evaluate();
        assertEquals(expected, actual, 0.0);
    }

    @Test
    void testFunction15() {
        Function custom1 = new AbstractFunction("timesPi") {

            @Override
            public double apply(double... values) {
                return values[0] * Math.PI;
            }
        };
        double varX = 1;
        Expression e = new ExpressionBuilder("timesPi(x^2)")
                .variables("x")
                .function(custom1)
                .build()
                .setVariable("x", varX);
        double expected = varX * Math.PI;
        double actual = e.evaluate();
        assertEquals(expected, actual, 0.0);
    }

    @Test
    void testFunction16() {
        Function custom1 = new AbstractFunction("multiply", 3) {

            @Override
            public double apply(double... values) {
                return values[0] * values[1] * values[2];
            }
        };
        double varX = 1;
        Expression e = new ExpressionBuilder("multiply(sin(x),x+1^(-2),log(x))")
                .variables("x")
                .function(custom1)
                .build()
                .setVariable("x", varX);
        double expected = Math.sin(varX) * Math.pow((varX + 1), -2) * Math.log(varX);
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testFunction17() {
        Function custom1 = new AbstractFunction("timesPi") {

            @Override
            public double apply(double... values) {
                return values[0] * Math.PI;
            }
        };
        double varX = Math.E;
        Expression e = new ExpressionBuilder("timesPi(log(x^(2+1)))")
                .variables("x")
                .function(custom1)
                .build()
                .setVariable("x", varX);
        double expected = Math.log(Math.pow(varX, 3)) * Math.PI;
        assertEquals(expected, e.evaluate(), 0.0);
    }

    // thanks to Marcin Domanski who issued
    // http://jira.congrace.de/jira/browse/EXP-11
    // i have this test, which fails in 0.2.9
    @Test
    void testFunction18() {
        Function minFunction = new AbstractFunction("min", 2) {

            @Override
            public double apply(double[] values) {
                double currentMin = Double.POSITIVE_INFINITY;
                for (double value : values) {
                    currentMin = Math.min(currentMin, value);
                }
                return currentMin;
            }
        };
        ExpressionBuilder b = new ExpressionBuilder("-min(5, 0) + 10")
                .function(minFunction);
        double calculated = b.build().evaluate();
        assertEquals(10, calculated, 0.0);
    }

    // thanks to Sylvain Machefert who issued
    // http://jira.congrace.de/jira/browse/EXP-11
    // i have this test, which fails in 0.3.2
    @Test
    void testFunction19() {
        Function minFunction = new AbstractFunction("power", 2) {

            @Override
            public double apply(double[] values) {
                return Math.pow(values[0], values[1]);
            }
        };
        ExpressionBuilder b = new ExpressionBuilder("power(2,3)")
                .function(minFunction);
        double calculated = b.build().evaluate();
        assertEquals(Math.pow(2, 3), calculated, 0d);
    }

    // thanks to Narendra Harmwal who noticed that getArgumentCount was not
    // implemented
    // this test has been added in 0.3.5
    @Test
    void testFunction20() {
        Function maxFunction = new AbstractFunction("max", 3) {

            @Override
            public double apply(double... values) {
                double max = values[0];
                for (int i = 1; i < numArguments; i++) {
                    if (values[i] > max) {
                        max = values[i];
                    }
                }
                return max;
            }
        };
        ExpressionBuilder b = new ExpressionBuilder("max(1,2,3)")
                .function(maxFunction);
        double calculated = b.build().evaluate();
        assertEquals(3, maxFunction.getNumArguments());
        assertEquals(3, calculated, 0.0);
    }

    @Test
    void testOperators1() {
        Operator factorial = new AbstractOperator("!", 1, true, Operators.PRECEDENCE_POWER + 1) {

            @Override
            public double apply(double... args) {
                int arg = (int) args[0];
                if ((double) arg != args[0]) {
                    throw new IllegalArgumentException("Operand for factorial has to be an integer");
                }
                if (arg < 0) {
                    throw new IllegalArgumentException("The operand of the factorial can not be less than zero");
                }
                double result = 1;
                for (int i = 1; i <= arg; i++) {
                    result *= i;
                }
                return result;
            }
        };

        Expression e = new ExpressionBuilder("1!").operator(factorial)
                .build();
        assertEquals(1d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("2!").operator(factorial)
                .build();
        assertEquals(2d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("3!").operator(factorial)
                .build();
        assertEquals(6d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("4!").operator(factorial)
                .build();
        assertEquals(24d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("5!").operator(factorial)
                .build();
        assertEquals(120d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("11!").operator(factorial)
                .build();
        assertEquals(39916800d, e.evaluate(), 0.0);
    }

    @Test
    void testOperators2() {
        Operator factorial = new AbstractOperator("!", 1, true, Operators.PRECEDENCE_POWER + 1) {

            @Override
            public double apply(double... args) {
                int arg = (int) args[0];
                if ((double) arg != args[0]) {
                    throw new IllegalArgumentException("Operand for factorial has to be an integer");
                }
                if (arg < 0) {
                    throw new IllegalArgumentException("The operand of the factorial can not be less than zero");
                }
                double result = 1;
                for (int i = 1; i <= arg; i++) {
                    result *= i;
                }
                return result;
            }
        };
        Expression e = new ExpressionBuilder("2^3!").operator(factorial)
                .build();
        assertEquals(64d, e.evaluate(), 0d);
        e = new ExpressionBuilder("3!^2").operator(factorial)
                .build();
        assertEquals(36d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("-(3!)^-1").operator(factorial)
                .build();
        double actual = e.evaluate();
        assertEquals(Math.pow(-6d, -1), actual, 0d);
    }

    @Test
    void testOperators3() {
        Operator gteq = new AbstractOperator(">=", 2, true, Operators.PRECEDENCE_ADDITION - 1) {

            @Override
            public double apply(double[] values) {
                if (values[0] >= values[1]) {
                    return 1d;
                } else {
                    return 0d;
                }
            }
        };
        Expression e = new ExpressionBuilder("1>=2").operator(gteq)
                .build();
        assertEquals(0d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("2>=1").operator(gteq)
                .build();
        assertEquals(1d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("-2>=1").operator(gteq)
                .build();
        assertEquals(0d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("-2>=-1").operator(gteq)
                .build();
        assertEquals(0d, e.evaluate(), 0.0);
    }

    @Test
    void testModulo1() {
        double result = new ExpressionBuilder("33%(20/2)%2")
                .build().evaluate();
        assertEquals(1d, result, 0.0);
    }

    @Test
    void testOperators4() {
        Operator greaterEq = new AbstractOperator(">=", 2, true, 4) {

            @Override
            public double apply(double[] values) {
                if (values[0] >= values[1]) {
                    return 1d;
                } else {
                    return 0d;
                }
            }
        };
        Operator greater = new AbstractOperator(">", 2, true, 4) {

            @Override
            public double apply(double[] values) {
                if (values[0] > values[1]) {
                    return 1d;
                } else {
                    return 0d;
                }
            }
        };
        Operator newPlus = new AbstractOperator(">=>", 2, true, 4) {

            @Override
            public double apply(double[] values) {
                return values[0] + values[1];
            }
        };
        Expression e = new ExpressionBuilder("1>2").operator(greater)
                .build();
        assertEquals(0d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("2>=2").operator(greaterEq)
                .build();
        assertEquals(1d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("1>=>2").operator(newPlus)
                .build();
        assertEquals(3d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("1>=>2>2").operator(greater).operator(newPlus)
                .build();
        assertEquals(1d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("1>=>2>2>=1").operator(greater).operator(newPlus)
                .operator(greaterEq)
                .build();
        assertEquals(1d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("1 >=> 2 > 2 >= 1").operator(greater).operator(newPlus)
                .operator(greaterEq)
                .build();
        assertEquals(1d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("1 >=> 2 >= 2 > 1").operator(greater).operator(newPlus)
                .operator(greaterEq)
                .build();
        assertEquals(0d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("1 >=> 2 >= 2 > 0").operator(greater).operator(newPlus)
                .operator(greaterEq)
                .build();
        assertEquals(1d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("1 >=> 2 >= 2 >= 1").operator(greater).operator(newPlus)
                .operator(greaterEq)
                .build();
        assertEquals(1d, e.evaluate(), 0.0);
    }

    @Test
    void testInvalidOperator1() {
        assertThrows(IllegalArgumentException.class, () -> {
            Operator fail = new AbstractOperator("2", 2, true, 1) {

                @Override
                public double apply(double[] values) {
                    return 0;
                }
            };

            new ExpressionBuilder("1").operator(fail)
                    .build();
        });
    }

    @Test
    void testInvalidFunction1() {
        assertThrows(IllegalArgumentException.class, () -> new AbstractFunction("1gd") {
            @Override
            public double apply(double... args) {
                return 0;
            }
        });
    }

    @Test
    void testInvalidFunction2() {
        assertThrows(IllegalArgumentException.class,
                () -> new AbstractFunction("+1gd") {

                    @Override
                    public double apply(double... args) {
                        return 0;
                    }
                });
    }

    @Test
    void testExpressionBuilder01() {
        Expression e = new ExpressionBuilder("7*x + 3*y")
                .variables("x", "y")
                .build()
                .setVariable("x", 1)
                .setVariable("y", 2);
        double result = e.evaluate();
        assertEquals(13d, result, 0.0);
    }

    @Test
    void testExpressionBuilder02() {
        Expression e = new ExpressionBuilder("7*x + 3*y")
                .variables("x", "y")
                .build()
                .setVariable("x", 1)
                .setVariable("y", 2);
        double result = e.evaluate();
        assertEquals(13d, result, 0.0);
    }

    @Test
    void testExpressionBuilder03() {
        double varX = 1.3d;
        double varY = 4.22d;
        Expression e = new ExpressionBuilder("7*x + 3*y - log(y/x*12)^y")
                .variables("x", "y")
                .build()
                .setVariable("x", varX)
                .setVariable("y", varY);
        double result = e.evaluate();
        assertEquals(result, 7 * varX + 3 * varY - pow(log(varY / varX * 12), varY), 0.0);
    }

    @Test
    void testExpressionBuilder04() {
        double varX = 1.3d;
        double varY = 4.22d;
        Expression e =
                new ExpressionBuilder("7*x + 3*y - log(y/x*12)^y")
                        .variables("x", "y")
                        .build()
                        .setVariable("x", varX)
                        .setVariable("y", varY);
        double result = e.evaluate();
        assertEquals(result, 7 * varX + 3 * varY - pow(log(varY / varX * 12), varY), 0.0);
        varX = 1.79854d;
        varY = 9281.123d;
        e.setVariable("x", varX);
        e.setVariable("y", varY);
        result = e.evaluate();
        assertEquals(result, 7 * varX + 3 * varY - pow(log(varY / varX * 12), varY), 0.0);
    }

    @Test
    void testExpressionBuilder05() {
        double varX = 1.3d;
        double varY = 4.22d;
        Expression e = new ExpressionBuilder("3*y")
                .variables("y")
                .build()
                .setVariable("x", varX)
                .setVariable("y", varY);
        double result = e.evaluate();
        assertEquals(result, 3 * varY, 0.0);
    }

    @Test
    void testExpressionBuilder06() {
        double varX = 1.3d;
        double varY = 4.22d;
        double varZ = 4.22d;
        Expression e = new ExpressionBuilder("x * y * z")
                .variables("x", "y", "z")
                .build();
        e.setVariable("x", varX);
        e.setVariable("y", varY);
        e.setVariable("z", varZ);
        double result = e.evaluate();
        assertEquals(result, varX * varY * varZ, 0.0);
    }

    @Test
    void testExpressionBuilder07() {
        double varX = 1.3d;
        Expression e = new ExpressionBuilder("log(sin(x))")
                .variables("x")
                .build()
                .setVariable("x", varX);
        double result = e.evaluate();
        assertEquals(result, log(sin(varX)), 0.0);
    }

    @Test
    void testExpressionBuilder08() {
        double varX = 1.3d;
        Expression e = new ExpressionBuilder("log(sin(x))")
                .variables("x")
                .build()
                .setVariable("x", varX);
        double result = e.evaluate();
        assertEquals(result, log(sin(varX)), 0.0);
    }

    @Test
    void testSameName() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    Function custom = new AbstractFunction("bar") {

                        @Override
                        public double apply(double... values) {
                            return values[0] / 2;
                        }
                    };
                    double varBar = 1.3d;
                    Expression e = new ExpressionBuilder("bar(bar)")
                            .variables("bar")
                            .function(custom)
                            .build()
                            .setVariable("bar", varBar);
                    ValidationResult res = e.validate();
                    assertFalse(res.isValid());
                    assertEquals(1, res.getErrors().size());
                });
    }

    @Test
    void testInvalidAbstractFunction() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    double varY = 4.22d;
                    Expression e = new ExpressionBuilder("3*invalid_function(y)")
                            .variables("<")
                            .build()
                            .setVariable("y", varY);
                    e.evaluate();
                });
    }

    @Test
    void testMissingVar() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    double varY = 4.22d;
                    Expression e = new ExpressionBuilder("3*y*z")
                            .variables("y", "z")
                            .build()
                            .setVariable("y", varY);
                    e.evaluate();
                });
    }

    @Test
    void testUnaryMinusPowerPrecedence() {
        Expression e = new ExpressionBuilder("-1^2")
                .build();
        assertEquals(-1d, e.evaluate(), 0d);
    }

    @Test
    void testUnaryMinus() {
        Expression e = new ExpressionBuilder("-1")
                .build();
        assertEquals(-1d, e.evaluate(), 0d);
    }

    @Test
    void testExpression1() {
        String expr;
        double expected;
        expr = "2 + 4";
        expected = 6d;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression10() {
        String expr;
        double expected;
        expr = "1 * 1.5 + 1";
        expected = 1 * 1.5 + 1;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression11() {
        double x = 1d;
        double y = 2d;
        String expr = "log(x) ^ sin(y)";
        double expected = Math.pow(Math.log(x), Math.sin(y));
        Expression e = new ExpressionBuilder(expr)
                .variables("x", "y")
                .build()
                .setVariable("x", x)
                .setVariable("y", y);
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression12() {
        String expr = "log(2.5333333333)^(0-1)";
        double expected = Math.pow(Math.log(2.5333333333d), -1);
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression13() {
        String expr = "2.5333333333^(0-1)";
        double expected = Math.pow(2.5333333333d, -1);
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression14() {
        String expr = "2 * 17.41 + (12*2)^(0-1)";
        double expected = 2 * 17.41d + Math.pow((12 * 2), -1);
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression15() {
        String expr = "2.5333333333 * 17.41 + (12*2)^log(2.764)";
        double expected = 2.5333333333d * 17.41d + Math.pow((12 * 2), Math.log(2.764d));
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression16() {
        String expr = "2.5333333333/2 * 17.41 + (12*2)^(log(2.764) - sin(5.6664))";
        double expected = 2.5333333333d / 2 * 17.41d + Math.pow((12 * 2), Math.log(2.764d) - Math.sin(5.6664d));
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression17() {
        String expr = "x^2 - 2 * y";
        double x = Math.E;
        double y = Math.PI;
        double expected = x * x - 2 * y;
        Expression e = new ExpressionBuilder(expr)
                .variables("x", "y")
                .build()
                .setVariable("x", x)
                .setVariable("y", y);
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression18() {
        String expr = "-3";
        double expected = -3;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression19() {
        String expr = "-3 * -24.23";
        double expected = -3 * -24.23d;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression2() {
        String expr;
        double expected;
        expr = "2+3*4-12";
        expected = 2 + 3 * 4 - 12;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression20() {
        String expr = "-2 * 24/log(2) -2";
        double expected = -2 * 24 / Math.log(2) - 2;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression21() {
        String expr = "-2 *33.34/log(x)^-2 + 14 *6";
        double x = 1.334d;
        double expected = -2 * 33.34 / Math.pow(Math.log(x), -2) + 14 * 6;
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build()
                .setVariable("x", x);
        assertEquals(expected, e.evaluate(), 0d);
    }

    @Test
    void testExpressionPower() {
        String expr = "2^-2";
        double expected = Math.pow(2, -2);
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0d);
    }

    @Test
    void testExpressionMultiplication() {
        String expr = "2*-2";
        double expected = -4d;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0d);
    }

    @Test
    void testExpression22() {
        String expr = "-2 *33.34/log(x)^-2 + 14 *6";
        double x = 1.334d;
        double expected = -2 * 33.34 / Math.pow(Math.log(x), -2) + 14 * 6;
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build()
                .setVariable("x", x);
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression23() {
        String expr = "-2 *33.34/(log(foo)^-2 + 14 *6) - sin(foo)";
        double x = 1.334d;
        double expected = -2 * 33.34 / (Math.pow(Math.log(x), -2) + 14 * 6) - Math.sin(x);
        Expression e = new ExpressionBuilder(expr)
                .variables("foo")
                .build()
                .setVariable("foo", x);
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression24() {
        String expr = "3+4-log(23.2)^(2-1) * -1";
        double expected = 3 + 4 - Math.pow(Math.log(23.2), (2 - 1)) * -1;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression25() {
        String expr = "+3+4-+log(23.2)^(2-1) * + 1";
        double expected = 3 + 4 - Math.log(23.2d);
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression26() {
        String expr = "14 + -(1 / 2.22^3)";
        double expected = 14 - (1d / Math.pow(2.22d, 3d));
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression27() {
        String expr = "12^-+-+-+-+-+-+---2";
        double expected = Math.pow(12, -2);
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression28() {
        String expr = "12^-+-+-+-+-+-+---2 * (-14) / 2 ^ -log(2.22323) ";
        double expected = Math.pow(12, -2) * -14 / Math.pow(2, -Math.log(2.22323));
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression29() {
        String expr = "24.3343 % 3";
        double expected = 24.3343 % 3;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testVarName1() {
        String expr = "12.23 * foo.bar";
        Expression e = new ExpressionBuilder(expr)
                .variables("foo.bar")
                .build()
                .setVariable("foo.bar", 1d);
        assertEquals(12.23, e.evaluate(), 0.0);
    }

    @Test
    void testMisplacedSeparator() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    String expr = "12.23 * ,foo";
                    Expression e = new ExpressionBuilder(expr)
                            .build()
                            .setVariable(",foo", 1d);
                    assertEquals(12.23, e.evaluate(), 0.0);
                });
    }

    @Test
    void testInvalidVarName() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    String expr = "12.23 * @foo";
                    Expression e = new ExpressionBuilder(expr)
                            .build()
                            .setVariable("@foo", 1d);
                    assertEquals(12.23, e.evaluate(), 0.0);
                });
    }

    @Test
    void testVarMap() {
        String expr = "12.23 * foo - bar";
        Map<String, Double> variables = new HashMap<>();
        variables.put("foo", 2d);
        variables.put("bar", 3.3d);
        Expression e = new ExpressionBuilder(expr)
                .variables(variables.keySet())
                .build()
                .setVariables(variables);
        assertEquals(12.23d * 2d - 3.3d, e.evaluate(), 0.0);
    }

    @Test
    void testInvalidNumberOfArguments1() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    String expr = "log(2,2)";
                    Expression e = new ExpressionBuilder(expr)
                            .build();
                    e.evaluate();
                });
    }

    @Test
    void testInvalidNumberOfArguments2() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    new AbstractFunction("avg", 4) {

                        @Override
                        public double apply(double... args) {
                            double sum = 0;
                            for (double arg : args) {
                                sum += arg;
                            }
                            return sum / args.length;
                        }
                    };
                    String expr = "avg(2,2)";
                    Expression e = new ExpressionBuilder(expr)
                            .build();
                    e.evaluate();
                });
    }

    @Test
    void testExpression3() {
        String expr;
        double expected;
        expr = "2+4*5";
        expected = 2 + 4 * 5;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression30() {
        String expr = "24.3343 % 3 * 20 ^ -(2.334 % log(2 / 14))";
        double expected = 24.3343d % 3 * Math.pow(20, -(2.334 % Math.log(2d / 14d)));
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression31() {
        String expr = "-2 *33.34/log(y_x)^-2 + 14 *6";
        double x = 1.334d;
        double expected = -2 * 33.34 / Math.pow(Math.log(x), -2) + 14 * 6;
        Expression e = new ExpressionBuilder(expr)
                .variables("y_x")
                .build()
                .setVariable("y_x", x);
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression32() {
        String expr = "-2 *33.34/log(y_2x)^-2 + 14 *6";
        double x = 1.334d;
        double expected = -2 * 33.34 / Math.pow(Math.log(x), -2) + 14 * 6;
        Expression e = new ExpressionBuilder(expr)
                .variables("y_2x")
                .build()
                .setVariable("y_2x", x);
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression33() {
        String expr = "-2 *33.34/log(_y)^-2 + 14 *6";
        double x = 1.334d;
        double expected = -2 * 33.34 / Math.pow(Math.log(x), -2) + 14 * 6;
        Expression e = new ExpressionBuilder(expr)
                .variables("_y")
                .build()
                .setVariable("_y", x);
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression34() {
        String expr = "-2 + + (+4) +(4)";
        double expected = -2 + 4 + 4;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression40() {
        String expr = "1e1";
        double expected = 10d;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression41() {
        String expr = "1e-1";
        double expected = 0.1d;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    /*
     * Added tests for expressions with scientific notation see http://jira.congrace.de/jira/browse/EXP-17
     */
    @Test
    void testExpression42() {
        String expr = "7.2973525698e-3";
        double expected = 7.2973525698e-3d;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression43() {
        String expr = "6.02214E23";
        double expected = 6.02214e23d;
        Expression e = new ExpressionBuilder(expr)
                .build();
        double result = e.evaluate();
        assertEquals(expected, result, 0.0);
    }

    @Test
    void testExpression44() {
        String expr = "6.02214E23";
        double expected = 6.02214e23d;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression45() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    String expr = "6.02214E2E3";
                    new ExpressionBuilder(expr)
                            .build();
                });
    }

    @Test
    void testExpression46() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    String expr = "6.02214e2E3";
                    new ExpressionBuilder(expr)
                            .build();
                });
    }

    // tests for EXP-20: No exception is thrown for unmatched parenthesis in
// build
// Thanks go out to maheshkurmi for reporting
    @Test
    void testExpression48() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    String expr = "(1*2";
                    Expression e = new ExpressionBuilder(expr)
                            .build();

                    e.evaluate();
                });
    }

    @Test
    void testExpression49() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    String expr = "{1*2";
                    Expression e = new ExpressionBuilder(expr)
                            .build();

                    e.evaluate();
                });
    }

    @Test
    void testExpression50() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    String expr = "[1*2";
                    Expression e = new ExpressionBuilder(expr)
                            .build();

                    e.evaluate();
                });
    }

    @Test
    void testExpression51() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    String expr = "(1*{2+[3}";
                    Expression e = new ExpressionBuilder(expr)
                            .build();

                    e.evaluate();
                });
    }

    @Test
    void testExpression52() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    String expr = "(1*(2+(3";
                    Expression e = new ExpressionBuilder(expr)
                            .build();

                    e.evaluate();
                });
    }

    @Test
    void testExpression53() {
        String expr = "14 * 2x";
        Expression exp = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        exp.setVariable("x", 1.5d);
        assertTrue(exp.validate().isValid());
        assertEquals(14d * 2d * 1.5d, exp.evaluate(), 0d);
    }

    @Test
    void testExpression54() {
        String expr = "2 ((-(x)))";
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        e.setVariable("x", 1.5d);
        assertEquals(-3d, e.evaluate(), 0d);
    }

    @Test
    void testExpression55() {
        String expr = "2 sin(x)";
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        e.setVariable("x", 2d);
        assertEquals(sin(2d) * 2, e.evaluate(), 0.0);
    }

    @Test
    void testExpression56() {
        String expr = "2 sin(3x)";
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        e.setVariable("x", 2d);
        assertEquals(sin(6d) * 2d, e.evaluate(), 0.0);
    }

    @Test
    void testDocumentationExample1() {
        Expression e = new ExpressionBuilder("3 * sin(y) - 2 / (x - 2)")
                .variables("x", "y")
                .build()
                .setVariable("x", 2.3)
                .setVariable("y", 3.14);
        double result = e.evaluate();
        double expected = 3 * Math.sin(3.14d) - 2d / (2.3d - 2d);
        assertEquals(expected, result, 0d);
    }

    @Test
    void testDocumentationExample2() throws Exception {
        ExecutorService exec = Executors.newFixedThreadPool(1);
        Expression e = new ExpressionBuilder("3log(y)/(x+1)")
                .variables("x", "y")
                .build()
                .setVariable("x", 2.3)
                .setVariable("y", 3.14);
        Future<Double> result = e.evaluateAsync(exec);
        double expected = 3 * Math.log(3.14d) / (3.3);
        assertEquals(expected, result.get(), 0d);
    }

    @Test
    void testDocumentationExample3() {
        double result = new ExpressionBuilder("2cos(xy)")
                .variables("x", "y")
                .build()
                .setVariable("x", 0.5d)
                .setVariable("y", 0.25d)
                .evaluate();
        assertEquals(2d * Math.cos(0.5d * 0.25d), result, 0d);
    }

    @Test
    void testDocumentationExample4() {
        String expr = "pi+π+e+φ";
        double expected = 2 * Math.PI + Math.E + 1.61803398874d;
        Expression e = new ExpressionBuilder(expr).build();
        assertEquals(expected, e.evaluate(), 0d);
    }

    @Test
    void testDocumentationExample5() {
        String expr = "7.2973525698e-3";
        double expected = Double.parseDouble(expr);
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0d);
    }


    @Test
    void testDocumentationExample6() {
        Function logb = new AbstractFunction("logb", 2) {
            @Override
            public double apply(double... args) {
                return Math.log(args[0]) / Math.log(args[1]);
            }
        };
        double result = new ExpressionBuilder("logb(8, 2)")
                .function(logb)
                .build()
                .evaluate();
        double expected = 3;
        assertEquals(expected, result, 0d);
    }

    @Test
    void testDocumentationExample7() {
        Function avg = new AbstractFunction("avg", 4) {

            @Override
            public double apply(double... args) {
                double sum = 0;
                for (double arg : args) {
                    sum += arg;
                }
                return sum / args.length;
            }
        };
        double result = new ExpressionBuilder("avg(1,2,3,4)")
                .function(avg)
                .build()
                .evaluate();

        double expected = 2.5d;
        assertEquals(expected, result, 0d);
    }

    @Test
    void testDocumentationExample8() {
        Operator factorial = new AbstractOperator("!", 1, true, Operators.PRECEDENCE_POWER + 1) {

            @Override
            public double apply(double... args) {
                int arg = (int) args[0];
                if ((double) arg != args[0]) {
                    throw new IllegalArgumentException("Operand for factorial has to be an integer");
                }
                if (arg < 0) {
                    throw new IllegalArgumentException("The operand of the factorial can not be less than zero");
                }
                double result = 1;
                for (int i = 1; i <= arg; i++) {
                    result *= i;
                }
                return result;
            }
        };

        double result = new ExpressionBuilder("3!")
                .operator(factorial)
                .build()
                .evaluate();

        double expected = 6d;
        assertEquals(expected, result, 0d);
    }

    @Test
    void testDocumentationExample9() {
        Operator gteq = new AbstractOperator(">=", 2, true, Operators.PRECEDENCE_ADDITION - 1) {

            @Override
            public double apply(double[] values) {
                if (values[0] >= values[1]) {
                    return 1d;
                } else {
                    return 0d;
                }
            }
        };

        Expression e = new ExpressionBuilder("1>=2").operator(gteq)
                .build();
        assertEquals(0d, e.evaluate(), 0.0);
        e = new ExpressionBuilder("2>=1").operator(gteq)
                .build();
        assertEquals(1d, e.evaluate(), 0.0);
    }

    @Test
    void testDocumentationExample10() {
        assertThrows(ArithmeticException.class,
                () -> {
                    Operator reciprocal = new AbstractOperator("$", 1, true, Operators.PRECEDENCE_DIVISION) {
                        @Override
                        public double apply(double... args) {
                            if (args[0] == 0d) {
                                throw new ArithmeticException("Division by zero!");
                            }
                            return 1d / args[0];
                        }
                    };
                    Expression e = new ExpressionBuilder("0$").operator(reciprocal).build();
                    e.evaluate();
                });
    }

    @Test
    void testDocumentationExample11() {
        Expression e = new ExpressionBuilder("x")
                .variable("x")
                .build();

        ValidationResult res = e.validate();
        assertFalse(res.isValid());
        assertEquals(1, res.getErrors().size());

        e.setVariable("x", 1d);
        res = e.validate();
        assertTrue(res.isValid());
    }

    @Test
    void testDocumentationExample12() {
        Expression e = new ExpressionBuilder("x")
                .variable("x")
                .build();

        ValidationResult res = e.validate(false);
        assertTrue(res.isValid());
        assertTrue(res.getErrors().isEmpty());
    }

    // Thanks go out to Johan Björk for reporting the division by zero problem EXP-22
// https://www.objecthunter.net/jira/browse/EXP-22
    @Test
    void testExpression57() {
        String expr = "1 / 0";
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(Double.POSITIVE_INFINITY, e.evaluate(), 0.0);
    }

    @Test
    void testExpression58() {
        String expr = "17 * sqrt(-1) * 12";
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertTrue(Double.isNaN(e.evaluate()));
    }

    // Thanks go out to Alex Dolinsky for reporting the missing exception when an empty
// expression is passed as in new ExpressionBuilder("")
    @Test
    void testExpression59() {
        assertThrows(IllegalArgumentException.class,
                () -> new ExpressionBuilder("").build());
    }

    @Test
    void testExpression60() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    Expression e = new ExpressionBuilder("   ")
                            .build();
                    e.evaluate();
                });
    }

    @Test
    void testExpression61() {
        Expression e = new ExpressionBuilder("14 % 0")
                .build();

        assertEquals(Double.NaN, e.evaluate(), 0.0);
    }

    // https://www.objecthunter.net/jira/browse/EXP-24
// thanks go out to Rémi for the issue report
    @Test
    void testExpression62() {
        Expression e = new ExpressionBuilder("x*1.0e5+5")
                .variables("x")
                .build()
                .setVariable("x", Math.E);
        assertEquals(E * 1.0 * pow(10, 5) + 5, e.evaluate(), 0.0);
    }

    @Test
    void testExpression63() {
        Expression e = new ExpressionBuilder("log10(5)")
                .build();
        assertEquals(Math.log10(5), e.evaluate(), 0d);
    }

    @Test
    void testExpression64() {
        Expression e = new ExpressionBuilder("log2(5)")
                .build();
        assertEquals(Math.log(5) / Math.log(2), e.evaluate(), 0d);
    }

    @Test
    void testExpression65() {
        Expression e = new ExpressionBuilder("2log(e)")
                .variables("e")
                .build()
                .setVariable("e", Math.E);

        assertEquals(2d, e.evaluate(), 0d);
    }

    @Test
    void testExpression66() {
        Expression e = new ExpressionBuilder("log(e)2")
                .variables("e")
                .build()
                .setVariable("e", Math.E);

        assertEquals(2d, e.evaluate(), 0d);
    }

    @Test
    void testExpression67() {
        Expression e = new ExpressionBuilder("2esin(pi/2)")
                .variables("e", "pi")
                .build()
                .setVariable("e", Math.E)
                .setVariable("pi", Math.PI);

        assertEquals(2 * Math.E * Math.sin(Math.PI / 2d), e.evaluate(), 0d);
    }

    @Test
    void testExpression68() {
        Expression e = new ExpressionBuilder("2x")
                .variables("x")
                .build()
                .setVariable("x", Math.E);
        assertEquals(2 * Math.E, e.evaluate(), 0d);
    }

    @Test
    void testExpression69() {
        Expression e = new ExpressionBuilder("2x2")
                .variables("x")
                .build()
                .setVariable("x", Math.E);
        assertEquals(4 * Math.E, e.evaluate(), 0d);
    }

    @Test
    void testExpression70() {
        Expression e = new ExpressionBuilder("2xx")
                .variables("x")
                .build()
                .setVariable("x", Math.E);
        assertEquals(2 * Math.E * Math.E, e.evaluate(), 0d);
    }

    @Test
    void testExpression71() {
        Expression e = new ExpressionBuilder("x2x")
                .variables("x")
                .build()
                .setVariable("x", Math.E);
        assertEquals(2 * Math.E * Math.E, e.evaluate(), 0d);
    }

    @Test
    void testExpression72() {
        Expression e = new ExpressionBuilder("2cos(x)")
                .variables("x")
                .build()
                .setVariable("x", Math.E);
        assertEquals(2 * Math.cos(Math.E), e.evaluate(), 0d);
    }

    @Test
    void testExpression73() {
        Expression e = new ExpressionBuilder("cos(x)2")
                .variables("x")
                .build()
                .setVariable("x", Math.E);
        assertEquals(2 * Math.cos(Math.E), e.evaluate(), 0d);
    }

    @Test
    void testExpression74() {
        Expression e = new ExpressionBuilder("cos(x)(-2)")
                .variables("x")
                .build()
                .setVariable("x", Math.E);
        assertEquals(-2d * Math.cos(Math.E), e.evaluate(), 0d);
    }

    @Test
    void testExpression75() {
        Expression e = new ExpressionBuilder("(-2)cos(x)")
                .variables("x")
                .build()
                .setVariable("x", Math.E);
        assertEquals(-2d * Math.cos(Math.E), e.evaluate(), 0d);
    }

    @Test
    void testExpression76() {
        Expression e = new ExpressionBuilder("(-x)cos(x)")
                .variables("x")
                .build()
                .setVariable("x", Math.E);
        assertEquals(-E * Math.cos(Math.E), e.evaluate(), 0d);
    }

    @Test
    void testExpression77() {
        Expression e = new ExpressionBuilder("(-xx)cos(x)")
                .variables("x")
                .build()
                .setVariable("x", Math.E);
        assertEquals(-E * E * Math.cos(Math.E), e.evaluate(), 0d);
    }

    @Test
    void testExpression78() {
        Expression e = new ExpressionBuilder("(xx)cos(x)")
                .variables("x")
                .build()
                .setVariable("x", Math.E);
        assertEquals(E * E * Math.cos(Math.E), e.evaluate(), 0d);
    }

    @Test
    void testExpression79() {
        Expression e = new ExpressionBuilder("cos(x)(xx)")
                .variables("x")
                .build()
                .setVariable("x", Math.E);
        assertEquals(E * E * Math.cos(Math.E), e.evaluate(), 0d);
    }

    @Test
    void testExpression80() {
        Expression e = new ExpressionBuilder("cos(x)(xy)")
                .variables("x", "y")
                .build()
                .setVariable("x", Math.E)
                .setVariable("y", Math.sqrt(2));
        assertEquals(sqrt(2) * E * Math.cos(Math.E), e.evaluate(), 0d);
    }

    @Test
    void testExpression81() {
        Expression e = new ExpressionBuilder("cos(xy)")
                .variables("x", "y")
                .build()
                .setVariable("x", Math.E)
                .setVariable("y", Math.sqrt(2));
        assertEquals(cos(sqrt(2) * E), e.evaluate(), 0d);
    }

    @Test
    void testExpression82() {
        Expression e = new ExpressionBuilder("cos(2x)")
                .variables("x")
                .build()
                .setVariable("x", Math.E);
        assertEquals(cos(2 * E), e.evaluate(), 0d);
    }

    @Test
    void testExpression83() {
        Expression e = new ExpressionBuilder("cos(xlog(xy))")
                .variables("x", "y")
                .build()
                .setVariable("x", Math.E)
                .setVariable("y", Math.sqrt(2));
        assertEquals(cos(E * log(E * sqrt(2))), e.evaluate(), 0d);
    }

    @Test
    void testExpression84() {
        Expression e = new ExpressionBuilder("3x_1")
                .variables("x_1")
                .build()
                .setVariable("x_1", Math.E);
        assertEquals(3d * E, e.evaluate(), 0d);
    }

    @Test
    void testExpression85() {
        Expression e = new ExpressionBuilder("1/2x")
                .variables("x")
                .build()
                .setVariable("x", 6);
        assertEquals(3d, e.evaluate(), 0d);
    }

    // thanks got out to David Sills
    @Test
    void testSpaceBetweenNumbers() {
        assertThrows(IllegalArgumentException.class,
                () -> new ExpressionBuilder("1 1").build());
    }

    // thanks go out to Janny for providing the tests and the bug report
    @Test
    void testUnaryMinusInParenthesisSpace() {
        ExpressionBuilder b = new ExpressionBuilder("( -1)^2");
        double calculated = b.build().evaluate();
        assertEquals(1d, calculated, 0.0);
    }

    @Test
    void testUnaryMinusSpace() {
        ExpressionBuilder b = new ExpressionBuilder(" -1 + 2");
        double calculated = b.build().evaluate();
        assertEquals(1d, calculated, 0.0);
    }

    @Test
    void testUnaryMinusSpaces() {
        ExpressionBuilder b = new ExpressionBuilder(" -1 + + 2 +   -   1");
        double calculated = b.build().evaluate();
        assertEquals(0d, calculated, 0.0);
    }

    @Test
    void testUnaryMinusSpace1() {
        ExpressionBuilder b = new ExpressionBuilder("-1");
        double calculated = b.build().evaluate();
        assertEquals(calculated, -1d, 0.0);
    }

    @Test
    void testExpression4() {
        String expr;
        double expected;
        expr = "2+4 * 5";
        expected = 2 + 4 * 5;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression5() {
        String expr;
        double expected;
        expr = "(2+4)*5";
        expected = (2 + 4) * 5;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression6() {
        String expr;
        double expected;
        expr = "(2+4)*5 + 2.5*2";
        expected = (2 + 4) * 5 + 2.5 * 2;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression7() {
        String expr;
        double expected;
        expr = "(2+4)*5 + 10/2";
        expected = (2 + 4) * 5 + 10D / 2;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression8() {
        String expr;
        double expected;
        expr = "(2 * 3 +4)*5 + 10/2";
        expected = (2 * 3 + 4) * 5 + 10D / 2;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testExpression9() {
        String expr;
        double expected;
        expr = "(2 * 3 +4)*5 +4 + 10/2";
        expected = 59; //(2 * 3 + 4) * 5 + 4 + 10 / 2 = 59
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testFailUnknownFunction1() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    String expr;
                    expr = "lig(1)";
                    Expression e = new ExpressionBuilder(expr)
                            .build();
                    e.evaluate();
                });
    }

    @Test
    void testFailUnknownFunction2() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    String expr;
                    expr = "galength(1)";
                    new ExpressionBuilder(expr)
                            .build().evaluate();
                });
    }

    @Test
    void testFailUnknownFunction3() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    String expr;
                    expr = "tcos(1)";
                    Expression exp = new ExpressionBuilder(expr)
                            .build();
                    double result = exp.evaluate();
                    System.out.println(result);
                });
    }

    @Test
    void testFunction22() {
        String expr;
        expr = "cos(cos_1)";
        Expression e = new ExpressionBuilder(expr)
                .variables("cos_1")
                .build()
                .setVariable("cos_1", 1d);
        assertEquals(e.evaluate(), cos(1d), 0.0);
    }

    @Test
    void testFunction23() {
        String expr;
        expr = "log1p(1)";
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(log1p(1d), e.evaluate(), 0d);
    }

    @Test
    void testFunction24() {
        String expr;
        expr = "pow(3,3)";
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(27d, e.evaluate(), 0d);
    }

    @Test
    void testPostfix1() {
        String expr;
        double expected;
        expr = "2.2232^0.1";
        expected = Math.pow(2.2232d, 0.1d);
        double actual = new ExpressionBuilder(expr)
                .build().evaluate();
        assertEquals(expected, actual, 0.0);
    }

    @Test
    void testPostfixEverything() {
        String expr;
        double expected;
        expr = "(sin(12) + log(34)) * 3.42 - cos(2.234-log(2))";
        expected = (Math.sin(12) + Math.log(34)) * 3.42 - Math.cos(2.234 - Math.log(2));
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testPostfixExponentiation1() {
        String expr;
        double expected;
        expr = "2^3";
        expected = Math.pow(2, 3);
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testPostfixExponentiation2() {
        String expr;
        double expected;
        expr = "24 + 4 * 2^3";
        expected = 24 + 4 * Math.pow(2, 3);
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testPostfixExponentiation3() {
        String expr;
        double expected;
        double x = 4.334d;
        expr = "24 + 4 * 2^x";
        expected = 24 + 4 * Math.pow(2, x);
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build()
                .setVariable("x", x);
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testPostfixExponentiation4() {
        String expr;
        double expected;
        double x = 4.334d;
        expr = "(24 + 4) * 2^log(x)";
        expected = (24 + 4) * Math.pow(2, Math.log(x));
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build()
                .setVariable("x", x);
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testPostfixFunction1() {
        String expr;
        double expected;
        expr = "log(1) * sin(0)";
        expected = Math.log(1) * Math.sin(0);
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testPostfixFunction10() {
        String expr;
        double expected;
        expr = "cbrt(x)";
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        for (double x = -10; x < 10; x = x + 0.5d) {
            expected = Math.cbrt(x);
            assertEquals(expected, e.setVariable("x", x).evaluate(), 0.0);
        }
    }

    @Test
    void testPostfixFunction11() {
        String expr;
        double expected;
        expr = "cos(x) - (1/cbrt(x))";
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        for (double x = -10; x < 10; x = x + 0.5d) {
            if (x == 0d) {
                continue;
            }
            expected = Math.cos(x) - (1 / Math.cbrt(x));
            assertEquals(expected, e.setVariable("x", x).evaluate(), 0.0);
        }
    }

    @Test
    void testPostfixFunction12() {
        String expr;
        double expected;
        expr = "acos(x) * expm1(asin(x)) - exp(atan(x)) + floor(x) + cosh(x) - sinh(cbrt(x))";
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        for (double x = -10; x < 10; x = x + 0.5d) {
            expected =
                    Math.acos(x) * Math.expm1(Math.asin(x)) - Math.exp(Math.atan(x)) + Math.floor(x) + Math.cosh(x)
                    - Math.sinh(Math.cbrt(x));
            if (Double.isNaN(expected)) {
                assertTrue(Double.isNaN(e.setVariable("x", x).evaluate()));
            } else {
                assertEquals(expected, e.setVariable("x", x).evaluate(), 0.0);
            }
        }
    }

    @Test
    void testPostfixFunction13() {
        String expr;
        double expected;
        expr = "acos(x)";
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        for (double x = -10; x < 10; x = x + 0.5d) {
            expected = Math.acos(x);
            if (Double.isNaN(expected)) {
                assertTrue(Double.isNaN(e.setVariable("x", x).evaluate()));
            } else {
                assertEquals(expected, e.setVariable("x", x).evaluate(), 0.0);
            }
        }
    }

    @Test
    void testPostfixFunction14() {
        String expr;
        double expected;
        expr = " expm1(x)";
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        for (double x = -10; x < 10; x = x + 0.5d) {
            expected = Math.expm1(x);
            if (Double.isNaN(expected)) {
                assertTrue(Double.isNaN(e.setVariable("x", x).evaluate()));
            } else {
                assertEquals(expected, e.setVariable("x", x).evaluate(), 0.0);
            }
        }
    }

    @Test
    void testPostfixFunction15() {
        String expr;
        double expected;
        expr = "asin(x)";
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        for (double x = -10; x < 10; x = x + 0.5d) {
            expected = Math.asin(x);
            if (Double.isNaN(expected)) {
                assertTrue(Double.isNaN(e.setVariable("x", x).evaluate()));
            } else {
                assertEquals(expected, e.setVariable("x", x).evaluate(), 0.0);
            }
        }
    }

    @Test
    void testPostfixFunction16() {
        String expr;
        double expected;
        expr = " exp(x)";
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        for (double x = -10; x < 10; x = x + 0.5d) {
            expected = Math.exp(x);
            assertEquals(expected, e.setVariable("x", x).evaluate(), 0.0);
        }
    }

    @Test
    void testPostfixFunction17() {
        String expr;
        double expected;
        expr = "floor(x)";
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        for (double x = -10; x < 10; x = x + 0.5d) {
            expected = Math.floor(x);
            assertEquals(expected, e.setVariable("x", x).evaluate(), 0.0);
        }
    }

    @Test
    void testPostfixFunction18() {
        String expr;
        double expected;
        expr = " cosh(x)";
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        for (double x = -10; x < 10; x = x + 0.5d) {
            expected = Math.cosh(x);
            assertEquals(expected, e.setVariable("x", x).evaluate(), 0.0);
        }
    }

    @Test
    void testPostfixFunction19() {
        String expr;
        double expected;
        expr = "sinh(x)";
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        for (double x = -10; x < 10; x = x + 0.5d) {
            expected = Math.sinh(x);
            assertEquals(expected, e.setVariable("x", x).evaluate(), 0.0);
        }
    }

    @Test
    void testPostfixFunction20() {
        String expr;
        double expected;
        expr = "cbrt(x)";
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        for (double x = -10; x < 10; x = x + 0.5d) {
            expected = Math.cbrt(x);
            assertEquals(expected, e.setVariable("x", x).evaluate(), 0.0);
        }
    }

    @Test
    void testPostfixFunction21() {
        String expr;
        double expected;
        expr = "tanh(x)";
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        for (double x = -10; x < 10; x = x + 0.5d) {
            expected = Math.tanh(x);
            assertEquals(expected, e.setVariable("x", x).evaluate(), 0.0);
        }
    }

    @Test
    void testPostfixFunction2() {
        String expr;
        double expected;
        expr = "log(1)";
        expected = 0d;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testPostfixFunction3() {
        String expr;
        double expected;
        expr = "sin(0)";
        expected = 0d;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testPostfixFunction5() {
        String expr;
        double expected;
        expr = "ceil(2.3) +1";
        expected = Math.ceil(2.3) + 1;
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testPostfixFunction6() {
        String expr;
        double expected;
        double x = 1.565d;
        double y = 2.1323d;
        expr = "ceil(x) + 1 / y * abs(1.4)";
        expected = Math.ceil(x) + 1 / y * Math.abs(1.4);
        Expression e = new ExpressionBuilder(expr)
                .variables("x", "y")
                .build();
        assertEquals(expected, e.setVariable("x", x)
                .setVariable("y", y).evaluate(), 0.0);
    }

    @Test
    void testPostfixFunction7() {
        String expr;
        double expected;
        double x = Math.E;
        expr = "tan(x)";
        expected = Math.tan(x);
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        assertEquals(expected, e.setVariable("x", x).evaluate(), 0.0);
    }

    @Test
    void testPostfixFunction8() {
        String expr;
        double expected;
        expr = "2^3.4223232 + tan(e)";
        expected = Math.pow(2, 3.4223232d) + Math.tan(Math.E);
        Expression e = new ExpressionBuilder(expr)
                .variables("e")
                .build();
        assertEquals(expected, e.setVariable("e", E).evaluate(), 0.0);
    }

    @Test
    void testPostfixFunction9() {
        String expr;
        double expected;
        double x = Math.E;
        expr = "cbrt(x)";
        expected = Math.cbrt(x);
        Expression e = new ExpressionBuilder(expr)
                .variables("x")
                .build();
        assertEquals(expected, e.setVariable("x", x).evaluate(), 0.0);
    }

    @Test
    void testPostfixInvalidVariableName() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    String expr;
                    double expected;
                    double x = 4.5334332d;
                    double log = Math.PI;
                    expr = "x * pi";
                    expected = x * log;
                    Expression e = new ExpressionBuilder(expr)
                            .variables("x", "pi")
                            .build();
                    assertEquals(expected, e.setVariable("x", x)
                            .setVariable("log", log).evaluate(), 0.0);
                });
    }

    @Test
    void testPostfixParenthesis() {
        String expr;
        double expected;
        expr = "(3 + 3 * 14) * (2 * (24-17) - 14)/((34) -2)";
        expected = 0; //(3 + 3 * 14) * (2 * (24-17) - 14)/((34) -2) = 0
        Expression e = new ExpressionBuilder(expr)
                .build();
        assertEquals(expected, e.evaluate(), 0.0);
    }

    @Test
    void testPostfixVariables() {
        String expr;
        double expected;
        double x = 4.5334332d;
        double pi = Math.PI;
        expr = "x * pi";
        expected = x * pi;
        Expression e = new ExpressionBuilder(expr)
                .variables("x", "pi")
                .build();
        assertEquals(expected, e.setVariable("x", x)
                .setVariable("pi", pi).evaluate(), 0.0);
    }

    @Test
    void testUnicodeVariable1() {
        Expression e = new ExpressionBuilder("λ")
                .variable("λ")
                .build()
                .setVariable("λ", E);
        assertEquals(E, e.evaluate(), 0d);
    }

    @Test
    void testUnicodeVariable2() {
        Expression e = new ExpressionBuilder("log(3ε+1)")
                .variable("ε")
                .build()
                .setVariable("ε", E);
        assertEquals(log(3 * E + 1), e.evaluate(), 0d);
    }

    @Test
    void testUnicodeVariable3() {
        Function log = new AbstractFunction("λωγ", 1) {

            @Override
            public double apply(double... args) {
                return log(args[0]);
            }
        };

        Expression e = new ExpressionBuilder("λωγ(π)")
                .variable("π")
                .function(log)
                .build()
                .setVariable("π", PI);
        assertEquals(log(PI), e.evaluate(), 0d);
    }

    @Test
    void testUnicodeVariable4() {
        Function log = new AbstractFunction("λ_ωγ", 1) {

            @Override
            public double apply(double... args) {
                return log(args[0]);
            }
        };

        Expression e = new ExpressionBuilder("3λ_ωγ(πε6)")
                .variables("π", "ε")
                .function(log)
                .build()
                .setVariable("π", PI)
                .setVariable("ε", E);
        assertEquals(3 * log(PI * E * 6), e.evaluate(), 0d);
    }

    @Test
    void testImplicitMultiplicationOffNumber() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    Expression e = new ExpressionBuilder("var_12")
                            .variable("var_1")
                            .implicitMultiplication(false)
                            .build();
                    e.evaluate();
                });
    }

    @Test
    void testImplicitMultiplicationOffVariable() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    Expression e = new ExpressionBuilder("var_1var_1")
                            .variable("var_1")
                            .implicitMultiplication(false)
                            .build();
                    e.evaluate();
                });
    }

    @Test
    void testImplicitMultiplicationOffParentheses() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    Expression e = new ExpressionBuilder("var_1(2)")
                            .variable("var_1")
                            .implicitMultiplication(false)
                            .build();
                    e.evaluate();
                });
    }

    @Test
    void testImplicitMultiplicationOffAbstractFunction() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    Expression e = new ExpressionBuilder("var_1log(2)")
                            .variable("var_1")
                            .implicitMultiplication(false)
                            .build()
                            .setVariable("var_1", 2);
                    e.evaluate();
                });
    }

    @Test
    void testImplicitMultiplicationOnNumber() {
        Expression e = new ExpressionBuilder("var_12")
                .variable("var_1")
                .build()
                .setVariable("var_1", 2);
        assertEquals(4d, e.evaluate(), 0d);
    }

    @Test
    void testImplicitMultiplicationOnVariable() {
        Expression e = new ExpressionBuilder("var_1var_1")
                .variable("var_1")
                .build()
                .setVariable("var_1", 2);
        assertEquals(4d, e.evaluate(), 0d);
    }

    @Test
    void testImplicitMultiplicationOnParentheses() {
        Expression e = new ExpressionBuilder("var_1(2)")
                .variable("var_1")
                .build()
                .setVariable("var_1", 2);
        assertEquals(4d, e.evaluate(), 0d);
    }

    @Test
    void testImplicitMultiplicationOnAbstractFunction() {
        Expression e = new ExpressionBuilder("var_1log(2)")
                .variable("var_1")
                .build()
                .setVariable("var_1", 2);
        assertEquals(2 * log(2), e.evaluate(), 0d);
    }

    // thanks go out to vandanagopal for reporting the issue
// https://github.com/fasseg/exp4j/issues/23
    @Test
    void testSecondArgumentNegative() {
        Function round = new AbstractFunction("MULTIPLY", 2) {
            @Override
            public double apply(double... args) {
                return Math.round(args[0] * args[1]);
            }
        };
        double result = new ExpressionBuilder("MULTIPLY(2,-1)")
                .function(round)
                .build()
                .evaluate();
        assertEquals(-2d, result, 0d);
    }

    // Test for https://github.com/fasseg/exp4j/issues/65
    @Test
    void testVariableWithDot() {
        double result = new ExpressionBuilder("2*SALARY.Basic")
                .variable("SALARY.Basic")
                .build()
                .setVariable("SALARY.Basic", 1.5d)
                .evaluate();
        assertEquals(3d, result, 0d);
    }

    @Test
    void testTwoAdjacentOperators() {
        Operator factorial = new AbstractOperator("!", 1, true, Operators.PRECEDENCE_POWER + 1) {

            @Override
            public double apply(double... args) {
                int arg = (int) args[0];
                if ((double) arg != args[0]) {
                    throw new IllegalArgumentException("Operand for factorial has to be an integer");
                }
                if (arg < 0) {
                    throw new IllegalArgumentException("The operand of the factorial can not be less than zero");
                }
                double result = 1;
                for (int i = 1; i <= arg; i++) {
                    result *= i;
                }
                return result;
            }
        };

        double result = new ExpressionBuilder("3!+2")
                .operator(factorial)
                .build()
                .evaluate();

        double expected = 8d;
        assertEquals(expected, result, 0d);
    }

    @Test
    void testGetVariableNames1() {
        Expression e = new ExpressionBuilder("b*a-9.24c")
                .variables("b", "a", "c")
                .build();
        Set<String> variableNames = e.getVariableNames();
        assertTrue(variableNames.contains("a"));
        assertTrue(variableNames.contains("b"));
        assertTrue(variableNames.contains("c"));
    }

    @Test
    void testGetVariableNames2() {
        Expression e = new ExpressionBuilder("log(bar)-FOO.s/9.24c")
                .variables("bar", "FOO.s", "c")
                .build();
        Set<String> variableNames = e.getVariableNames();
        assertTrue(variableNames.contains("bar"));
        assertTrue(variableNames.contains("FOO.s"));
        assertTrue(variableNames.contains("c"));
    }

    @Test
    void testSameVariableAndBuiltinFunctionName() {
        assertThrows(IllegalArgumentException.class,
                () -> new ExpressionBuilder("log10(log10)")
                        .variables("log10")
                        .build());
    }

    @Test
    void testSameVariableAndUserFunctionName() {
        assertThrows(IllegalArgumentException.class,
                () -> new ExpressionBuilder("2*tr+tr(2)")
                        .variables("tr")
                        .function(new AbstractFunction("tr") {
                            @Override
                            public double apply(double... args) {
                                return 0;
                            }
                        })
                        .build());
    }

    @Test
    void testSignum() {
        Expression e = new ExpressionBuilder("signum(1)")
                .build();
        assertEquals(1, e.evaluate(), 0d);

        e = new ExpressionBuilder("signum(-1)")
                .build();
        assertEquals(-1, e.evaluate(), 0d);

        e = new ExpressionBuilder("signum(--1)")
                .build();
        assertEquals(1, e.evaluate(), 0d);

        e = new ExpressionBuilder("signum(+-1)")
                .build();
        assertEquals(-1, e.evaluate(), 0d);

        e = new ExpressionBuilder("-+1")
                .build();
        assertEquals(-1, e.evaluate(), 0d);

        e = new ExpressionBuilder("signum(-+1)")
                .build();
        assertEquals(-1, e.evaluate(), 0d);
    }

    @Test
    void testCustomPercent() {
        Function percentage = new AbstractFunction("percentage", 2) {
            @Override
            public double apply(double... args) {
                double val = args[0];
                double percent = args[1];
                if (percent < 0) {
                    return val - val * Math.abs(percent) / 100d;
                } else {
                    return val - val * percent / 100d;
                }
            }
        };

        Expression e = new ExpressionBuilder("percentage(1000,-10)")
                .function(percentage)
                .build();
        assertEquals(0d, 900, e.evaluate());

        e = new ExpressionBuilder("percentage(1000,12)")
                .function(percentage)
                .build();
        assertEquals(0d, 1000d * 0.12d, e.evaluate());
    }
}
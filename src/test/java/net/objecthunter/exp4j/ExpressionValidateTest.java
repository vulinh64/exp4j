/*
 * Copyright 2014 Bartosz Firyn
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
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpressionValidateTest {

    /**
     * Dummy function with 2 arguments.
     */
    private final Function beta = new AbstractFunction("beta", 2) {

        @Override
        public double apply(double... args) {
            return args[1] - args[0];
        }
    };

    /**
     * Dummy function with 3 arguments.
     */
    private final Function gamma = new AbstractFunction("gamma", 3) {

        @Override
        public double apply(double... args) {
            return args[0] * args[1] / args[2];
        }
    };

    /**
     * Dummy function with 7 arguments.
     */
    private final Function eta = new AbstractFunction("eta", 7) {

        @Override
        public double apply(double... args) {
            double x = 0;
            for (double a : args) {
                x += a;
            }
            return x;
        }
    };

    // valid scenarios

    @Test
    void testValidateNumber() {
        Expression exp = new ExpressionBuilder("1")
                .build();
        ValidationResult result = exp.validate(false);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateNumberPositive() {
        Expression exp = new ExpressionBuilder("+1")
                .build();
        ValidationResult result = exp.validate(false);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateNumberNegative() {
        Expression exp = new ExpressionBuilder("-1")
                .build();
        ValidationResult result = exp.validate(false);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateOperator() {
        Expression exp = new ExpressionBuilder("x + 1 + 2")
                .variable("x")
                .build();
        ValidationResult result = exp.validate(false);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateFunction() {
        Expression exp = new ExpressionBuilder("sin(x)")
                .variable("x")
                .build();
        ValidationResult result = exp.validate(false);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateFunctionPositive() {
        Expression exp = new ExpressionBuilder("+sin(x)")
                .variable("x")
                .build();
        ValidationResult result = exp.validate(false);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateFunctionNegative() {
        Expression exp = new ExpressionBuilder("-sin(x)")
                .variable("x")
                .build();
        ValidationResult result = exp.validate(false);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateFunctionAndOperator() {
        Expression exp = new ExpressionBuilder("sin(x + 1 + 2)")
                .variable("x")
                .build();
        ValidationResult result = exp.validate(false);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateFunctionWithTwoArguments() {
        Expression exp = new ExpressionBuilder("beta(x, y)")
                .variables("x", "y")
                .functions(beta)
                .build();
        ValidationResult result = exp.validate(false);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateFunctionWithTwoArgumentsAndOperator() {
        Expression exp = new ExpressionBuilder("beta(x, y + 1)")
                .variables("x", "y")
                .functions(beta)
                .build();
        ValidationResult result = exp.validate(false);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateFunctionWithThreeArguments() {
        Expression exp = new ExpressionBuilder("gamma(x, y, z)")
                .variables("x", "y", "z")
                .functions(gamma)
                .build();
        ValidationResult result = exp.validate(false);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateFunctionWithThreeArgumentsAndOperator() {
        Expression exp = new ExpressionBuilder("gamma(x, y, z + 1)")
                .variables("x", "y", "z")
                .functions(gamma)
                .build();
        ValidationResult result = exp.validate(false);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateFunctionWithTwoAndThreeArguments() {
        Expression exp = new ExpressionBuilder("gamma(x, beta(y, h), z)")
                .variables("x", "y", "z", "h")
                .functions(gamma, beta)
                .build();
        ValidationResult result = exp.validate(false);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateFunctionWithTwoAndThreeArgumentsAndOperator() {
        Expression exp = new ExpressionBuilder("gamma(x, beta(y, h), z + 1)")
                .variables("x", "y", "z", "h")
                .functions(gamma, beta)
                .build();
        ValidationResult result = exp.validate(false);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateFunctionWithTwoAndThreeArgumentsAndMultipleOperator() {
        Expression exp = new ExpressionBuilder("gamma(x * 2 / 4, beta(y, h + 1 + 2), z + 1 + 2 + 3 + 4)")
                .variables("x", "y", "z", "h")
                .functions(gamma, beta)
                .build();
        ValidationResult result = exp.validate(false);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateFunctionWithSevenArguments() {
        Expression exp = new ExpressionBuilder("eta(1, 2, 3, 4, 5, 6, 7)")
                .functions(eta)
                .build();
        ValidationResult result = exp.validate(false);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateFunctionWithSevenArgumentsAndOperator() {
        Expression exp = new ExpressionBuilder("eta(1, 2, 3, 4, 5, 6, 7) * 2 * 3 * 4")
                .functions(eta)
                .build();
        ValidationResult result = exp.validate(false);
        assertTrue(result.isValid());
    }

    // invalid scenarios

    @Test
    void testValidateInvalidFunction() {
        Expression exp = new ExpressionBuilder("sin()")
                .build();
        ValidationResult result = exp.validate(false);
        assertFalse(result.isValid());
    }

    @Test
    void testValidateInvalidOperand() {
        Expression exp = new ExpressionBuilder("1 + ")
                .build();
        ValidationResult result = exp.validate(false);
        assertFalse(result.isValid());
    }

    @Test
    void testValidateInvalidFunctionWithTooFewArguments() {
        Expression exp = new ExpressionBuilder("beta(1)")
                .functions(beta)
                .build();
        ValidationResult result = exp.validate(false);
        assertFalse(result.isValid());
    }

    @Test
    void testValidateInvalidFunctionWithTooFewArgumentsAndOperands() {
        Expression exp = new ExpressionBuilder("beta(1 + )")
                .functions(beta)
                .build();
        ValidationResult result = exp.validate(false);
        assertFalse(result.isValid());
    }

    @Test
    void testValidateInvalidFunctionWithManyArguments() {
        Expression exp = new ExpressionBuilder("beta(1, 2, 3)")
                .functions(beta)
                .build();
        ValidationResult result = exp.validate(false);
        assertFalse(result.isValid());
    }

    @Test
    void testValidateInvalidOperator() {
        Expression exp = new ExpressionBuilder("+")
                .build();
        ValidationResult result = exp.validate(false);
        assertFalse(result.isValid());
    }

    // Thanks go out to werwiesel for reporting the issue
    // https://github.com/fasseg/exp4j/issues/59
    @Test
    void testNoArgFunctionValidation() {
        Function now = new AbstractFunction("now", 0) {
            @Override
            public double apply(double... args) {
                return (double) new Date().getTime();
            }
        };
        Expression e = new ExpressionBuilder("14*now()")
                .function(now)
                .build();
        assertTrue(e.validate().isValid());

        e = new ExpressionBuilder("now()")
                .function(now)
                .build();
        assertTrue(e.validate().isValid());

        e = new ExpressionBuilder("sin(now())")
                .function(now)
                .build();
        assertTrue(e.validate().isValid());

        e = new ExpressionBuilder("sin(now()) % 14")
                .function(now)
                .build();
        assertTrue(e.validate().isValid());
    }

}

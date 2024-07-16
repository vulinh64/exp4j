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
package net.objecthunter.exp4j.tokenizer;

import net.objecthunter.exp4j.AbstractFunction;
import net.objecthunter.exp4j.AbstractOperator;
import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.operator.Operator;
import net.objecthunter.exp4j.operator.Operators;
import org.junit.Test;

import java.util.*;

import static net.objecthunter.exp4j.TestUtil.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TokenizerTest {

    @Test
    public void testTokenization1() {
        Tokenizer tokenizer = new Tokenizer("1.222331", null, null, null);
        assertNumberToken(tokenizer.nextToken(), 1.222331d);
    }

    @Test
    public void testTokenization2() {
        Tokenizer tokenizer = new Tokenizer(".222331", null, null, null);
        assertNumberToken(tokenizer.nextToken(), .222331d);
    }

    @Test
    public void testTokenization3() {
        Tokenizer tokenizer = new Tokenizer("3e2", null, null, null);
        assertNumberToken(tokenizer.nextToken(), 300d);
    }

    @Test
    public void testTokenization4() {
        Tokenizer tokenizer = new Tokenizer("3+1", null, null, null);

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 3d);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "+", 2, Operators.PRECEDENCE_ADDITION);

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 1d);

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization5() {
        Tokenizer tokenizer = new Tokenizer("+3", null, null, null);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "+", 1, Operators.PRECEDENCE_UNARY_PLUS);

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 3d);

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization6() {
        Tokenizer tokenizer = new Tokenizer("-3", null, null, null);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operators.PRECEDENCE_UNARY_MINUS);

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 3d);

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization7() {
        Tokenizer tokenizer = new Tokenizer("---++-3", null, null, null);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operators.PRECEDENCE_UNARY_MINUS);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operators.PRECEDENCE_UNARY_MINUS);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operators.PRECEDENCE_UNARY_MINUS);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "+", 1, Operators.PRECEDENCE_UNARY_PLUS);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "+", 1, Operators.PRECEDENCE_UNARY_PLUS);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operators.PRECEDENCE_UNARY_MINUS);

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 3d);

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization8() {
        Tokenizer tokenizer = new Tokenizer("---++-3.004", null, null, null);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operators.PRECEDENCE_UNARY_MINUS);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operators.PRECEDENCE_UNARY_MINUS);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operators.PRECEDENCE_UNARY_MINUS);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "+", 1, Operators.PRECEDENCE_UNARY_PLUS);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "+", 1, Operators.PRECEDENCE_UNARY_PLUS);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operators.PRECEDENCE_UNARY_MINUS);

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 3.004d);

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization9() {
        Tokenizer tokenizer = new Tokenizer("3+-1", null, null, null);

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 3d);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "+", 2, Operators.PRECEDENCE_ADDITION);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operators.PRECEDENCE_UNARY_MINUS);

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 1d);

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization10() {
        Tokenizer tokenizer = new Tokenizer("3+-1-.32++2", null, null, null);

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 3d);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "+", 2, Operators.PRECEDENCE_ADDITION);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operators.PRECEDENCE_UNARY_MINUS);

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 1d);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 2, Operators.PRECEDENCE_SUBTRACTION);

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 0.32d);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "+", 2, Operators.PRECEDENCE_ADDITION);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "+", 1, Operators.PRECEDENCE_UNARY_PLUS);

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 2d);

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization11() {
        Tokenizer tokenizer = new Tokenizer("2+", null, null, null);

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 2d);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "+", 2, Operators.PRECEDENCE_ADDITION);

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization12() {
        Tokenizer tokenizer = new Tokenizer("log(1)", null, null, null);

        assertTrue(tokenizer.hasNext());
        assertFunctionToken(tokenizer.nextToken(), "log", 1);

        assertTrue(tokenizer.hasNext());
        assertOpenParenthesesToken(tokenizer.nextToken());

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 1d);

        assertTrue(tokenizer.hasNext());
        assertCloseParenthesesToken(tokenizer.nextToken());

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization13() {
        Tokenizer tokenizer = new Tokenizer("x", null, null, new HashSet<>(Collections.singletonList("x")));

        assertTrue(tokenizer.hasNext());
        assertVariableToken(tokenizer.nextToken(), "x");

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization14() {
        Tokenizer tokenizer = new Tokenizer("2*x-log(3)", null, null, new HashSet<>(Collections.singletonList("x")));

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 2d);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "*", 2, Operators.PRECEDENCE_MULTIPLICATION);

        assertTrue(tokenizer.hasNext());
        assertVariableToken(tokenizer.nextToken(), "x");

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 2, Operators.PRECEDENCE_SUBTRACTION);

        assertTrue(tokenizer.hasNext());
        assertFunctionToken(tokenizer.nextToken(), "log", 1);

        assertTrue(tokenizer.hasNext());
        assertOpenParenthesesToken(tokenizer.nextToken());

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 3d);

        assertTrue(tokenizer.hasNext());
        assertCloseParenthesesToken(tokenizer.nextToken());

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization15() {
        Tokenizer tokenizer = new Tokenizer("2*xlog+log(3)", null, null, new HashSet<>(Collections.singletonList("xlog")));

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 2d);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "*", 2, Operators.PRECEDENCE_MULTIPLICATION);

        assertTrue(tokenizer.hasNext());
        assertVariableToken(tokenizer.nextToken(), "xlog");

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "+", 2, Operators.PRECEDENCE_ADDITION);

        assertTrue(tokenizer.hasNext());
        assertFunctionToken(tokenizer.nextToken(), "log", 1);

        assertTrue(tokenizer.hasNext());
        assertOpenParenthesesToken(tokenizer.nextToken());

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 3d);

        assertTrue(tokenizer.hasNext());
        assertCloseParenthesesToken(tokenizer.nextToken());

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization16() {
        Tokenizer tokenizer = new Tokenizer("2*x+-log(3)", null, null, new HashSet<>(Collections.singletonList("x")));

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 2d);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "*", 2, Operators.PRECEDENCE_MULTIPLICATION);

        assertTrue(tokenizer.hasNext());
        assertVariableToken(tokenizer.nextToken(), "x");

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "+", 2, Operators.PRECEDENCE_ADDITION);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operators.PRECEDENCE_UNARY_MINUS);

        assertTrue(tokenizer.hasNext());
        assertFunctionToken(tokenizer.nextToken(), "log", 1);

        assertTrue(tokenizer.hasNext());
        assertOpenParenthesesToken(tokenizer.nextToken());

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 3d);

        assertTrue(tokenizer.hasNext());
        assertCloseParenthesesToken(tokenizer.nextToken());

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization17() {
        Tokenizer tokenizer = new Tokenizer("2 * x + -log(3)", null, null, new HashSet<>(Collections.singletonList("x")));

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 2d);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "*", 2, Operators.PRECEDENCE_MULTIPLICATION);

        assertTrue(tokenizer.hasNext());
        assertVariableToken(tokenizer.nextToken(), "x");

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "+", 2, Operators.PRECEDENCE_ADDITION);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operators.PRECEDENCE_UNARY_MINUS);

        assertTrue(tokenizer.hasNext());
        assertFunctionToken(tokenizer.nextToken(), "log", 1);

        assertTrue(tokenizer.hasNext());
        assertOpenParenthesesToken(tokenizer.nextToken());

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 3d);

        assertTrue(tokenizer.hasNext());
        assertCloseParenthesesToken(tokenizer.nextToken());

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization18() {
        Function log2 = new AbstractFunction("log2") {

            @Override
            public double apply(double... args) {
                return Math.log(args[0]) / Math.log(2d);
            }
        };

        Map<String, Function> funcs = new HashMap<>(1);
        funcs.put(log2.getName(), log2);
        Tokenizer tokenizer = new Tokenizer("log2(4)", funcs, null, null);

        assertTrue(tokenizer.hasNext());
        assertFunctionToken(tokenizer.nextToken(), "log2", 1);

        assertTrue(tokenizer.hasNext());
        assertOpenParenthesesToken(tokenizer.nextToken());

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 4d);

        assertTrue(tokenizer.hasNext());
        assertCloseParenthesesToken(tokenizer.nextToken());

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization19() {
        Function avg = new AbstractFunction("avg", 2) {

            @Override
            public double apply(double... args) {
                double sum = 0;
                for (double arg : args) {
                    sum += arg;
                }
                return sum / args.length;
            }
        };
        Map<String, Function> funcs = new HashMap<>(1);
        funcs.put(avg.getName(), avg);
        Tokenizer tokenizer = new Tokenizer("avg(1,2)", funcs, null, null);

        assertTrue(tokenizer.hasNext());
        assertFunctionToken(tokenizer.nextToken(), "avg", 2);

        assertTrue(tokenizer.hasNext());
        assertOpenParenthesesToken(tokenizer.nextToken());

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 1d);

        assertTrue(tokenizer.hasNext());
        assertFunctionSeparatorToken(tokenizer.nextToken());

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 2d);

        assertTrue(tokenizer.hasNext());
        assertCloseParenthesesToken(tokenizer.nextToken());

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization20() {
        Operator factorial = new AbstractOperator("!", 1, true, Operators.PRECEDENCE_POWER + 1) {
            @Override
            public double apply(double... args) {
                return 0d;
            }
        };
        Map<String, Operator> operators = new HashMap<>(1);
        operators.put(factorial.getSymbol(), factorial);

        Tokenizer tokenizer = new Tokenizer("2!", null, operators, null);

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 2d);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "!", factorial.getNumOperands(), factorial.getPrecedence());

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization21() {
        Tokenizer tokenizer = new Tokenizer("log(x) - y * (sqrt(x^cos(y)))", null, null, new HashSet<>(Arrays.asList("x", "y")));

        assertTrue(tokenizer.hasNext());
        assertFunctionToken(tokenizer.nextToken(), "log", 1);

        assertTrue(tokenizer.hasNext());
        assertOpenParenthesesToken(tokenizer.nextToken());

        assertTrue(tokenizer.hasNext());
        assertVariableToken(tokenizer.nextToken(), "x");

        assertTrue(tokenizer.hasNext());
        assertCloseParenthesesToken(tokenizer.nextToken());

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 2, Operators.PRECEDENCE_SUBTRACTION);

        assertTrue(tokenizer.hasNext());
        assertVariableToken(tokenizer.nextToken(), "y");

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "*", 2, Operators.PRECEDENCE_MULTIPLICATION);

        assertTrue(tokenizer.hasNext());
        assertOpenParenthesesToken(tokenizer.nextToken());

        assertTrue(tokenizer.hasNext());
        assertFunctionToken(tokenizer.nextToken(), "sqrt", 1);

        assertTrue(tokenizer.hasNext());
        assertOpenParenthesesToken(tokenizer.nextToken());

        assertTrue(tokenizer.hasNext());
        assertVariableToken(tokenizer.nextToken(), "x");

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "^", 2, Operators.PRECEDENCE_POWER);

        assertTrue(tokenizer.hasNext());
        assertFunctionToken(tokenizer.nextToken(), "cos", 1);

        assertTrue(tokenizer.hasNext());
        assertOpenParenthesesToken(tokenizer.nextToken());

        assertTrue(tokenizer.hasNext());
        assertVariableToken(tokenizer.nextToken(), "y");

        assertTrue(tokenizer.hasNext());
        assertCloseParenthesesToken(tokenizer.nextToken());

        assertTrue(tokenizer.hasNext());
        assertCloseParenthesesToken(tokenizer.nextToken());

        assertTrue(tokenizer.hasNext());
        assertCloseParenthesesToken(tokenizer.nextToken());

        assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testTokenization22() {
        Tokenizer tokenizer = new Tokenizer("--2 * (-14)", null, null, null);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operators.PRECEDENCE_UNARY_MINUS);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operators.PRECEDENCE_UNARY_MINUS);

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 2d);

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "*", 2, Operators.PRECEDENCE_MULTIPLICATION);

        assertTrue(tokenizer.hasNext());
        assertOpenParenthesesToken(tokenizer.nextToken());

        assertTrue(tokenizer.hasNext());
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operators.PRECEDENCE_UNARY_MINUS);

        assertTrue(tokenizer.hasNext());
        assertNumberToken(tokenizer.nextToken(), 14d);

        assertTrue(tokenizer.hasNext());
        assertCloseParenthesesToken(tokenizer.nextToken());

        assertFalse(tokenizer.hasNext());
    }
}

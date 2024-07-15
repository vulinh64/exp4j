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
import org.junit.Test;

import static org.junit.Assert.*;

public class FunctionsTest {
    @Test(expected = IllegalArgumentException.class)
    public void testFunctionNameNull() {
        Function f = new AbstractFunction(null) {
            @Override
            public double apply(double... args) {
                return 0;
            }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFunctionNameEmpty() {
        Function f = new AbstractFunction("") {
            @Override
            public double apply(double... args) {
                return 0;
            }
        };
    }

    @Test
    public void testFunctionNameZeroArgs() {
        Function f = new AbstractFunction("foo", 0) {
            @Override
            public double apply(double... args) {
                return 0;
            }
        };
        assertEquals(0f, f.apply(), 0f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFunctionNameNegativeArgs() {
        Function f = new AbstractFunction("foo", -1) {
            @Override
            public double apply(double... args) {
                return 0;
            }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalFunctionName1() {
        Function f = new AbstractFunction("1foo") {
            @Override
            public double apply(double... args) {
                return 0;
            }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalFunctionName2() {
        Function f = new AbstractFunction("_&oo") {
            @Override
            public double apply(double... args) {
                return 0;
            }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalFunctionName3() {
        Function f = new AbstractFunction("o+o") {
            @Override
            public double apply(double... args) {
                return 0;
            }
        };
    }

    @Test
    public void testCheckFunctionNames() {
        assertTrue(Function.isValidFunctionName("log"));
        assertTrue(Function.isValidFunctionName("sin"));
        assertTrue(Function.isValidFunctionName("abz"));
        assertTrue(Function.isValidFunctionName("alongfunctionnamecanhappen"));
        assertTrue(Function.isValidFunctionName("_log"));
        assertTrue(Function.isValidFunctionName("__blah"));
        assertTrue(Function.isValidFunctionName("foox"));
        assertTrue(Function.isValidFunctionName("aZ"));
        assertTrue(Function.isValidFunctionName("Za"));
        assertTrue(Function.isValidFunctionName("ZZaa"));
        assertTrue(Function.isValidFunctionName("_"));
        assertTrue(Function.isValidFunctionName("log2"));
        assertTrue(Function.isValidFunctionName("lo32g2"));
        assertTrue(Function.isValidFunctionName("_o45g2"));

        assertFalse(Function.isValidFunctionName("&"));
        assertFalse(Function.isValidFunctionName("_+log"));
        assertFalse(Function.isValidFunctionName("_k&l"));
        assertFalse(Function.isValidFunctionName("k&l"));
        assertFalse(Function.isValidFunctionName("+log"));
        assertFalse(Function.isValidFunctionName("fo-o"));
        assertFalse(Function.isValidFunctionName("log+"));
        assertFalse(Function.isValidFunctionName("perc%"));
        assertFalse(Function.isValidFunctionName("del$a"));
    }
}

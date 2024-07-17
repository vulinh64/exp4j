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
import net.objecthunter.exp4j.function.Functions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionsTest {

    @Test
    void testFunctionNameNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new AbstractFunction(null) {
                    @Override
                    public double apply(double... args) {
                        return 0;
                    }
                });
    }

    @Test
    void testFunctionNameEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> new AbstractFunction("") {
                    @Override
                    public double apply(double... args) {
                        return 0;
                    }
                });
    }

    @Test
    void testFunctionNameZeroArgs() {
        Function f = new AbstractFunction("foo", 0) {
            @Override
            public double apply(double... args) {
                return 0;
            }
        };

        assertEquals(0f, f.apply(), 0f);
    }

    @Test
    void testFunctionNameNegativeArgs() {
        assertThrows(IllegalArgumentException.class, () -> new AbstractFunction("foo", -1) {
            @Override
            public double apply(double... args) {
                return 0;
            }
        });
    }

    @Test
    void testIllegalFunctionName1() {
        assertThrows(IllegalArgumentException.class, () -> new AbstractFunction("1foo") {
            @Override
            public double apply(double... args) {
                return 0;
            }
        });
    }

    @Test
    void testIllegalFunctionName2() {
        assertThrows(IllegalArgumentException.class, () -> new AbstractFunction("_&oo") {
            @Override
            public double apply(double... args) {
                return 0;
            }
        });
    }

    @Test
    void testIllegalFunctionName3() {
        assertThrows(IllegalArgumentException.class, () -> new AbstractFunction("o+o") {
            @Override
            public double apply(double... args) {
                return 0;
            }
        });
    }

    @Test
    void testCheckFunctionNames() {
        assertTrue(Functions.isValidFunctionName("log"));
        assertTrue(Functions.isValidFunctionName("sin"));
        assertTrue(Functions.isValidFunctionName("abz"));
        assertTrue(Functions.isValidFunctionName("alongfunctionnamecanhappen"));
        assertTrue(Functions.isValidFunctionName("_log"));
        assertTrue(Functions.isValidFunctionName("__blah"));
        assertTrue(Functions.isValidFunctionName("foox"));
        assertTrue(Functions.isValidFunctionName("aZ"));
        assertTrue(Functions.isValidFunctionName("Za"));
        assertTrue(Functions.isValidFunctionName("ZZaa"));
        assertTrue(Functions.isValidFunctionName("_"));
        assertTrue(Functions.isValidFunctionName("log2"));
        assertTrue(Functions.isValidFunctionName("lo32g2"));
        assertTrue(Functions.isValidFunctionName("_o45g2"));

        assertFalse(Functions.isValidFunctionName("&"));
        assertFalse(Functions.isValidFunctionName("_+log"));
        assertFalse(Functions.isValidFunctionName("_k&l"));
        assertFalse(Functions.isValidFunctionName("k&l"));
        assertFalse(Functions.isValidFunctionName("+log"));
        assertFalse(Functions.isValidFunctionName("fo-o"));
        assertFalse(Functions.isValidFunctionName("log+"));
        assertFalse(Functions.isValidFunctionName("perc%"));
        assertFalse(Functions.isValidFunctionName("del$a"));
    }
}

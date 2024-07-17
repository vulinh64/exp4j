/*
 * Copyright 2015 Federico Vera
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

import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Federico Vera (dktcoding [at] gmail)
 */
class ArrayStackTest {

    public ArrayStackTest() {
    }

    @Test
    void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new ArrayStack(-1));
    }

    @Test
    void testPushNoSize() {
        ArrayStack stack = new ArrayStack();

        stack.push(0);
        stack.push(1);
        stack.push(3);

        assertEquals(3, stack.size());
    }

    @Test
    void testPushLessSize() {
        ArrayStack stack = new ArrayStack(5);

        for (int i = 0; i < 5; i++) {
            stack.push(i);
        }

        assertEquals(5, stack.size());
    }

    @Test
    void testPeek() {
        ArrayStack stack = new ArrayStack(5);

        for (int i = 0; i < 5; i++) {
            stack.push(i);
        }

        assertEquals(4d, stack.peek(), 0d);
        assertEquals(4d, stack.peek(), 0d);
        assertEquals(4d, stack.peek(), 0d);
    }

    @Test
    void testPeek2() {
        ArrayStack stack = new ArrayStack(5);
        stack.push(-1);
        double old = -1;
        for (int i = 0; i < 5; i++) {
            assertEquals(old, stack.peek(), 0d);
            stack.push(i);
            old = i;
            assertEquals(old, stack.peek(), 0d);
        }
    }

    @Test
    void testPeekNoData() {
        assertThrows(EmptyStackException.class, () -> {
            ArrayStack stack = new ArrayStack(5);
            stack.peek();
        });
    }

    @Test
    void testPop() {
        ArrayStack stack = new ArrayStack(5);

        for (int i = 0; i < 5; i++) {
            stack.push(i);
        }

        while (!stack.isEmpty()) {
            stack.pop();
        }

        assertNotNull(stack);
    }

    @Test
    void testPop2() {
        ArrayStack stack = new ArrayStack(5);

        for (int i = 0; i < 5; i++) {
            stack.push(i);
        }

        assertThrows(EmptyStackException.class, () -> {
            while (true) {
                stack.pop();
            }
        });
    }

    @Test
    void testPop3() {
        ArrayStack stack = new ArrayStack(5);

        for (int i = 0; i < 5; i++) {
            stack.push(i);
            assertEquals(1, stack.size());
            assertEquals(i, stack.pop(), 0d);
        }

        assertEquals(0, stack.size());
        assertTrue(stack.isEmpty());
    }

    @Test
    void testPopNoData() {
        assertThrows(EmptyStackException.class, () -> {
            ArrayStack stack = new ArrayStack(5);
            stack.pop();
        });
    }

    @Test
    void testIsEmpty() {
        ArrayStack stack = new ArrayStack(5);
        assertTrue(stack.isEmpty());
        stack.push(4);
        assertFalse(stack.isEmpty());
        stack.push(4);
        assertFalse(stack.isEmpty());
        stack.push(4);
        assertFalse(stack.isEmpty());
        stack.pop();
        stack.pop();
        stack.pop();
        assertTrue(stack.isEmpty());
        stack.push(4);
        assertFalse(stack.isEmpty());
        stack.peek();
        assertFalse(stack.isEmpty());
        stack.pop();
        assertTrue(stack.isEmpty());
    }

    @Test
    void testSize() {
        ArrayStack stack = new ArrayStack(5);
        assertEquals(0, stack.size());
        stack.push(4);
        assertEquals(1, stack.size());
        stack.peek();
        assertEquals(1, stack.size());
        stack.pop();
        assertEquals(0, stack.size());
    }

}
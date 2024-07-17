package net.objecthunter.exp4j.tokenizer;

import net.objecthunter.exp4j.exception.UnknownFunctionOrVariableException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * This test is to check if {@link UnknownFunctionOrVariableException} generated when expression
 * contains unknown function or variable contains necessary expected details.
 *
 * @author Bartosz Firyn (sarxos)
 */
class TokenizerUnknownTokenOrVariableTest {

    @Test
    void testTokenizationOfUnknownVariable() {
        assertThrows(IllegalArgumentException.class, () -> {
            Tokenizer tokenizer = new Tokenizer("3 + x", null, null, null);
            while (tokenizer.hasNext()) {
                tokenizer.nextToken();
            }
        });
    }

    @Test
    void testTokenizationOfUnknownVariable1Details() {

        Tokenizer tokenizer = new Tokenizer("3 + x", null, null, null);
        tokenizer.nextToken(); // 3
        tokenizer.nextToken(); // +

        try {
            tokenizer.nextToken(); // x
            fail("Variable 'x' should be unknown!");
        } catch (UnknownFunctionOrVariableException e) {
            assertEquals("x", e.getToken());
            assertEquals(4, e.getPosition());
            assertEquals("3 + x", e.getExpression());
        }
    }

    @Test
    void testTokenizationOfUnknownVariable2Details() {

        Tokenizer tokenizer = new Tokenizer("x + 3", null, null, null);

        try {
            tokenizer.nextToken(); // x
            fail("Variable 'x' should be unknown!");
        } catch (UnknownFunctionOrVariableException e) {
            assertEquals("x", e.getToken());
            assertEquals(0, e.getPosition());
            assertEquals("x + 3", e.getExpression());
        }
    }

    @Test
    void testTokenizationOfUnknownFunction() {
        assertThrows(IllegalArgumentException.class, () -> {
            Tokenizer tokenizer = new Tokenizer("3 + p(1)", null, null, null);
            while (tokenizer.hasNext()) {
                tokenizer.nextToken();
            }
        });
    }

    @Test
    void testTokenizationOfUnknownFunction1Details() {

        Tokenizer tokenizer = new Tokenizer("3 + p(1)", null, null, null);
        tokenizer.nextToken(); // 3
        tokenizer.nextToken(); // +

        try {
            tokenizer.nextToken(); // p
            fail("Function 'p' should be unknown!");
        } catch (UnknownFunctionOrVariableException e) {
            assertEquals("p", e.getToken());
            assertEquals(4, e.getPosition());
            assertEquals("3 + p(1)", e.getExpression());
        }
    }

    @Test
    void testTokenizationOfUnknownFunction2Details() {

        Tokenizer tokenizer = new Tokenizer("p(1) + 3", null, null, null);

        try {
            tokenizer.nextToken(); // p
            fail("Function 'p' should be unknown!");
        } catch (UnknownFunctionOrVariableException e) {
            assertEquals("p", e.getToken());
            assertEquals(0, e.getPosition());
            assertEquals("p(1) + 3", e.getExpression());
        }
    }
}

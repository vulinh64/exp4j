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

import net.objecthunter.exp4j.tokenizer.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TestUtil {

    public static void assertVariableToken(Token token, String name) {
        assertEquals(TokenType.TOKEN_VARIABLE, token.getType());
        assertEquals(name, ((VariableToken) token).getName());
    }

    public static void assertOpenParenthesesToken(Token token) {
        assertEquals(TokenType.TOKEN_PARENTHESES_OPEN, token.getType());
    }

    public static void assertCloseParenthesesToken(Token token) {
        assertEquals(TokenType.TOKEN_PARENTHESES_CLOSE, token.getType());
    }

    public static void assertFunctionToken(Token token, String name, int i) {
        assertEquals(TokenType.TOKEN_FUNCTION, token.getType());
        FunctionToken f = (FunctionToken) token;
        assertEquals(i, f.getFunction().getNumArguments());
        assertEquals(name, f.getFunction().getName());
    }

    public static void assertOperatorToken(Token tok, String symbol, int numArgs, int precedence) {
        assertEquals(TokenType.TOKEN_OPERATOR, tok.getType());
        assertEquals(numArgs, ((OperatorToken) tok).getOperator().getNumOperands());
        assertEquals(symbol, ((OperatorToken) tok).getOperator().getSymbol());
        assertEquals(precedence, ((OperatorToken) tok).getOperator().getPrecedence());
    }

    public static void assertNumberToken(Token tok, double v) {
        assertEquals(TokenType.TOKEN_NUMBER, tok.getType());
        assertEquals(v, ((NumberToken) tok).getValue(), 0d);
    }

    public static void assertFunctionSeparatorToken(Token t) {
        assertEquals(TokenType.TOKEN_SEPARATOR, t.getType());
    }
}

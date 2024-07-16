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
package net.objecthunter.exp4j.shuntingyard;

import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.operator.Operator;
import net.objecthunter.exp4j.tokenizer.OperatorToken;
import net.objecthunter.exp4j.tokenizer.Token;
import net.objecthunter.exp4j.tokenizer.TokenType;
import net.objecthunter.exp4j.tokenizer.Tokenizer;

import java.util.*;

/**
 * Shunting yard implementation to convert infix to reverse polish notation
 */
public class ShuntingYard {

    private ShuntingYard() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * Convert a Set of tokens from infix to reverse polish notation
     *
     * @param expression             the expression to convert
     * @param userFunctions          the custom functions used
     * @param userOperators          the custom operators used
     * @param variableNames          the variable names used in the expression
     * @param implicitMultiplication set to false to turn off implicit multiplication
     * @return a {@link net.objecthunter.exp4j.tokenizer.Token} array containing the result
     */
    public static List<Token> convertToRPN(final String expression, final Map<String, Function> userFunctions,
                                           final Map<String, Operator> userOperators, final Set<String> variableNames, final boolean implicitMultiplication) {
        final Deque<Token> deque = new ArrayDeque<>();
        final List<Token> output = new ArrayList<>();

        final Tokenizer tokenizer = new Tokenizer(expression, userFunctions, userOperators, variableNames, implicitMultiplication);
        while (tokenizer.hasNext()) {
            Token token = tokenizer.nextToken();
            switch (token.getType()) {
                case TOKEN_NUMBER:
                case TOKEN_VARIABLE:
                    output.add(token);
                    break;
                case TOKEN_FUNCTION:
                    deque.push(token); // Equivalent to stack.add
                    break;
                case TOKEN_SEPARATOR:
                    while (!deque.isEmpty() && deque.peek().getType() != TokenType.TOKEN_PARENTHESES_OPEN) {
                        output.add(deque.pop());
                    }
                    if (deque.isEmpty() || deque.peek().getType() != TokenType.TOKEN_PARENTHESES_OPEN) {
                        throw new IllegalArgumentException("Misplaced function separator ',' or mismatched parentheses");
                    }
                    break;
                case TOKEN_OPERATOR:
                    while (!deque.isEmpty() && deque.peek().getType() == TokenType.TOKEN_OPERATOR) {
                        OperatorToken o1 = (OperatorToken) token;
                        OperatorToken o2 = (OperatorToken) deque.peek();
                        if (o1.getOperator().getNumOperands() == 1 && o2.getOperator().getNumOperands() == 2) {
                            break;
                        } else if ((o1.getOperator().isLeftAssociative() && o1.getOperator().getPrecedence() <= o2.getOperator().getPrecedence())
                                || (o1.getOperator().getPrecedence() < o2.getOperator().getPrecedence())) {
                            output.add(deque.pop());
                        } else {
                            break;
                        }
                    }
                    deque.push(token);
                    break;
                case TOKEN_PARENTHESES_OPEN:
                    deque.push(token);
                    break;
                case TOKEN_PARENTHESES_CLOSE:
                    while (deque.peek().getType() != TokenType.TOKEN_PARENTHESES_OPEN) {
                        output.add(deque.pop());
                    }
                    deque.pop();
                    if (!deque.isEmpty() && deque.peek().getType() == TokenType.TOKEN_FUNCTION) {
                        output.add(deque.pop());
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Token type encountered. This should not happen");
            }
        }
        while (!deque.isEmpty()) {
            Token t = deque.pop();
            if (t.getType() == TokenType.TOKEN_PARENTHESES_CLOSE || t.getType() == TokenType.TOKEN_PARENTHESES_OPEN) {
                throw new IllegalArgumentException("Mismatched parentheses detected. Please check the expression");
            } else {
                output.add(t);
            }
        }
        return output;
    }
}

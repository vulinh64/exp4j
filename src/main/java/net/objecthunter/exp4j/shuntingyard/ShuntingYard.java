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

import java.util.*;
import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.operator.Operator;
import net.objecthunter.exp4j.tokenizer.OperatorToken;
import net.objecthunter.exp4j.tokenizer.Token;
import net.objecthunter.exp4j.tokenizer.TokenType;
import net.objecthunter.exp4j.tokenizer.Tokenizer;

/** Shunting yard implementation to convert infix to reverse polish notation */
public class ShuntingYard {

  private ShuntingYard() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }

  /**
   * Convert a Set of tokens from infix to reverse polish notation
   *
   * @param expression the expression to convert
   * @param userFunctions the custom functions used
   * @param userOperators the custom operators used
   * @param variableNames the variable names used in the expression
   * @param implicitMultiplication set to false to turn off implicit multiplication
   * @return a {@link net.objecthunter.exp4j.tokenizer.Token} array containing the result
   */
  public static List<Token> convertToRPN(
      String expression,
      Map<String, Function> userFunctions,
      Map<String, Operator> userOperators,
      Set<String> variableNames,
      boolean implicitMultiplication) {
    Deque<Token> deque = new ArrayDeque<>();
    List<Token> output = new ArrayList<>();

    Tokenizer tokenizer =
        new Tokenizer(
            expression, userFunctions, userOperators, variableNames, implicitMultiplication);

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
          parseSeparatorToken(deque, output);
          break;
        case TOKEN_OPERATOR:
          parseOperatorToken(deque, (OperatorToken) token, output);
          deque.push(token);
          break;
        case TOKEN_PARENTHESES_OPEN:
          deque.push(token);
          break;
        case TOKEN_PARENTHESES_CLOSE:
          parseCloseParenthesisToken(deque, output);
          break;
        default:
          throw new IllegalArgumentException(
              "Unknown Token type encountered. This should not happen");
      }
    }

    detectUnbalancedParenthesis(deque, output);

    return output;
  }

  private static void detectUnbalancedParenthesis(Deque<Token> deque, List<Token> output) {
    while (!deque.isEmpty()) {
      Token t = deque.pop();
      if (t.getType() == TokenType.TOKEN_PARENTHESES_CLOSE
          || t.getType() == TokenType.TOKEN_PARENTHESES_OPEN) {
        throw new IllegalArgumentException(
            "Mismatched parentheses detected. Please check the expression");
      } else {
        output.add(t);
      }
    }
  }

  private static void parseCloseParenthesisToken(Deque<Token> deque, List<Token> output) {
    while (!deque.isEmpty() && deque.peek().getType() != TokenType.TOKEN_PARENTHESES_OPEN) {
      output.add(deque.pop());
    }

    deque.pop();

    if (!deque.isEmpty() && deque.peek().getType() == TokenType.TOKEN_FUNCTION) {
      output.add(deque.pop());
    }
  }

  private static void parseOperatorToken(
      Deque<Token> deque, OperatorToken token, List<Token> output) {
    while (!deque.isEmpty() && deque.peek().getType() == TokenType.TOKEN_OPERATOR) {
      OperatorToken operation2 = (OperatorToken) deque.peek();

      Operator operator = token.getOperator();
      int precedence = operator.getPrecedence();

      if (operator.getNumOperands() == 1
          && Optional.ofNullable(operation2)
              .map(OperatorToken::getOperator)
              .map(Operator::getNumOperands)
              .filter(op -> op == 2)
              .isPresent()) {
        break;
      }

      Optional<Integer> optionalPrecedence =
          Optional.ofNullable(operation2)
              .map(OperatorToken::getOperator)
              .map(Operator::getPrecedence);

      if (operator.isLeftAssociative()
              && optionalPrecedence.filter(val -> precedence <= val).isPresent()
          || optionalPrecedence.filter(val -> precedence < val).isPresent()) {
        output.add(deque.pop());
      } else {
        break;
      }
    }
  }

  private static void parseSeparatorToken(Deque<Token> deque, List<Token> output) {
    while (!deque.isEmpty() && TokenType.TOKEN_PARENTHESES_OPEN != deque.peek().getType()) {
      output.add(deque.pop());
    }

    if (deque.isEmpty() || TokenType.TOKEN_PARENTHESES_OPEN != deque.peek().getType()) {
      throw new IllegalArgumentException(
          "Misplaced function separator ',' or mismatched parentheses");
    }
  }
}

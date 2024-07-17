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
package net.objecthunter.exp4j.operator;

public class Operators {

  /** The precedence value for the addition operation */
  public static final int PRECEDENCE_ADDITION = 500;

  /** The precedence value for the subtraction operation */
  public static final int PRECEDENCE_SUBTRACTION = PRECEDENCE_ADDITION;

  /** The precedence value for the multiplication operation */
  public static final int PRECEDENCE_MULTIPLICATION = 1000;

  /** The precedence value for the division operation */
  public static final int PRECEDENCE_DIVISION = PRECEDENCE_MULTIPLICATION;

  /** The precedence value for the modulo operation */
  public static final int PRECEDENCE_MODULO = PRECEDENCE_DIVISION;

  /** The precedence value for the power operation */
  public static final int PRECEDENCE_POWER = 10000;

  /** The precedence value for the unary minus operation */
  public static final int PRECEDENCE_UNARY_MINUS = 5000;

  /** The precedence value for the unary plus operation */
  public static final int PRECEDENCE_UNARY_PLUS = PRECEDENCE_UNARY_MINUS;

  /** The set of allowed operator chars */
  private static final char[] ALLOWED_OPERATOR_CHARS = {
    '+', '-', '*', '/', '%', '^', '!', '#', '§', '$', '&', ';', ':', '~', '<', '>', '|', '=', '÷',
    '√', '∛', '⌈', '⌊'
  };

  private Operators() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }

  public static Operator getBuiltinOperator(char symbol, int numArguments) {
    switch (symbol) {
      case '+':
        return numArguments == 1 ? UnaryOperators.UNARY_PLUS : BinaryOperators.ADDITION;
      case '-':
        return numArguments == 1 ? UnaryOperators.UNARY_MINUS : BinaryOperators.SUBTRACTION;
      case '*':
        return BinaryOperators.MULTIPLICATION;
      case '÷':
      case '/':
        return BinaryOperators.DIVISION;
      case '^':
        return BinaryOperators.POWER;
      case '%':
        return BinaryOperators.MODULO;
      default:
        return null;
    }
  }

  /**
   * Check if a character is an allowed operator char
   *
   * @param ch the char to check
   * @return true if the char is allowed an an operator symbol, false otherwise
   */
  public static boolean isAllowedOperatorChar(char ch) {
    for (char allowed : ALLOWED_OPERATOR_CHARS) {
      if (ch == allowed) {
        return true;
      }
    }
    return false;
  }
}

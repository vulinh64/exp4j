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
package net.objecthunter.exp4j.function;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class representing the builtin functions available for use in expressions
 */
public class Functions {

    private Functions() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    private static final Map<String, Function> MAPS = Stream.<Function>concat(
                    Arrays.stream(UnaryFunctionEnum.values()),
                    Arrays.stream(BinaryFunctionEnum.values()))
            .collect(Collectors.toMap(Function::getName, java.util.function.Function.identity()));

  /**
   * Get the builtin function for a given name
   *
   * @param name te name of the function
   * @return a Function instance
   */
  public static Function getBuiltInFunction(String name) {
        return MAPS.get(name);
    }

    public static boolean isValidFunctionName(String name) {
        if (name == null) {
            return false;
        }

        int size = name.length();

        if (size == 0) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            char c = name.charAt(i);

            if ((!Character.isLetter(c) && c != '_') && (!Character.isDigit(c) || i == 0)) {
                return false;
            }
        }

        return true;
    }
}

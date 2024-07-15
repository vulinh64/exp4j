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

/**
 * A class representing a Function which can be used in an expression
 */
public interface Function {

    static boolean isValidFunctionName(final String name) {
        if (name == null) {
            return false;
        }

        final int size = name.length();

        if (size == 0) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            final char c = name.charAt(i);
            if ((Character.isLetter(c) || c == '_') || Character.isDigit(c) && i > 0) {
                continue;
            }
            return false;
        }
        return true;
    }

    String getName();

    int getNumArguments();

    double apply(double... args);
}

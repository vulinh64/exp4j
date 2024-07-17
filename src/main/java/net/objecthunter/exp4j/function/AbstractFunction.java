package net.objecthunter.exp4j.function;

public abstract class AbstractFunction implements Function {

  private final String name;

  protected final int numArguments;

  /**
   * Create a new Function with a given name and number of arguments
   *
   * @param name the name of the Function
   * @param numArguments the number of arguments the function takes
   */
  protected AbstractFunction(String name, int numArguments) {
    if (numArguments < 0) {
      throw new IllegalArgumentException(
          String.format("The number of function arguments can not be less than 0 for '%s'", name));
    }

    if (!Functions.isValidFunctionName(name)) {
      throw new IllegalArgumentException(String.format("The function name '%s' is invalid", name));
    }

    this.name = name;
    this.numArguments = numArguments;
  }

  /**
   * Create a new Function with a given name that takes a single argument
   *
   * @param name the name of the Function
   */
  protected AbstractFunction(String name) {
    this(name, 1);
  }

  /**
   * Get the name of the Function
   *
   * @return the name
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Get the number of arguments for this function
   *
   * @return the number of arguments
   */
  @Override
  public int getNumArguments() {
    return numArguments;
  }
}

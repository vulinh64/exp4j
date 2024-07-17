package net.objecthunter.exp4j.tokenizer;

public enum NoArgsTokenEnum implements Token {
  ARGUMENT_SEPARATOR_TOKEN(TokenType.TOKEN_SEPARATOR),
  OPEN_PARENTHESES_TOKEN(TokenType.TOKEN_PARENTHESES_OPEN), // represent open parenthesis
  CLOSE_PARENTHESES_TOKEN(TokenType.TOKEN_PARENTHESES_CLOSE); // represent close parenthesis

  private final TokenType tokenType;

  NoArgsTokenEnum(TokenType tokenType) {
    this.tokenType = tokenType;
  }

  @Override
  public TokenType getType() {
    return tokenType;
  }
}

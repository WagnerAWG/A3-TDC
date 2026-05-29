package lexer;

/**
 * Representa um token produzido pelo analisador lexico.
 *
 * Um token e a unidade atomica do processo de compilacao. Ele encapsula:
 *   - type:  a categoria lexica (ID, NUM, +, =, PRINT, etc.)
 *   - value: o texto original encontrado no codigo fonte
 *   - line:  a linha do codigo fonte onde o token foi encontrado
 *
 * A linha e armazenada para que mensagens de erro possam indicar
 * exatamente onde o problema ocorreu no codigo do usuario.
 */
public class Token {
    private final TokenType type;
    private final String value;
    private final int line;

    public Token(TokenType type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    /**
     * Representacao textual do token para exibicao ao usuario.
     * Exemplos de saida: "ID(x)", "NUM(10)", "=", "+", "PRINT", "(", ")", "EOF"
     */
    @Override
    public String toString() {
        switch (type) {
            case ID:       return "ID(" + value + ")";
            case NUM:      return "NUM(" + value + ")";
            case PRINT:    return "PRINT";
            case ASSIGN:   return "=";
            case PLUS:     return "+";
            case LPAREN:   return "(";
            case RPAREN:   return ")";
            case EOF:      return "EOF";
            default:       return type.name();
        }
    }
}

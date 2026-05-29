package lexer;

import java.util.ArrayList;
import java.util.List;

/**
 * Analisador Lexico da MiniLang.
 *
 * CONCEITO FUNDAMENTAL: Automato Finito Deterministico (AFD)
 * ==========================================================
 *
 * Este lexer implementa um AFD de 3 estados que percorre a entrada
 * caractere por caractere, realizando transicoes determinadas pela
 * classe do caractere lido:
 *
 * Estados do AFD:
 *   START  -> estado inicial, decide qual transicao tomar
 *   IN_ID  -> lendo um identificador ou palavra reservada
 *   IN_NUM -> lendo um numero inteiro
 *
 * Transicoes (funcao de transicao delta):
 *   START --[letra]------------> IN_ID
 *   START --[digito]-----------> IN_NUM
 *   START --['=']--------------> (aceita: ASSIGN)
 *   START --['+']--------------> (aceita: PLUS)
 *   START --['(']--------------> (aceita: LPAREN)
 *   START --[')']--------------> (aceita: RPAREN)
 *   START --[fim da entrada]---> (aceita: EOF)
 *
 *   IN_ID  --[letra ou digito]-> IN_ID   (loop: consome mais caracteres)
 *   IN_ID  --[outro]-----------> (aceita: ID ou PRINT)
 *
 *   IN_NUM --[digito]----------> IN_NUM  (loop: consome mais digitos)
 *   IN_NUM --[outro]-----------> (aceita: NUM)
 *
 * O AFD e determinista porque, para cada estado e simbolo de entrada,
 * existe no maximo UMA transicao possivel. Nao ha ambiguidade.
 *
 * As Expressoes Regulares equivalentes para cada token sao:
 *   ID     = [a-zA-Z_][a-zA-Z0-9_]*
 *   NUM    = [0-9]+
 *   PRINT  = "print"
 *   ASSIGN = "="
 *   PLUS   = "+"
 *   LPAREN = "("
 *   RPAREN = ")"
 */
public class Lexer {

    private final String input;  // codigo fonte completo
    private int pos;             // posicao atual do "cabecote de leitura"
    private int line;            // numero da linha atual (para mensagens de erro)

    public Lexer(String input) {
        this.input = input;
        this.pos = 0;
        this.line = 1;
    }

    /**
     * Executa a analise lexica completa, retornando a lista de tokens.
     * O metodo chama nextToken() repetidamente ate encontrar o EOF.
     */
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        while ((token = nextToken()).getType() != TokenType.EOF) {
            tokens.add(token);
        }
        tokens.add(token);  // adiciona o EOF ao final
        return tokens;
    }

    /**
     * Le o proximo token da entrada.
     * Este metodo e o nucleo do AFD: ele consulta o caractere atual
     * e decide qual transicao de estado executar.
     *
     * Fluxo de decisao (funcao de transicao):
     *   1. Ignora espacos em branco (skipWhitespace)
     *   2. Se fim da entrada -> retorna EOF
     *   3. Le o caractere corrente
     *   4. Se for letra -> entra no estado IN_ID  (readIdentifierOrKeyword)
     *   5. Se for digito -> entra no estado IN_NUM (readNumber)
     *   6. Se for simbolo unico -> aceita imediatamente (switch)
     */
    private Token nextToken() {
        skipWhitespace();
        if (pos >= input.length()) {
            return new Token(TokenType.EOF, "", line);
        }

        char c = input.charAt(pos);

        if (isLetter(c)) {
            return readIdentifierOrKeyword();
        }

        if (isDigit(c)) {
            return readNumber();
        }

        // Simbolos de caractere unico: cada caso e uma transicao direta
        // do estado START para um estado de aceitacao
        switch (c) {
            case '=':
                pos++;
                return new Token(TokenType.ASSIGN, "=", line);
            case '+':
                pos++;
                return new Token(TokenType.PLUS, "+", line);
            case '(':
                pos++;
                return new Token(TokenType.LPAREN, "(", line);
            case ')':
                pos++;
                return new Token(TokenType.RPAREN, ")", line);
            default:
                // Caractere desconhecido: avanca e retorna EOF
                pos++;
                return new Token(TokenType.EOF, "", line);
        }
    }

    /**
     * Estado IN_ID do AFD: leitura de identificador ou palavra reservada.
     *
     * Loop: consome letras e digitos consecutivos (fecho de Kleene *).
     * Ao encontrar um caractere que nao pertence ao padrao [a-zA-Z0-9],
     * o AFD para e verifica se o lexema lido e a palavra reservada "print"
     * (retorna PRINT) ou um identificador comum (retorna ID).
     */
    private Token readIdentifierOrKeyword() {
        int start = pos;
        while (pos < input.length() && (isLetter(input.charAt(pos)) || isDigit(input.charAt(pos)))) {
            pos++;
        }
        String value = input.substring(start, pos);
        if ("print".equals(value)) {
            return new Token(TokenType.PRINT, value, line);
        }
        return new Token(TokenType.ID, value, line);
    }

    /**
     * Estado IN_NUM do AFD: leitura de numero inteiro.
     *
     * Loop: consome digitos consecutivos [0-9]+.
     * Ao encontrar um caractere nao-digito, o AFD para e aceita o token NUM
     * com o valor numerico lido.
     */
    private Token readNumber() {
        int start = pos;
        while (pos < input.length() && isDigit(input.charAt(pos))) {
            pos++;
        }
        String value = input.substring(start, pos);
        return new Token(TokenType.NUM, value, line);
    }

    /**
     * Ignora espacos, tabulacoes e quebras de linha.
     * Quebras de linha incrementam o contador de linhas.
     * Nao produz tokens - sao descartados pelo AFD.
     */
    private void skipWhitespace() {
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (c == ' ' || c == '\t' || c == '\r') {
                pos++;
            } else if (c == '\n') {
                pos++;
                line++;
            } else {
                break;
            }
        }
    }

    /**
     * Verifica se um caractere e uma letra (a-z, A-Z) ou underscore (_).
     * Usado pelo AFD para a transicao START -> IN_ID.
     */
    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    /**
     * Verifica se um caractere e um digito (0-9).
     * Usado pelo AFD para a transicao START -> IN_NUM.
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}

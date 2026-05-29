package lexer;

/**
 * Tipos de tokens reconhecidos pelo analisador lexico da MiniLang.
 *
 * Cada tipo corresponde a uma Expressao Regular especifica:
 *   ID     -> [a-zA-Z_][a-zA-Z0-9_]*
 *   NUM    -> [0-9]+
 *   ASSIGN -> "="
 *   PLUS   -> "+"
 *   PRINT  -> "print" (palavra reservada)
 *   LPAREN -> "("
 *   RPAREN -> ")"
 *   EOF    -> fim da entrada
 *
 * CONCEITO: Expressoes Regulares (ER)
 * Cada token e definido por uma ER que descreve o padrao lexico.
 * O Lexer transforma essas ERs em um Automato Finito Deterministico (AFD)
 * que reconhece os tokens percorrendo a entrada caractere por caractere.
 */
public enum TokenType {
    ID,       // identificador: nome de variavel
    NUM,      // numero inteiro literal
    ASSIGN,   // operador de atribuicao "="
    PLUS,     // operador de adicao "+"
    PRINT,    // comando de saida "print"
    LPAREN,   // parentese esquerdo "("
    RPAREN,   // parentese direito ")"
    EOF       // marcador de fim de arquivo
}

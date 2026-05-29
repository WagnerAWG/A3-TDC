package parser;

import lexer.Token;
import lexer.TokenType;
import java.util.ArrayList;
import java.util.List;

/**
 * Analisador Sintatico da MiniLang.
 *
 * CONCEITO FUNDAMENTAL: Automato de Pilha (PDA) e Gramatica Livre de Contexto (CFL)
 * ==================================================================================
 *
 * Este parser implementa um ANALISADOR SINTATICO DESCENDENTE RECURSIVO
 * (Recursive Descent Parser), que e uma implementacao pratica de um PDA.
 *
 * A pilha do PDA e IMPLICITA: a pilha de chamadas de metodo da JVM atua como
 * a pilha do automato. Cada chamada recursiva corresponde a empilhar um
 * nao-terminal da gramatica. O retorno do metodo corresponde a desempilhar.
 *
 * Gramatica Livre de Contexto (CFL) da MiniLang (notacao BNF):
 * =============================================================
 *
 *   <program>        -> <statement_list> EOF
 *   <statement_list> -> <statement> <statement_list> | ε
 *   <statement>      -> <assignment> | <print_stmt>
 *   <assignment>     -> ID ASSIGN <expression>
 *   <print_stmt>     -> PRINT LPAREN <expression> RPAREN
 *   <expression>     -> <term> (PLUS <term>)*
 *   <term>           -> ID | NUM
 *
 * Caracteristicas desta gramatica:
 * - E LIVRE DE CONTEXTO: o lado esquerdo de cada producao e sempre um
 *   unico nao-terminal (independe do contexto ao redor).
 * - E LL(1): pode ser analisada com 1 token de lookahead (o token atual
 *   e suficiente para decidir qual producao aplicar).
 * - Nao possui recursao a esquerda (eliminada na regra de <expression>).
 *
 * Mapeamento metodo <-> nao-terminal:
 *   parseProgram()      -> <program>
 *   parseStatement()    -> <statement>
 *   parseAssignment()   -> <assignment>
 *   parsePrintStmt()    -> <print_stmt>
 *   parseExpression()   -> <expression>
 *   parseTerm()         -> <term>
 */
public class Parser {

    private final List<Token> tokens;  // lista de tokens do lexer
    private int pos;                    // indice do token atual (lookahead)
    private List<String> errors;        // lista de erros sintaticos encontrados

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
        this.errors = new ArrayList<>();
    }

    /**
     * Executa a analise sintatica completa.
     * Retorna um ParseResult contendo a AST (se sucesso) e a lista de erros.
     */
    public ParseResult parse() {
        errors.clear();
        ASTNode ast = parseProgram();
        return new ParseResult(ast, errors);
    }

    /**
     * Resultado da analise sintatica.
     * Encapsula a AST gerada e a lista de erros encontrados.
     */
    public static class ParseResult {
        public final ASTNode ast;
        public final List<String> errors;

        ParseResult(ASTNode ast, List<String> errors) {
            this.ast = ast;
            this.errors = errors;
        }

        public boolean success() {
            return errors.isEmpty();
        }
    }

    /**
     * Retorna o token atual (lookahead) sem avancar.
     * Se ja consumiu todos os tokens, retorna um token EOF artificial.
     */
    private Token current() {
        if (pos < tokens.size()) {
            return tokens.get(pos);
        }
        return new Token(TokenType.EOF, "", -1);
    }

    /**
     * Consome o token atual e avanca o indice.
     * Equivalente a "ler o proximo simbolo da fita" no PDA.
     */
    private Token advance() {
        Token t = current();
        pos++;
        return t;
    }

    /**
     * Verifica se o token atual e do tipo esperado.
     * Se for, consome e retorna true. Caso contrario, retorna false.
     * Usado para decisoes condicionais no parser.
     */
    private boolean match(TokenType expected) {
        if (current().getType() == expected) {
            advance();
            return true;
        }
        return false;
    }

    /**
     * Exige que o token atual seja do tipo esperado.
     * Se for, consome e retorna o token.
     * Se nao for, registra um erro sintatico e retorna o token atual
     * (modo panico simplificado: nao interrompe a analise).
     *
     * Este metodo e central para o PDA: ele verifica se a entrada
     * corresponde a producao esperada pela gramatica.
     */
    private Token expect(TokenType expected) {
        Token t = current();
        if (t.getType() == expected) {
            return advance();
        }
        errors.add("Linha " + t.getLine() + ": esperado '" + expected + "', encontrado '" + t + "'");
        return t;
    }

    /**
     * <program> -> <statement_list> EOF
     *
     * Processa todos os statements ate encontrar EOF.
     * Cada statement (atribuicao ou print) e adicionado a lista.
     */
    private ASTNode parseProgram() {
        List<ASTNode> statements = new ArrayList<>();
        while (current().getType() != TokenType.EOF) {
            ASTNode stmt = parseStatement();
            if (stmt != null) {
                statements.add(stmt);
            } else {
                break;  // erro critico: interrompe
            }
        }
        expect(TokenType.EOF);
        return new ProgramNode(statements);
    }

    /**
     * <statement> -> <assignment> | <print_stmt>
     *
     * Decide qual producao aplicar com base no primeiro token (lookahead):
     *   - ID    -> parseAssignment (atribuicao)
     *   - PRINT -> parsePrintStmt  (impressao)
     *   - EOF   -> fim, retorna null
     *   - outro -> erro sintatico
     *
     * Esta decisao e LL(1): um unico token de lookahead e suficiente
     * para determinar sem ambiguidade qual regra aplicar.
     */
    private ASTNode parseStatement() {
        Token first = current();

        if (first.getType() == TokenType.ID) {
            return parseAssignment();
        }

        if (first.getType() == TokenType.PRINT) {
            return parsePrintStmt();
        }

        if (first.getType() == TokenType.EOF) {
            return null;
        }

        errors.add("Linha " + first.getLine() + ": instrucao invalida iniciada com '" + first + "'");
        advance();
        return null;
    }

    /**
     * <assignment> -> ID ASSIGN <expression>
     *
     * Exemplo: "x = 10 + 5"
     *   1. Consome ID("x")
     *   2. Consome ASSIGN ("=")
     *   3. Analisa a expressao "10 + 5"
     *   4. Retorna AssignNode("x", expressao)
     *
     * O PDA empilha o estado de parseExpression() enquanto processa
     * a sub-expressao, e desempilha ao retornar.
     */
    private ASTNode parseAssignment() {
        Token idToken = expect(TokenType.ID);
        expect(TokenType.ASSIGN);
        ASTNode expr = parseExpression();
        return new AssignNode(idToken.getValue(), expr);
    }

    /**
     * <print_stmt> -> PRINT LPAREN <expression> RPAREN
     *
     * Exemplo: "print(y)"
     *   1. Consome PRINT
     *   2. Consome LPAREN ("(")
     *   3. Analisa a expressao "y"
     *   4. Consome RPAREN (")")
     *   5. Retorna PrintNode(expressao)
     */
    private ASTNode parsePrintStmt() {
        expect(TokenType.PRINT);
        expect(TokenType.LPAREN);
        ASTNode expr = parseExpression();
        expect(TokenType.RPAREN);
        return new PrintNode(expr);
    }

    /**
     * <expression> -> <term> (PLUS <term>)*
     *
     * Implementa a associatividade a ESQUERDA do operador "+".
     *
     * Algoritmo (loop, nao recursivo para evitar recursao infinita):
     *   1. Analisa o primeiro termo (left)
     *   2. Enquanto o proximo token for PLUS:
     *      a. Consome o PLUS
     *      b. Analisa o proximo termo (right)
     *      c. left = BinaryOpNode(left, "+", right)  (associatividade esquerda)
     *   3. Retorna left
     *
     * Exemplo: "1 + 2 + 3"
     *   Iteracao 1: left=NumNode(1), encontra +, right=NumNode(2)
     *               left=BinaryOpNode(NumNode(1), "+", NumNode(2))
     *   Iteracao 2: encontra +, right=NumNode(3)
     *               left=BinaryOpNode(BinaryOpNode(NumNode(1),"+",NumNode(2)), "+", NumNode(3))
     *
     * O uso de loop em vez de recursao elimina a recursao a esquerda
     * da gramatica original (<expression> -> <expression> + <term> | <term>),
     * que causaria loop infinito em um parser descendente.
     */
    private ASTNode parseExpression() {
        ASTNode left = parseTerm();

        while (current().getType() == TokenType.PLUS) {
            Token op = advance();
            ASTNode right = parseTerm();
            left = new BinaryOpNode(left, op.getValue(), right);
        }

        return left;
    }

    /**
     * <term> -> ID | NUM
     *
     * Termo atomico de uma expressao: ou e uma variavel ou um numero.
     *
     * Decisao LL(1):
     *   - ID  -> IdNode
     *   - NUM -> NumNode
     */
    private ASTNode parseTerm() {
        Token t = current();

        if (t.getType() == TokenType.ID) {
            advance();
            return new IdNode(t.getValue());
        }

        if (t.getType() == TokenType.NUM) {
            advance();
            return new NumNode(Integer.parseInt(t.getValue()));
        }

        errors.add("Linha " + t.getLine() + ": esperado ID ou NUM, encontrado '" + t + "'");
        return new NumNode(0);  // no ficticio para continuar a analise
    }
}

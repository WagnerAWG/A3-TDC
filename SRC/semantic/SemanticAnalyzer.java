package semantic;

import parser.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Analisador Semantico da MiniLang.
 *
 * CONCEITO FUNDAMENTAL: Linguagem Sensivel ao Contexto (CSL)
 * ==========================================================
 *
 * A analise semantica verifica regras que NAO podem ser expressas
 * por uma gramatica livre de contexto (CFL). Estas regras dependem
 * do CONTEXTO acumulado durante a analise do programa.
 *
 * REGRA CSL verificada:
 *   "Toda variavel usada em uma expressao deve ter sido declarada
 *    em uma atribuicao anterior no programa."
 *
 * Exemplo de codigo SINTATICAMENTE valido mas SEMANTICAMENTE invalido:
 *   y = x + 5    // x nao foi declarada antes -> ERRO SEMANTICO
 *
 * O analisador semantico PERCORRE A AST (arvore sintatica abstrata)
 * e, para cada no, consulta/atualiza a TABELA DE SIMBOLOS.
 *
 * A verificacao e SENSAVEL AO CONTEXTO porque a validade de "x"
 * na linha 2 depende do que aconteceu na linha 1 (se "x" foi atribuido).
 * Nenhuma gramatica livre de contexto pode expressar essa dependencia,
 * pois numa CFL, um ID e sempre um ID, independente do que veio antes.
 *
 * Algoritmo (caminhamento na AST):
 *   Para cada statement no programa:
 *     Se for AssignNode:
 *       1. Analisa a expressao do lado direito (verifica variaveis usadas)
 *       2. Marca a variavel do lado esquerdo como inicializada
 *     Se for PrintNode:
 *       1. Analisa a expressao (verifica variaveis usadas)
 *     Se encontrar IdNode em uma expressao:
 *       - Se a variavel NAO esta na tabela de simbolos -> ERRO
 *       - Se esta -> OK
 */
public class SemanticAnalyzer {

    private SymbolTable symbolTable;   // tabela de simbolos (contexto acumulado)
    private List<String> errors;       // erros semanticos encontrados

    /**
     * Executa a analise semantica sobre a AST.
     * Retorna um SemanticResult com a tabela de simbolos e a lista de erros.
     */
    public SemanticResult analyze(ASTNode ast) {
        symbolTable = new SymbolTable();
        errors = new ArrayList<>();

        // Percorre a AST comecando pelo no raiz (ProgramNode)
        if (ast instanceof ProgramNode) {
            ProgramNode program = (ProgramNode) ast;
            for (ASTNode stmt : program.statements) {
                analyzeNode(stmt);
            }
        }

        return new SemanticResult(symbolTable, errors);
    }

    /**
     * Dispatcher: encaminha cada tipo de no da AST para o metodo
     * de analise semantica especifico.
     */
    private void analyzeNode(ASTNode node) {
        if (node instanceof AssignNode) {
            analyzeAssign((AssignNode) node);
        } else if (node instanceof PrintNode) {
            analyzePrint((PrintNode) node);
        } else if (node instanceof IdNode) {
            analyzeId((IdNode) node);
        } else if (node instanceof BinaryOpNode) {
            analyzeBinaryOp((BinaryOpNode) node);
        }
    }

    /**
     * Analise semantica de uma atribuicao: x = expressao
     *
     * Regra CSL aplicada:
     *   1. Verifica se a expressao usa variaveis nao declaradas
     *   2. Declara/implicitamente a variavel do lado esquerdo
     *   3. Marca a variavel como inicializada (recebeu valor)
     *
     * Note que MiniLang permite ATRIBUICAO IMPLICITA:
     *   x = 10  -> declara E inicializa x
     * Nao exige declaracao previa (como "int x"), simplificando a linguagem.
     */
    private void analyzeAssign(AssignNode node) {
        analyzeExpression(node.expression);       // passo 1: verifica lado direito
        symbolTable.markInitialized(node.name);   // passos 2-3: declara e inicializa
    }

    /**
     * Analise semantica de um comando print: print(expressao)
     *
     * Apenas verifica se a expressao a ser impressa usa variaveis
     * que foram previamente declaradas.
     */
    private void analyzePrint(PrintNode node) {
        analyzeExpression(node.expression);
    }

    /**
     * Analise semantica de uma expressao (recursiva).
     *
     * Para IdNode: verifica se a variavel foi declarada.
     *   - Esta e a REGRA CSL PRINCIPAL: o uso de um ID so e valido
     *     se existe contexto previo que o declarou.
     *
     * Para BinaryOpNode: analisa recursivamente os dois lados.
     * Para NumNode: sempre valido (numeros sao constantes).
     */
    private void analyzeExpression(ASTNode expr) {
        if (expr instanceof IdNode) {
            IdNode id = (IdNode) expr;
            // VERIFICACAO CSL: a variavel existe no contexto?
            if (!symbolTable.isDeclared(id.name)) {
                errors.add("Erro Semantico: variavel '" + id.name + "' usada mas nao declarada");
            }
        } else if (expr instanceof BinaryOpNode) {
            BinaryOpNode binOp = (BinaryOpNode) expr;
            analyzeExpression(binOp.left);
            analyzeExpression(binOp.right);
        }
    }

    private void analyzeId(IdNode node) {
        if (!symbolTable.isDeclared(node.name)) {
            errors.add("Erro Semantico: variavel '" + node.name + "' usada mas nao declarada");
        }
    }

    private void analyzeBinaryOp(BinaryOpNode node) {
        analyzeExpression(node.left);
        analyzeExpression(node.right);
    }

    /**
     * Resultado da analise semantica.
     * Contem a tabela de simbolos resultante e a lista de erros.
     */
    public static class SemanticResult {
        public final SymbolTable symbolTable;
        public final List<String> errors;

        SemanticResult(SymbolTable symbolTable, List<String> errors) {
            this.symbolTable = symbolTable;
            this.errors = errors;
        }

        public boolean success() {
            return errors.isEmpty();
        }
    }
}

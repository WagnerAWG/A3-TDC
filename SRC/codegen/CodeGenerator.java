package codegen;

import parser.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerador de Codigo Intermediario da MiniLang.
 *
 * Converte a AST (arvore sintatica abstrata) em uma sequencia de
 * instrucoes no formato pseudo-assembly (codigo de 3 enderecos).
 *
 * O codigo intermediario e uma representacao independente de maquina
 * que fica entre a AST de alto nivel e a execucao final. Ele e mais
 * proximo da linguagem de maquina, usando:
 *   - Pilha de operandos (modelo de maquina de pilha)
 *   - Memoria para variaveis (mapa nome -> valor)
 *   - Instrucoes simples (LOAD, STORE, ADD, PRINT)
 *
 * CONJUNTO DE INSTRUCOES (Instruction Set):
 *   LOAD <num>     : empilha uma constante inteira
 *   LOAD_VAR <id>  : empilha o valor de uma variavel
 *   STORE <id>     : desempilha e armazena em uma variavel
 *   ADD            : desempilha dois valores e empilha a soma
 *   PRINT          : desempilha e imprime um valor
 *
 * A geracao e feita por caminhamento recursivo na AST (pos-ordem):
 * para cada no, gera-se primeiro o codigo das sub-arvores e depois
 * a instrucao correspondente ao operador do no.
 */
public class CodeGenerator {

    /**
     * Representa uma instrucao do codigo intermediario.
     * Cada instrucao tem um opcode (operacao) e um operando opcional.
     */
    public static class Instruction {
        public final String opcode;   // ex: "LOAD", "ADD", "STORE", "PRINT"
        public final String operand;  // ex: "10", "x", "" (vazio para ADD/PRINT)

        public Instruction(String opcode, String operand) {
            this.opcode = opcode;
            this.operand = operand;
        }

        @Override
        public String toString() {
            if (operand.isEmpty()) {
                return opcode;
            }
            return opcode + " " + operand;
        }
    }

    private List<Instruction> instructions;

    /**
     * Gera o codigo intermediario a partir da AST.
     *
     * @param ast raiz da arvore sintatica abstrata
     * @return lista de instrucoes no formato pseudo-assembly
     */
    public List<Instruction> generate(ASTNode ast) {
        instructions = new ArrayList<>();

        if (ast instanceof ProgramNode) {
            ProgramNode program = (ProgramNode) ast;
            for (ASTNode stmt : program.statements) {
                generateNode(stmt);
            }
        }

        return instructions;
    }

    /**
     * Dispatcher: encaminha cada no da AST para o metodo gerador
     * de codigo especifico.
     */
    private void generateNode(ASTNode node) {
        if (node instanceof AssignNode) {
            generateAssign((AssignNode) node);
        } else if (node instanceof PrintNode) {
            generatePrint((PrintNode) node);
        }
    }

    /**
     * Gera codigo para uma atribuicao: x = expressao
     *
     * Estrategia:
     *   1. Gera codigo para calcular a expressao (resultado fica no topo da pilha)
     *   2. Gera STORE para mover o resultado da pilha para a variavel
     *
     * Exemplo: x = 10 + 5 gera:
     *   LOAD 10
     *   LOAD 5
     *   ADD
     *   STORE x
     */
    private void generateAssign(AssignNode node) {
        generateExpression(node.expression);  // passo 1: calcula expressao
        instructions.add(new Instruction("STORE", node.name));  // passo 2: armazena
    }

    /**
     * Gera codigo para um comando print: print(expressao)
     *
     * Estrategia:
     *   1. Gera codigo para calcular a expressao (resultado no topo da pilha)
     *   2. Gera PRINT para exibir o valor do topo da pilha
     *
     * Exemplo: print(y) gera:
     *   LOAD_VAR y
     *   PRINT
     */
    private void generatePrint(PrintNode node) {
        generateExpression(node.expression);  // passo 1: calcula expressao
        instructions.add(new Instruction("PRINT", ""));  // passo 2: imprime
    }

    /**
     * Gera codigo para uma expressao (recursivo, caminhamento pos-ordem).
     *
     * Regras de geracao por tipo de no:
     *
     *   NumNode(n):
     *     LOAD n           // empilha constante
     *
     *   IdNode(x):
     *     LOAD_VAR x       // empilha valor da variavel
     *
     *   BinaryOpNode(esq, "+", dir):
     *     <codigo para esq>  // empilha operando esquerdo
     *     <codigo para dir>  // empilha operando direito
     *     ADD                // desempilha ambos, empilha soma
     *
     * O caminhamento pos-ordem garante que os operandos estejam na pilha
     * antes da operacao que os consome. Este e o mesmo principio usado
     * por compiladores reais ao gerar codigo para maquinas de pilha (ex: JVM).
     */
    private void generateExpression(ASTNode expr) {
        if (expr instanceof NumNode) {
            NumNode num = (NumNode) expr;
            instructions.add(new Instruction("LOAD", String.valueOf(num.value)));
        } else if (expr instanceof IdNode) {
            IdNode id = (IdNode) expr;
            instructions.add(new Instruction("LOAD_VAR", id.name));
        } else if (expr instanceof BinaryOpNode) {
            BinaryOpNode binOp = (BinaryOpNode) expr;
            generateExpression(binOp.left);   // pos-ordem: esquerda primeiro
            generateExpression(binOp.right);  // pos-ordem: direita depois
            instructions.add(new Instruction("ADD", ""));  // pos-ordem: operador por ultimo
        }
    }
}

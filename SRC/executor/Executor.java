package executor;

import codegen.CodeGenerator.Instruction;
import java.util.*;

/**
 * Executor da MiniLang - Maquina Virtual baseada em Pilha.
 *
 * CONCEITO FUNDAMENTAL: Maquina de Turing (Processamento Sequencial)
 * ==================================================================
 *
 * O executor modela uma MAQUINA DE TURING simplificada:
 *
 * Componentes da Maquina de Turing mapeados no executor:
 *   - FITA (memoria):     Map<String, Integer> memory
 *                         Armazena os valores das variaveis. A fita e
 *                         acessada por nome (endereco simbolico).
 *
 *   - CABECOTE (PC):      int pc (Program Counter)
 *                         Aponta para a instrucao atual. Avanca
 *                         sequencialmente, uma instrucao por vez.
 *
 *   - PILHA DE OPERANDOS: Deque<Integer> stack
 *                         Usada para calculos intermediarios.
 *
 *   - PROGRAMA (tabela de transicoes): List<Instruction> instructions
 *                         O conjunto de instrucoes a serem executadas,
 *                         que define o comportamento da maquina.
 *
 * Funcionamento (ciclo da Maquina de Turing):
 *   1. Le a instrucao na posicao atual do cabecote (PC)
 *   2. Executa a transicao correspondente (switch sobre o opcode)
 *   3. Avanca o cabecote (PC++)
 *   4. Repete ate o fim do programa
 *
 * Este processamento e SEQUENCIAL e DETERMINISTICO:
 * cada instrucao e executada em ordem, e o resultado de cada passo
 * depende apenas do estado atual (memoria + pilha + PC).
 */
public class Executor {

    /** Memoria da maquina (fita): mapeia nomes de variaveis para valores */
    private final Map<String, Integer> memory = new HashMap<>();

    /** Pilha de operandos para calculos intermediarios */
    private final Deque<Integer> stack = new ArrayDeque<>();

    /** Acumulador de saida (resultado dos comandos print) */
    private String output = "";

    /**
     * Executa a lista de instrucoes do codigo intermediario.
     *
     * @param instructions lista de instrucoes pseudo-assembly
     * @return string com a saida dos comandos PRINT (uma linha por print)
     */
    public String execute(List<Instruction> instructions) {
        memory.clear();
        stack.clear();
        output = "";

        // Ciclo da Maquina de Turing: processamento sequencial
        for (int pc = 0; pc < instructions.size(); pc++) {
            Instruction instr = instructions.get(pc);
            executeInstruction(instr);
        }

        return output;
    }

    /**
     * Executa uma unica instrucao (transicao da Maquina de Turing).
     *
     * Cada caso do switch corresponde a uma transicao da maquina:
     *
     * LOAD <n>:
     *   Empilha o valor constante n.
     *   Exemplo: LOAD 10 -> pilha: [10]
     *
     * LOAD_VAR <id>:
     *   Busca o valor da variavel na memoria e empilha.
     *   Se a variavel nao foi inicializada, lanca erro de execucao.
     *   Exemplo: LOAD_VAR x -> pilha: [10] (se x=10)
     *
     * STORE <id>:
     *   Desempilha o valor do topo e armazena na variavel.
     *   Exemplo: STORE y (com pilha [15]) -> memoria: y=15, pilha: []
     *
     * ADD:
     *   Desempilha dois valores, soma e empilha o resultado.
     *   Exemplo: ADD (com pilha [10, 5]) -> pilha: [15]
     *
     * PRINT:
     *   Desempilha o valor do topo e adiciona a saida.
     *   Exemplo: PRINT (com pilha [15]) -> saida: "15\n", pilha: []
     */
    private void executeInstruction(Instruction instr) {
        switch (instr.opcode) {
            case "LOAD": {
                int val = Integer.parseInt(instr.operand);
                stack.push(val);
                break;
            }
            case "LOAD_VAR": {
                Integer val = memory.get(instr.operand);
                if (val == null) {
                    throw new RuntimeException("Variavel '" + instr.operand + "' nao inicializada");
                }
                stack.push(val);
                break;
            }
            case "STORE": {
                int val = stack.pop();
                memory.put(instr.operand, val);
                break;
            }
            case "ADD": {
                int right = stack.pop();
                int left = stack.pop();
                stack.push(left + right);
                break;
            }
            case "PRINT": {
                int val = stack.pop();
                output += val + "\n";
                break;
            }
        }
    }

    /**
     * Retorna o estado final da memoria (para exibicao no console).
     */
    public Map<String, Integer> getMemory() {
        return memory;
    }

    /**
     * Retorna o estado final da pilha (para exibicao no console).
     */
    public Deque<Integer> getStack() {
        return stack;
    }
}

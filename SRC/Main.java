/**
 * Classe principal do Compilador MiniLang.
 *
 * Ponto de entrada do sistema. Apenas instancia a interface de
 * terminal (ConsoleUI) e inicia o loop interativo.
 *
 * O fluxo completo de compilacao e orquestrado por ConsoleUI,
 * que chama cada etapa na ordem correta:
 *
 *   Lexer -> Parser -> SemanticAnalyzer -> CodeGenerator -> Executor
 *
 * CONCEITOS DA TEORIA DA COMPUTACAO APLICADOS:
 *   Etapa 1 - Lexico:  Automato Finito Deterministico (AFD)
 *   Etapa 2 - Sintatico: Gramatica Livre de Contexto + Automato de Pilha (PDA)
 *   Etapa 3 - Semantico: Linguagem Sensivel ao Contexto (CSL)
 *   Etapa 5 - Execucao: Maquina de Turing (processamento sequencial)
 */
import ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        ConsoleUI ui = new ConsoleUI();
        ui.start();
    }
}

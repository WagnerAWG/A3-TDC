package ui;

import lexer.*;
import parser.*;
import semantic.*;
import codegen.*;
import executor.*;

import java.util.List;
import java.util.Scanner;

/**
 * Interface de terminal do compilador MiniLang.
 *
 * Fornece um menu interativo onde o usuario digita o codigo fonte
 * linha por linha e visualiza o resultado de CADA ETAPA da compilacao:
 *
 *   [1] Analise Lexica    -> exibe os tokens gerados pelo AFD
 *   [2] Analise Sintatica -> exibe a AST ou os erros sintaticos
 *   [3] Analise Semantica -> exibe a tabela de simbolos ou erros
 *   [4] Geracao de Codigo -> exibe o codigo pseudo-assembly
 *   [5] Execucao          -> exibe a saida do programa e estado da memoria
 *
 * A interface e puramente textual (terminal), atendendo ao requisito
 * minimo obrigatorio do projeto.
 *
 * Uso:
 *   - Digite o codigo linha por linha
 *   - Digite "fim" para finalizar a entrada e compilar
 *   - Digite "sair" para encerrar o compilador
 */
public class ConsoleUI {

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Inicia o loop principal da interface.
     * Exibe o banner, instrucoes e processa a entrada do usuario
     * ate que ele digite "sair".
     */
    public void start() {
        System.out.println("========================================");
        System.out.println("  COMPILADOR MiniLang - Versao 1.0");
        System.out.println("  Teoria da Computacao");
        System.out.println("========================================");
        System.out.println("Digite 'sair' para encerrar.");
        System.out.println("Digite o codigo MiniLang (uma linha por vez):");
        System.out.println("Exemplo:");
        System.out.println("  x = 10");
        System.out.println("  print(x)");
        System.out.println("  fim");
        System.out.println("----------------------------------------");

        while (true) {
            System.out.print("\n> ");
            String firstLine = scanner.nextLine().trim();
            if (firstLine.equalsIgnoreCase("sair")) {
                break;
            }

            if (firstLine.isEmpty()) {
                continue;
            }

            // Leitura de multiplas linhas ate o usuario digitar "fim"
            StringBuilder code = new StringBuilder();
            code.append(firstLine).append("\n");
            while (true) {
                System.out.print("  ");
                String line = scanner.nextLine();
                if (line.trim().equalsIgnoreCase("fim")) {
                    break;
                }
                code.append(line).append("\n");
            }

            System.out.println("\n--- COMPILANDO ---\n");

            String source = code.toString();
            compileAndShow(source);
        }

        scanner.close();
        System.out.println("\nCompilador encerrado.");
    }

    /**
     * Executa o fluxo completo de compilacao para um codigo fonte
     * e exibe o resultado de cada etapa.
     *
     * Fluxo (pipeline do compilador):
     *   source -> Lexer -> tokens -> Parser -> AST -> SemanticAnalyzer
     *          -> CodeGenerator -> instructions -> Executor -> saida
     *
     * @param source codigo fonte MiniLang completo
     */
    public void compileAndShow(String source) {

        // ================================================================
        // ETAPA 1: ANALISE LEXICA (AFD / Expressoes Regulares)
        // ================================================================
        System.out.println("[1] ANALISE LEXICA (AFD / Expressoes Regulares)");
        System.out.println("    Codigo fonte:");
        String[] lines = source.split("\n");
        for (String line : lines) {
            System.out.println("      " + line);
        }

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        System.out.println("\n    Tokens gerados:");
        StringBuilder tokenStr = new StringBuilder("    ");
        for (int i = 0; i < tokens.size(); i++) {
            tokenStr.append(tokens.get(i).toString());
            if (i < tokens.size() - 1) {
                tokenStr.append(", ");
            }
        }
        System.out.println(tokenStr.toString());

        // ================================================================
        // ETAPA 2: ANALISE SINTATICA (CFL / PDA)
        // ================================================================
        System.out.println("\n[2] ANALISE SINTATICA (Gramatica Livre de Contexto / PDA)");
        Parser parser = new Parser(tokens);
        Parser.ParseResult parseResult = parser.parse();

        if (!parseResult.success()) {
            System.out.println("    *** ERROS SINTATICOS ***");
            for (String err : parseResult.errors) {
                System.out.println("    " + err);
            }
            return;  // interrompe o fluxo em caso de erro sintatico
        }
        System.out.println("    Sintaxe VALIDA");
        System.out.println("\n    Arvore Sintatica Abstrata (AST):");
        System.out.print(parseResult.ast.toStringIndent("    "));

        // ================================================================
        // ETAPA 3: ANALISE SEMANTICA (CSL / Sensivel ao Contexto)
        // ================================================================
        System.out.println("\n[3] ANALISE SEMANTICA (Linguagem Sensivel ao Contexto / CSL)");
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        SemanticAnalyzer.SemanticResult semResult = analyzer.analyze(parseResult.ast);

        if (!semResult.success()) {
            System.out.println("    *** ERROS SEMANTICOS ***");
            for (String err : semResult.errors) {
                System.out.println("    " + err);
            }
            return;  // interrompe o fluxo em caso de erro semantico
        }
        System.out.println("    Semantica VALIDA");
        System.out.print(semResult.symbolTable.toString());

        // ================================================================
        // ETAPA 4: GERACAO DE CODIGO INTERMEDIARIO
        // ================================================================
        System.out.println("\n[4] GERACAO DE CODIGO INTERMEDIARIO");
        CodeGenerator codeGen = new CodeGenerator();
        List<CodeGenerator.Instruction> instructions = codeGen.generate(parseResult.ast);

        System.out.println("    Codigo Intermediario (Pseudo-Assembly):");
        for (int i = 0; i < instructions.size(); i++) {
            System.out.printf("    %3d: %s%n", i, instructions.get(i).toString());
        }

        // ================================================================
        // ETAPA 5: EXECUCAO (Maquina de Turing)
        // ================================================================
        System.out.println("\n[5] EXECUCAO (Maquina de Turing - Processamento Sequencial)");
        Executor executor = new Executor();
        try {
            String output = executor.execute(instructions);
            System.out.println("    Resultado da execucao:");
            if (!output.isEmpty()) {
                for (String outLine : output.split("\n")) {
                    System.out.println("    " + outLine);
                }
            } else {
                System.out.println("    (sem saida)");
            }
            System.out.println("    Estado final da memoria: " + executor.getMemory());
        } catch (Exception e) {
            System.out.println("    *** ERRO DE EXECUCAO: " + e.getMessage() + " ***");
        }

        System.out.println("\n--- COMPILACAO CONCLUIDA ---");
    }
}

package ui;

import lexer.Lexer;
import lexer.Token;
import java.util.List;
import parser.Parser;
import parser.Parser.ParseResult;
import semantic.SemanticAnalyzer;
import codegen.CodeGenerator;
import executor.Executor;


public class CompilerService {

    public String compile(String source, String opcao) {

    StringBuilder report = new StringBuilder();
    
    boolean mostrarTudo = opcao.equals("Todas");

    boolean mostrarLexico =
        mostrarTudo || opcao.equals("Análise Léxica");

    boolean mostrarSintatico =
        mostrarTudo || opcao.equals("Análise Sintática");

    boolean mostrarSemantico =
        mostrarTudo || opcao.equals("Análise Semântica");

    boolean mostrarCodigo =
        mostrarTudo || opcao.equals("Geração de Código");

    boolean mostrarExecucao =
        mostrarTudo || opcao.equals("Execução");
    System.out.println("Lexico: " + mostrarLexico);
    System.out.println("Sintatico: " + mostrarSintatico);
    System.out.println("Semantico: " + mostrarSemantico);
    System.out.println("Codigo: " + mostrarCodigo);
    System.out.println("Execucao: " + mostrarExecucao);
    
    System.out.println("[" + opcao + "]");

    // =========================
    // 1. ANALISE LEXICA
    // =========================

    Lexer lexer = new Lexer(source);
    List<Token> tokens = lexer.tokenize();

    if (mostrarLexico) {

        report.append("\n////////////////////////////////////\n[1] ANALISE LEXICA\n////////////////////////////////////\n\n");

        report.append("Tokens gerados:\n");

    for (Token token : tokens) {
        report.append(token.toString()).append("\n");
    }
    }
    if (opcao.equals("Análise Léxica")) {
    return report.toString();
    }

    // =========================
    // 2. ANALISE SINTATICA
    // =========================
    report.append("\n////////////////////////////////////\n[2] ANALISE SINTATICA\n////////////////////////////////////\n\n");

    Parser parser = new Parser(tokens);
    ParseResult result = parser.parse();

    if (!result.success()) {
        report.append("*** ERROS SINTATICOS ***\n");
        for (String err : result.errors) {
            report.append(err).append("\n");
        }
        return report.toString();
    }

    report.append("Sintaxe VALIDA\n");

    report.append("\nAST:\n");
    report.append(result.ast.toStringIndent("  "));
    if (opcao.equals("Análise Sintática")) {
    return report.toString();
    }
    
    // =========================
// 3. ANALISE SEMANTICA
// =========================
    if (opcao.equals("Análise Semântica")) {
    report = new StringBuilder();
    }

    report.append("\n////////////////////////////////////\n[3] ANALISE SEMANTICA\n////////////////////////////////////\n\n");

    SemanticAnalyzer analyzer = new SemanticAnalyzer();

    SemanticAnalyzer.SemanticResult semResult =
        analyzer.analyze(result.ast);

    if (!semResult.success()) {

        report.append("*** ERROS SEMANTICOS ***\n");

        for (String err : semResult.errors) {
            report.append(err).append("\n");
    }

    return report.toString();
}

report.append("Semantica VALIDA\n");
report.append(semResult.symbolTable.toString());
if (opcao.equals("Análise Semântica")) {
    return report.toString();
}

// =========================
// 4. GERACAO DE CODIGO
// =========================
if (opcao.equals("Geração de Código")) {
    report = new StringBuilder();
}

report.append("\n////////////////////////////////////\n[4] GERACAO DE CODIGO INTERMEDIARIO\n////////////////////////////////////\n\n");

CodeGenerator codeGen = new CodeGenerator();

List<CodeGenerator.Instruction> instructions =
        codeGen.generate(result.ast);

report.append("Codigo Intermediario:\n");

for (int i = 0; i < instructions.size(); i++) {

    report.append(String.format(
            "%3d: %s\n",
            i,
            instructions.get(i).toString()
    ));
}
if (opcao.equals("Geração de Código")) {
    return report.toString();
}

// =========================
// 5. EXECUCAO
// =========================
if (opcao.equals("Execução")) {
    report = new StringBuilder();
}
report.append("\n////////////////////////////////////\n[5] EXECUCAO\n////////////////////////////////////\n\n");

Executor executor = new Executor();

try {

    String output = executor.execute(instructions);

    report.append("Resultado da execucao:\n");

    if (!output.isEmpty()) {
        report.append(output);
    } else {
        report.append("(sem saida)\n");
    }

    report.append("\n");
    report.append("Estado final da memoria: ");
    report.append(executor.getMemory());

} catch (Exception e) {

    report.append("*** ERRO DE EXECUCAO ***\n");
    report.append(e.getMessage());
}

    return report.toString();
}
    }


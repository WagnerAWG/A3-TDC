package parser;

/**
 * Classe base abstrata para todos os nos da Arvore Sintatica Abstrata (AST).
 *
 * A AST e uma representacao hierarquica do programa que captura a estrutura
 * sintatica apos a analise (parsing). Diferente da arvore de derivacao
 * concreta, a AST omite detalhes sintaticos irrelevantes (como ";", "=", etc.)
 * e mantem apenas a informacao essencial para as fases seguintes do compilador.
 *
 * Cada subclasse representa um construto da linguagem:
 *   ProgramNode  -> raiz: lista de comandos
 *   AssignNode   -> atribuicao: x = expressao
 *   PrintNode    -> impressao: print(expressao)
 *   BinaryOpNode -> operacao binaria: esquerda + direita
 *   IdNode       -> referencia a variavel
 *   NumNode      -> literal inteiro
 *
 * CONCEITO: Gramatica Livre de Contexto (CFL)
 * A estrutura da AST reflete diretamente as regras da gramatica BNF
 * da MiniLang. Cada no da AST corresponde a um nao-terminal da gramatica.
 */
public abstract class ASTNode {
    /**
     * Retorna uma representacao indentada da arvore para exibicao.
     * Util na interface de terminal para visualizar a estrutura sintatica.
     */
    public abstract String toStringIndent(String indent);
}

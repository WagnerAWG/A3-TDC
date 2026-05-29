package parser;

/**
 * No raiz da AST. Representa o programa completo.
 *
 * Corresponde ao nao-terminal <program> da gramatica:
 *   <program> -> <statement_list> EOF
 *
 * Contem uma lista de statements (atribuicoes ou print) que compoem
 * o programa. Cada statement sera analisado semantica e gerado como
 * codigo intermediario na ordem em que aparece.
 */
public class ProgramNode extends ASTNode {
    public java.util.List<ASTNode> statements;

    public ProgramNode(java.util.List<ASTNode> statements) {
        this.statements = statements;
    }

    @Override
    public String toStringIndent(String indent) {
        StringBuilder sb = new StringBuilder(indent + "Program\n");
        for (ASTNode stmt : statements) {
            sb.append(stmt.toStringIndent(indent + "  "));
        }
        return sb.toString();
    }
}

package parser;

/**
 * No que representa um comando de impressao: print(expressao)
 *
 * Corresponde ao nao-terminal <print_stmt> da gramatica:
 *   <print_stmt> -> PRINT LPAREN <expression> RPAREN
 *
 * Exemplo: "print(y)" produz:
 *   PrintNode(expression=IdNode("y"))
 */
public class PrintNode extends ASTNode {
    public ASTNode expression;

    public PrintNode(ASTNode expression) {
        this.expression = expression;
    }

    @Override
    public String toStringIndent(String indent) {
        return indent + "Print\n" + expression.toStringIndent(indent + "  ");
    }
}

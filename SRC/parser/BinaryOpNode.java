package parser;

/**
 * No que representa uma operacao binaria (soma).
 *
 * Corresponde a regra da gramatica:
 *   <expression> -> <term> (PLUS <term>)*
 *
 * A associatividade a esquerda e garantida pelo parser, que constroi
 * a arvore recursivamente. Exemplo: "1 + 2 + 3" produz:
 *   BinaryOpNode(
 *     left=BinaryOpNode(NumNode(1), "+", NumNode(2)),
 *     op="+",
 *     right=NumNode(3)
 *   )
 *
 * O operador armazenado em 'op' atualmente e sempre "+" (MiniLang so
 * suporta adicao), mas a estrutura permite extensao futura.
 */
public class BinaryOpNode extends ASTNode {
    public ASTNode left;
    public String op;
    public ASTNode right;

    public BinaryOpNode(ASTNode left, String op, ASTNode right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public String toStringIndent(String indent) {
        return indent + "BinaryOp(" + op + ")\n"
             + left.toStringIndent(indent + "  ")
             + right.toStringIndent(indent + "  ");
    }
}

package parser;

/**
 * No que representa uma atribuicao: ID = expressao
 *
 * Corresponde ao nao-terminal <assignment> da gramatica:
 *   <assignment> -> ID ASSIGN <expression>
 *
 * Exemplo: "x = 10 + 5" produz:
 *   AssignNode(name="x", expression=BinaryOpNode(IdNode("10"), "+", NumNode(5)))
 *
 * O nome da variavel (name) e a expressao do lado direito (expression)
 * sao armazenados para uso nas fases de analise semantica e geracao de codigo.
 */
public class AssignNode extends ASTNode {
    public String name;
    public ASTNode expression;

    public AssignNode(String name, ASTNode expression) {
        this.name = name;
        this.expression = expression;
    }

    @Override
    public String toStringIndent(String indent) {
        return indent + "Assign(" + name + ")\n" + expression.toStringIndent(indent + "  ");
    }
}

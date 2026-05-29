package parser;

/**
 * No que representa uma referencia a uma variavel.
 *
 * Corresponde ao nao-terminal <term> quando o token e ID:
 *   <term> -> ID
 *
 * Exemplo: na expressao "x + 5", o "x" gera IdNode("x").
 *
 * Durante a analise semantica, este no sera verificado contra a
 * tabela de simbolos para garantir que a variavel foi declarada.
 */
public class IdNode extends ASTNode {
    public String name;

    public IdNode(String name) {
        this.name = name;
    }

    @Override
    public String toStringIndent(String indent) {
        return indent + "ID(" + name + ")\n";
    }
}

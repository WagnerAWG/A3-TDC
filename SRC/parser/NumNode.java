package parser;

/**
 * No que representa um literal inteiro.
 *
 * Corresponde ao nao-terminal <term> quando o token e NUM:
 *   <term> -> NUM
 *
 * Exemplo: na expressao "x + 5", o "5" gera NumNode(5).
 *
 * O valor e armazenado como int para uso direto na execucao.
 */
public class NumNode extends ASTNode {
    public int value;

    public NumNode(int value) {
        this.value = value;
    }

    @Override
    public String toStringIndent(String indent) {
        return indent + "NUM(" + value + ")\n";
    }
}

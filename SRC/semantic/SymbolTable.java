package semantic;

import java.util.HashMap;
import java.util.Map;

/**
 * Tabela de Simbolos do compilador MiniLang.
 *
 * Armazena informacoes sobre cada variavel declarada no programa:
 *   - nome da variavel
 *   - se foi inicializada (recebeu valor)
 *
 * CONCEITO: Linguagem Sensivel ao Contexto (CSL)
 * ===============================================
 * A tabela de simbolos e a estrutura de dados que permite verificar
 * RESTRICOES DEPENDENTES DE CONTEXTO.
 *
 * Uma linguagem e SENSIVEL AO CONTEXTO quando a validade de uma construcao
 * depende de informacoes que estao FORA daquele ponto especifico do codigo.
 *
 * Exemplo:
 *   y = x + 5    // "x" e valido sintaticamente (ID + NUM)
 *                 // mas so e valido semanticamente se "x" foi declarado ANTES
 *
 * A tabela de simbolos acumula contexto (variaveis declaradas) durante
 * a analise e permite consultas para verificar se uma variavel existe
 * no momento do uso.
 */
public class SymbolTable {

    /**
     * Informacao sobre um simbolo (variavel) na tabela.
     */
    public static class SymbolInfo {
        public final String name;
        public boolean initialized;   // true se a variavel ja recebeu valor

        public SymbolInfo(String name, boolean initialized) {
            this.name = name;
            this.initialized = initialized;
        }

        @Override
        public String toString() {
            return name + " (init=" + initialized + ")";
        }
    }

    private final Map<String, SymbolInfo> symbols = new HashMap<>();

    /**
     * Declara uma variavel na tabela (se ainda nao existir).
     * A variavel comeca como NAO inicializada.
     */
    public void declare(String name) {
        symbols.putIfAbsent(name, new SymbolInfo(name, false));
    }

    /**
     * Marca uma variavel como inicializada.
     * Chamado apos uma atribuicao bem-sucedida (ex: x = 10).
     * Se a variavel nao existir, ela e criada e marcada como inicializada.
     */
    public void markInitialized(String name) {
        SymbolInfo s = symbols.get(name);
        if (s == null) {
            s = new SymbolInfo(name, true);
            symbols.put(name, s);
        } else {
            s.initialized = true;
        }
    }

    /**
     * Verifica se uma variavel ja foi declarada.
     * Usado pela analise semantica para detectar uso de variaveis inexistentes.
     */
    public boolean isDeclared(String name) {
        return symbols.containsKey(name);
    }

    /**
     * Verifica se uma variavel ja foi inicializada.
     */
    public boolean isInitialized(String name) {
        SymbolInfo s = symbols.get(name);
        return s != null && s.initialized;
    }

    public SymbolInfo get(String name) {
        return symbols.get(name);
    }

    public Map<String, SymbolInfo> getAll() {
        return symbols;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Tabela de Simbolos:\n");
        for (SymbolInfo s : symbols.values()) {
            sb.append("  ").append(s.name).append(" -> inicializada=").append(s.initialized).append("\n");
        }
        return sb.toString();
    }
}

public class TabelaHash {
    private Node[] tabela;
    private int tamanho;
    private long colisoes;
    private long comparacoes;
    private HashFunction hashFunction;

    public interface HashFunction {
        int hash(String key, int tamanho);
    }

    private static class Node {
        Registro registro;
        Node next;

        Node(Registro registro) {
            this.registro = registro;
            this.next = null;
        }
    }

    public TabelaHash(int tamanho, HashFunction hashFunction) {
        this.tamanho = tamanho;
        this.tabela = new Node[tamanho];
        this.hashFunction = hashFunction;
        this.colisoes = 0;
        this.comparacoes = 0;
    }

    public static class DivisaoHash implements HashFunction {
        @Override
        public int hash(String key, int tamanho) {
            return Math.abs(key.hashCode()) % tamanho;
        }
    }

    public static class MultiplicacaoHash implements HashFunction {
        @Override
        public int hash(String key, int tamanho) {
            double A = 0.6180339887;
            double valor = Math.abs(key.hashCode()) * A;
            valor = valor - Math.floor(valor);
            return (int) (tamanho * valor);
        }
    }

    public static class DobramentoHash implements HashFunction {
        @Override
        public int hash(String key, int tamanho) {
            String strKey = String.valueOf(Math.abs(key.hashCode()));
            int soma = 0;
            for (int i = 0; i < strKey.length(); i += 2) {
                if (i + 1 < strKey.length()) {
                    soma += Integer.parseInt(strKey.substring(i, i + 2));
                } else {
                    soma += Integer.parseInt(strKey.substring(i));
                }
            }
            return soma % tamanho;
        }
    }

    public static final HashFunction DIVISAO = new DivisaoHash();
    public static final HashFunction MULTIPLICACAO = new MultiplicacaoHash();
    public static final HashFunction DOBRAMENTO = new DobramentoHash();

    public void inserir(Registro registro) {
        int index = hashFunction.hash(registro.getCodigo(), tamanho);
        Node novoNode = new Node(registro);

        if (tabela[index] == null) {
            tabela[index] = novoNode;
        } else {
            colisoes++;
            Node atual = tabela[index];
            while (atual.next != null) {
                atual = atual.next;
            }
            atual.next = novoNode;
        }
    }

    public boolean buscar(Registro registro) {
        int index = hashFunction.hash(registro.getCodigo(), tamanho);
        Node atual = tabela[index];

        while (atual != null) {
            comparacoes++;
            if (atual.registro.getCodigo().equals(registro.getCodigo())) {
                return true;
            }
            atual = atual.next;
        }
        return false;
    }

    public long getColisoes() {
        return colisoes;
    }

    public long getComparacoes() {
        return comparacoes;
    }

    public void resetComparacoes() {
        this.comparacoes = 0L;
    }

    public void resetContadores() {
        this.colisoes = 0L;
        this.comparacoes = 0L;
    }
}
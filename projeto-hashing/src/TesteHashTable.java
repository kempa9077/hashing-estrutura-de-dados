import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class TesteHashTable {
    private static final int[] TAMANHOS = {1000, 10000, 100000};
    private static final int[] QUANTIDADE_DADOS = {1000000, 5000000, 20000000};
    private static final long SEED = 12345L;
    private static final String CSV_HEADER = "QtdDados,TamanhoTabela,FuncaoHash,TempoInsercao_ms,Colisoes,TempoBusca_ms,Comparacoes_Busca\n";

    private static final int TAMANHO_AMOSTRA_BUSCA = 10000;

    public static void main(String[] args) throws Exception {
        System.out.println("Iniciando bateria de testes...");

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<String>> futureResults = new ArrayList<>();

        for (int qtdDados : QUANTIDADE_DADOS) {
            System.out.println("\nGerando " + qtdDados + " registros para a próxima bateria de testes...");
            List<Registro> dados = gerarDados(qtdDados, SEED);

            for (int tamanho : TAMANHOS) {
                for (TabelaHash.HashFunction funcao : new TabelaHash.HashFunction[]{TabelaHash.DIVISAO, TabelaHash.MULTIPLICACAO, TabelaHash.DOBRAMENTO}) {

                    Callable<String> task = () -> {
                        System.out.printf(">> Iniciando teste: Qtd=%d, Tam=%d, Funcao=%s\n", qtdDados, tamanho, funcao.getClass().getSimpleName());

                        TabelaHash tabela = new TabelaHash(tamanho, funcao);

                        long tempoInicio = System.nanoTime();
                        for (Registro reg : dados) {
                            tabela.inserir(reg);
                        }
                        long tempoFim = System.nanoTime();
                        long tempoInsercao = (tempoFim - tempoInicio) / 1_000_000;
                        long colisoes = tabela.getColisoes();

                        tabela.resetComparacoes();

                        List<Registro> dadosDeBusca = new ArrayList<>(dados);
                        Collections.shuffle(dadosDeBusca, new Random(SEED));
                        dadosDeBusca = dadosDeBusca.subList(0, Math.min(dados.size(), TAMANHO_AMOSTRA_BUSCA));

                        tempoInicio = System.nanoTime();
                        for (Registro reg : dadosDeBusca) {
                            tabela.buscar(reg);
                        }
                        tempoFim = System.nanoTime();
                        long tempoBusca = (tempoFim - tempoInicio) / 1_000_000;
                        long comparacoesTotais = tabela.getComparacoes();

                        System.out.printf("<< Teste finalizado: Qtd=%d, Tam=%d, Funcao=%s, TempoBusca=%dms, Comp=%d\n",
                                qtdDados, tamanho, funcao.getClass().getSimpleName(), tempoBusca, comparacoesTotais);

                        return String.format("%d,%d,%s,%d,%d,%d,%d\n",
                                qtdDados, tamanho, funcao.getClass().getSimpleName(),
                                tempoInsercao, colisoes, tempoBusca, comparacoesTotais);
                    };
                    futureResults.add(executor.submit(task));
                }
            }
        }

        executor.shutdown();
        executor.awaitTermination(24, TimeUnit.HOURS);

        System.out.println("\nTodos os testes foram concluídos. Coletando resultados...");
        StringBuilder resultsCsv = new StringBuilder();
        for (Future<String> future : futureResults) {
            resultsCsv.append(future.get());
        }

        salvarResultadosCSV("resultados_hash_otimizado.csv", resultsCsv);
        System.out.println("\nResultados salvos em 'resultados_hash_otimizado.csv'");
    }

    private static List<Registro> gerarDados(int quantidade, long seed) {
        List<Registro> dados = new ArrayList<>(quantidade);
        Random rand = new Random(seed);
        for (int i = 0; i < quantidade; i++) {
            StringBuilder codigo = new StringBuilder(9);
            for (int j = 0; j < 9; j++) {
                codigo.append(rand.nextInt(10));
            }
            dados.add(new Registro(codigo.toString()));
        }
        return dados;
    }

    private static void salvarResultadosCSV(String filename, StringBuilder results) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write(CSV_HEADER);
        writer.write(results.toString());
        writer.close();
    }
}
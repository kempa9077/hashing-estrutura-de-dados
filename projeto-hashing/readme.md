# Análise de Desempenho de Tabelas Hash

Este projeto implementa e analisa o desempenho de diferentes configurações de Tabelas Hash em Java. O objetivo é medir e comparar os tempos de inserção e busca, bem como o número de colisões e comparações, variando o tamanho da tabela, a quantidade de dados e a função de hash utilizada.

O código foi projetado para ser robusto e eficiente, utilizando paralelismo para acelerar a execução dos testes e gerando um arquivo CSV com os resultados para fácil análise e visualização.

## Estrutura do Projeto

O projeto é composto por três arquivos principais:

* **`Registro.java`**: Define a estrutura de dados que será armazenada na tabela. Cada `Registro` contém um código único de 9 dígitos.
* **`TabelaHash.java`**: Contém a implementação da Tabela Hash com a estratégia de tratamento de colisões por **encadeamento separado** (`separate chaining`). Inclui também a implementação de três funções de hash distintas.
* **`TesteHashTable.java`**: É o orquestrador do experimento. Ele gera os dados, executa os testes em todas as combinações de parâmetros, mede as métricas de desempenho e salva os resultados.

---

## Methodology

O experimento foi estruturado para seguir uma metodologia clara, testando sistematicamente o comportamento da Tabela Hash sob diferentes condições.

### 1. Parâmetros do Teste

Para garantir uma análise completa, o projeto utiliza uma matriz de testes com as seguintes variações:

* **Tamanho da Tabela**: `1.000`, `10.000` e `100.000` posições, com uma variação de 10x entre eles.
* **Quantidade de Dados**: `1.000.000`, `5.000.000` e `20.000.000` de registros.
* **Funções de Hash**:
    1.  **Resto da Divisão**: `hash(k) = k mod M`
    2.  **Multiplicação**: Baseado na constante da razão áurea.
    3.  **Dobramento (Folding)**: Soma os "pedaços" do código hash da chave.

### 2. Geração de Dados e Reprodutibilidade

Para garantir que todas as funções de hash sejam testadas sob as mesmas condições, os conjuntos de dados são gerados aleatoriamente, mas utilizando uma **`SEED`** fixa (`12345L`). Isso assegura que a sequência de `Registros` gerada para uma determinada quantidade de dados seja sempre a mesma, permitindo uma comparação justa entre os algoritmos.

### 3. Métricas de Desempenho

Para cada uma das 27 combinações de teste (`3 quantidades x 3 tamanhos x 3 funções`), as seguintes métricas são coletadas:

* **Tempo de Inserção (ms)**: Tempo total para inserir todos os registros no conjunto de dados.
* **Número de Colisões**: Contagem de quantas inserções ocorreram em um slot já ocupado na tabela.
* **Tempo de Busca (ms)**: Tempo total para buscar uma amostra de **10.000 registros**.
* **Número de Comparações**: Total de comparações (`equals`) realizadas durante a busca da amostra.

### 4. Otimização da Execução

Devido ao grande volume de dados, duas otimizações cruciais foram implementadas no `TesteHashTable.java`:

* **Execução Paralela**: O `ExecutorService` do Java é utilizado para rodar os testes em múltiplas threads, aproveitando todos os núcleos do processador e reduzindo drasticamente o tempo total de execução.
* **Busca por Amostragem**: Em vez de buscar todos os milhões de registros (o que tornaria o teste inviável), uma amostra aleatória e representativa de **10.000 registros** é utilizada para medir o desempenho da busca.

---

## Como Executar

1.  **Compilação**: Certifique-se de que você tem o JDK (Java Development Kit) instalado. Compile os três arquivos `.java`:
    ```bash
    javac Registro.java TabelaHash.java TesteHashTable.java
    ```

2.  **Execução**: Execute a classe principal que contém o método `main`:
    ```bash
    java TesteHashTable
    ```

3.  **Acompanhamento**: O console exibirá o progresso dos testes em tempo real, informando qual combinação está sendo executada.

4.  **Resultados**: Ao final da execução, um arquivo chamado **`resultados_hash_otimizado.csv`** será gerado no mesmo diretório.

## Análise dos Resultados

O arquivo `resultados_hash_otimizado.csv` contém os dados brutos de desempenho. Você pode importá-lo em qualquer software de planilha (como Microsoft Excel, Google Sheets) ou ferramenta de análise de dados (como Python com as bibliotecas Pandas e Matplotlib) para:

* **Criar Tabelas**: Organizar os dados para comparar diretamente as métricas.
* **Gerar Gráficos**: Visualizar o impacto do **fator de carga** (`Quantidade de Dados / Tamanho da Tabela`) no tempo de execução, colisões e comparações.
* **Concluir**: Determinar qual função de hash teve o melhor desempenho geral e em quais cenários cada uma se destacou ou falhou.
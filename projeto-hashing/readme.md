# Análise de Desempenho de Tabelas Hash

## Resumo
Este projeto realiza uma análise empírica do desempenho de Tabelas Hash em Java, utilizando a estratégia de tratamento de colisões por **encadeamento separado**. O objetivo é avaliar como diferentes **funções de hash**, **tamanhos de tabela** e **volumes de dados** impactam métricas cruciais de performance, como tempo de inserção, tempo de busca, número de colisões e comparações. Os resultados são sistematicamente coletados e exportados para um arquivo CSV, permitindo uma análise quantitativa e visual detalhada.

---

## 1. Implementação e Estrutura

O projeto foi desenvolvido em Java e está organizado em três classes principais que trabalham em conjunto para executar o experimento.

* **`Registro.java`**: Uma classe simples que representa o dado a ser armazenado. Contém um código único de 9 dígitos e implementa `equals()` e `hashCode()` de forma correta, o que é fundamental para o funcionamento da tabela hash.

* **`TabelaHash.java`**: O coração do projeto. Implementa a tabela hash usando um vetor de nós (`Node[]`) onde cada posição pode ser o início de uma lista encadeada.
    * **Tratamento de Colisão**: Utiliza o método de **encadeamento separado** (`separate chaining`). Quando ocorre uma colisão, o novo registro é adicionado ao final da lista encadeada no índice correspondente.
    * **Flexibilidade**: A classe foi projetada para aceitar diferentes funções de hash através da interface `HashFunction`, permitindo trocar o algoritmo de hashing de forma limpa e eficiente.

* **`TesteHashTable.java`**: O orquestrador dos testes. É responsável por:
    1.  Gerar os conjuntos de dados com `SEED` fixa para garantir reprodutibilidade.
    2.  Executar os testes em todas as 27 combinações de parâmetros.
    3.  Utilizar um `ExecutorService` para rodar os testes em paralelo, otimizando o tempo de execução.
    4.  Medir os tempos e contadores (colisões, comparações) de forma precisa.
    5.  Salvar os resultados consolidados no arquivo `resultados_hash_otimizado.csv`.

---

## 2. Metodologia e Escolhas

A metodologia foi definida para cobrir cenários variados e extrair conclusões significativas sobre o comportamento da estrutura.

### 2.1. Funções de Hash Selecionadas

Foram escolhidas três funções de hash clássicas com características distintas:

1.  **Resto da Divisão (`DivisaoHash`)**: `hash(k) = k mod M`. É a mais simples e rápida. Seu desempenho é excelente quando o tamanho `M` da tabela é um número primo, pois ajuda a espalhar melhor as chaves.
2.  **Multiplicação (`MultiplicacaoHash`)**: Utiliza a multiplicação do `hashCode` por uma constante (a razão áurea, ≈ 0.618) e extrai a parte fracionária para determinar o índice. A vantagem teórica é que a escolha de `M` (tamanho da tabela) não é tão crítica quanto no método da divisão.
3.  **Dobramento (`DobramentoHash`)**: Converte o `hashCode` para uma string, quebra-a em "pedaços" de 2 dígitos, soma esses pedaços e usa o resto da divisão da soma pelo tamanho da tabela. A intenção é fazer com que todos os dígitos da chave original contribuam para o resultado final.

### 2.2. Tamanhos das Tabelas e Conjuntos de Dados

A análise se baseia na interação entre o volume de dados e o espaço disponível na tabela, cujo conceito é medido pelo **Fator de Carga ($\alpha$)**:

$$\alpha = \frac{\text{Quantidade de Dados}}{\text{Tamanho da Tabela}}$$

Quanto maior o fator de carga, maior o número médio de elementos por "slot" da tabela e, consequentemente, pior o desempenho.

* **Tamanhos da Tabela**: `1.000`, `10.000` e `100.000`.
* **Conjuntos de Dados**: `1.000.000`, `5.000.000` e `20.000.000` de registros.

Esta combinação gera cenários com fatores de carga que variam de **10** (1M de dados / 100k de espaço) a **20.000** (20M de dados / 1k de espaço), testando a estrutura em condições de baixa a altíssima sobrecarga.

---

## 3. Resultados e Gráficos Comparativos

A análise a seguir é baseada nos dados coletados no arquivo `resultados_hash_otimizado.csv`.

*(Instrução: Gere os gráficos em uma ferramenta de sua preferência (Excel, Google Sheets, Python/Matplotlib) a partir do arquivo CSV e insira as imagens nos locais indicados abaixo).*

### 3.1. Análise de Colisões

O número de colisões reflete a capacidade da função de hash de espalhar os dados uniformemente pela tabela. Funções melhores resultam em menos colisões para o mesmo fator de carga.

**Observações:**
* As funções de **Divisão** e **Multiplicação** apresentam um número de colisões muito similar e consistentemente menor que a função de Dobramento.
* Isso indica que ambas possuem uma qualidade de distribuição superior, preenchendo mais slots vazios antes de começar a colidir.
* A função de **Dobramento** gera um número significativamente maior de colisões, o que sugere uma má distribuição das chaves e a criação de "clusters" (muitas chaves mapeadas para poucos índices).

### 3.2. Análise de Comparações na Busca

O número de comparações é a métrica mais direta para medir a eficiência da busca. Em uma tabela com encadeamento, ele está diretamente ligado ao comprimento médio das listas encadeadas.

**Observações:**
* O número de comparações cresce de forma quase linear com o aumento do fator de carga para todas as funções, o que é esperado.
* Novamente, **Divisão** e **Multiplicação** mostram um desempenho muito superior, com um número de comparações visivelmente menor em todos os cenários.
* O desempenho da função de **Dobramento** é o pior, exigindo um número muito maior de comparações para encontrar os mesmos elementos, confirmando que suas listas encadeadas são, em média, mais longas.

### 3.3. Análise do Tempo de Execução (Busca e Inserção)

O tempo de execução reflete não só a qualidade da distribuição (medida pelas comparações), mas também o custo computacional da própria função de hash.

**Observações:**
* **Tempo de Inserção**: A função de **Dobramento** é ordens de magnitude mais lenta que as outras duas. Isso ocorre porque sua implementação é baseada em operações de **manipulação de String** (`String.valueOf`, `substring`, `parseInt`), que são computacionalmente muito mais caras do que as operações puramente **aritméticas** usadas pela Divisão e Multiplicação.
* **Tempo de Busca**: O tempo de busca é um reflexo tanto do número de comparações quanto do custo da função. A superioridade da Divisão e da Multiplicação é evidente, pois ambas são mais rápidas para calcular e resultam em menos comparações.

---

## 4. Discussão: Qual foi a Melhor Função?

Com base em todas as métricas analisadas, podemos eleger os vencedores e explicar os motivos.

### 🏆 Melhor Desempenho: Divisão e Multiplicação

As funções de **Resto da Divisão** e **Multiplicação** foram as melhores em todos os cenários, com uma leve vantagem sendo difícil de determinar sem uma análise estatística mais profunda, pois seus resultados foram muito próximos.

**Por que foram melhores?**
1.  **Eficiência Computacional**: Ambas são implementadas com poucas operações aritméticas, que são executadas de forma nativa e extremamente rápida pelo processador.
2.  **Qualidade da Distribuição**: Matematicamente, elas são projetadas para espalhar bem as chaves, minimizando o comprimento das listas de colisão. Isso foi comprovado pelo menor número de colisões e, consequentemente, de comparações na busca.

### 🐢 Pior Desempenho: Dobramento

A função de **Dobramento**, na forma como foi implementada, foi indiscutivelmente a pior em todos os aspectos.

**Por que foi pior?**
1.  **Custo Computacional Elevado**: A dependência de conversões entre número-string, criação de substrings e parsing dentro de um loop tornou o cálculo de cada hash extremamente lento.
2.  **Distribuição Deficiente**: Os resultados mostraram que a soma dos "pedaços" não conseguiu gerar um conjunto de índices bem distribuído, levando a mais colisões e listas encadeadas maiores.

---

## 5. Conclusão

Este trabalho demonstrou na prática os princípios fundamentais que governam o desempenho de tabelas hash. Concluiu-se que, para um bom desempenho, a escolha da função de hash é crítica e deve priorizar duas características: **baixo custo computacional** e **alta qualidade de distribuição**.

As funções baseadas em operações aritméticas simples, como **Divisão** e **Multiplicação**, provaram ser muito superiores a uma implementação mais complexa e baseada em strings, como a de **Dobramento**.

Além disso, foi evidenciado o papel central do **fator de carga ($\alpha$)**. Mesmo com a melhor função de hash, o desempenho degrada severamente à medida que o fator de carga aumenta, reforçando que, em aplicações reais, é essencial manter a tabela com uma ocupação razoável, utilizando técnicas de redimensionamento (rehashing) quando necessário.
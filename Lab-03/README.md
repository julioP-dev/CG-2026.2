# Lab 3 - Transformações geométricas 2D

Implementação das Partes 3 (reflexão e cisalhamento) e 4 (motor de composição),
integrada à interface de `origin/Interface-lab3`. Os métodos têm documentação
Javadoc no código, incluindo parâmetros, convenções e condições de erro.

## Compilar e executar

Requisito: JDK 17 ou superior (verificado com JDK 21). Na raiz do repositório,
execute no PowerShell:

```powershell
$fontes = Get-ChildItem Lab-02/src/lab02/*.java, Lab-03/src/cg/*.java
javac -encoding UTF-8 -d out $fontes.FullName
java -cp out cg.Main
```

Na janela, selecione a aba **Lab 03 - Transformações 2D**. Carregue um modelo,
escolha uma reflexão ou cisalhamento, clique em **Adicionar à composição** e
depois em **Aplicar composição**. A ordem da lista é a ordem de aplicação.
O original aparece em azul, as etapas em cinza e o resultado em vermelho.

Os modelos são um quadrado e um hexágono (regulares) e um polígono irregular côncavo.
Todos têm centroide na origem cartesiana. O modo de cliques permite criar outros
modelos; nele, cabe ao usuário posicionar o polígono em torno da origem.

Também é possível **digitar 3 ou 4 vértices** (x e y de cada um; o 4º é opcional) e
clicar em **Definir**. Cada coordenada precisa ser um número dentro do mundo visível
(com o display de 800x600: x de -400 a 399 e y de -299 a 300). Depois a aplicação exige
ao menos três vértices e recusa polígonos com arestas cruzadas, vértices repetidos
ou área zero (`ValidadorPoligono`); polígonos côncavos continuam permitidos.

## Responsabilidades e contratos

| Classe | Parte | Responsabilidade |
| --- | --- | --- |
| `Rasterizador` | 1, existente | Arestas pelo ponto médio, com saída por `Tela.setPixel`. |
| `TransformacoesBasicas` | 2 | Translação, escala e rotação. |
| `ReflexaoCisalhamento` | 3 | Quatro reflexões e cisalhamentos em X e em Y. |
| `Matriz3` | 4 | Identidade, produto 3x3 e aplicação a coordenadas homogêneas. |
| `Composicao` | 4 | Ordem de aplicação, transformação de polígonos e operações com ponto fixo. |
| `MotorComposicao` | Integração das Partes 3 e 4 | Adapta `Operacao` aos módulos e implementa `MotorTransformacoes`. |
| `PainelTransformacoes` / `DisplayTransformacoes` | 5, existentes com ajustes | Controles, etapas, coordenadas de tela e exibição. |

`MotorProvisorio` foi substituído por `MotorComposicao`. A Parte 2 continua sob
responsabilidade de seu integrante. Enquanto seu módulo não estiver conectado,
o seletor mostra apenas as seis operações disponíveis da Parte 3. O motor rejeita
explicitamente chamadas a operações básicas sem integração.

## Convenções matemáticas

- Ponto em coluna: `P = (x, y, 1)` e `P' = M · P`.
- `a.multiplicar(b)` retorna `a · b`, sem alterar as entradas.
- `Composicao.compor(A, B, C)` aplica A primeiro, depois B e C: retorna `C · B · A`.
- Uma composição vazia retorna a identidade.
- Coordenadas permanecem em `double`; somente `Rasterizador.drawPolygon` arredonda.
- A formatação da matriz no histórico não interfere no cálculo.
- O mundo usa X para a direita e Y para cima. Somente o display inverte Y e
  desloca a origem para o centro da imagem.
- `Matriz3.aplicar` calcula `(X, Y, W)` e devolve `(X/W, Y/W)`.
  W igual a zero não representa um ponto cartesiano finito e é rejeitado.
- O campo público `Matriz3.m` segue o contrato do grupo. O construtor com
  `double[][]` copia seus dados; os métodos não modificam matrizes recebidas.
  Quem editar `m` diretamente deve preservar sua dimensão e valores finitos.
- `new Matriz3()` cria uma matriz nula para preenchimento pelo grupo.
  Use `Matriz3.identidade()` para uma transformação neutra.

## Parte 3: reflexão e cisalhamento

Todas as matrizes têm última linha `[0, 0, 1]` e são novas a cada chamada.

| Método | Duas primeiras linhas | Efeito |
| --- | --- | --- |
| `reflexaoX()` | `[1, 0, 0]; [0, -1, 0]` | `(x, y) → (x, -y)` |
| `reflexaoY()` | `[-1, 0, 0]; [0, 1, 0]` | `(x, y) → (-x, y)` |
| `reflexaoOrigem()` | `[-1, 0, 0]; [0, -1, 0]` | `(x, y) → (-x, -y)` |
| `reflexaoYeX()` | `[0, 1, 0]; [1, 0, 0]` | `(x, y) → (y, x)` |
| `cisalhamentoX(shx)` | `[1, shx, 0]; [0, 1, 0]` | `(x, y) → (x + shx*y, y)` |
| `cisalhamentoY(shy)` | `[1, 0, 0]; [shy, 1, 0]` | `(x, y) → (x, y + shy*x)` |

### Dedução das reflexões para o relatório

Na reflexão na origem, a imagem está à mesma distância da origem e em direção
oposta: as duas coordenadas trocam de sinal. As duas primeiras linhas são,
portanto, `[-1, 0, 0]` e `[0, -1, 0]`. É o mesmo resultado de compor as reflexões
nos eixos X e Y, ou de uma rotação de 180 graus na origem.

Na reflexão em `y = x`, os pontos `(a, a)` ficam fixos. Para um ponto `(x, y)`,
a imagem `(y, x)` tem ponto médio sobre essa reta, e o segmento entre os dois
é perpendicular a ela. Logo, a transformação troca as coordenadas. Outra
dedução é girar -45 graus, refletir no eixo X e girar +45 graus:
`R(45) · Fx · R(-45)`. O produto resulta nas linhas `[0, 1, 0]` e `[1, 0, 0]`.

### Cisalhamentos e ordem

O seletor apresenta X e Y separadamente, conforme G5. Com fator 1 em ambos:

```text
P = (1, 2)
X depois Y: (1, 2) → (3, 2) → (3, 5)
Y depois X: (1, 2) → (1, 3) → (4, 3)
```

O tipo antigo `Operacao.Tipo.CISALHAMENTO` permanece aceito pela API para
compatibilidade com chamadas à interface anterior, mas não aparece no seletor.
Ele calcula simultaneamente `(x + a*y, y + b*x)` usando as coordenadas originais:
para o mesmo exemplo, resulta em `(3, 3)`. Isso não é a composição dos dois
cisalhamentos individuais.

## Parte 4: composição e ponto fixo

Para cada nova operação, a composição pré-multiplica a matriz acumulada:
`acumulada = proxima · acumulada`. Depois, aplica a matriz resultante uma vez
a cada vértice e retorna um novo polígono com uma nova lista.

Exemplo da não comutatividade para o relatório:

```text
P = (1, 0), T = translação por (2, 0), R = rotação anti-horária de 90 graus
compor(T, R) = R · T: (1, 0) → (3, 0) → (0, 3)
compor(R, T) = T · R: (1, 0) → (0, 1) → (2, 1)
```

Duas rotações em torno da origem comutam entre si. Duas escalas nos mesmos
eixos e em torno da origem também comutam. Esses casos não permitem trocar
livremente a ordem de transformações de tipos diferentes.

As operações com ponto fixo seguem o processo da aula: levar o pivô à origem,
transformar e levar de volta. A implementação usa a expansão algébrica dos
produtos para permanecer independente da classe da Parte 2:

```text
Escala: T(p) · S · T(-p)
[sx  0   (1-sx)*px]
[0   sy  (1-sy)*py]
[0   0   1        ]

Rotação: T(p) · R · T(-p), c = cos(graus), s = sen(graus)
[c  -s  (1-c)*px + s*py]
[s   c  (1-c)*py - s*px]
[0   0  1              ]
```

O cosseno e o seno são calculados após `Math.toRadians(graus)`. Para escala
(2,2) com pivô (2,1), o ponto (4,1) passa a (6,1), como no slide 25 da G5.
O pivô permanece inalterado. Esses métodos estão disponíveis na API e nos testes.
Na interface, as opções **Escala com ponto fixo** e **Rotação com ponto fixo** (campos `xf; yf`)
adicionam à lista três operações, como nos slides 24 e 26: translação de `(-xf, -yf)`,
a escala ou rotação, e translação de `(xf, yf)`.

## Conectar a Parte 2 quando estiver pronta

A classe da Parte 2 deve manter as assinaturas combinadas:

```java
public static Matriz3 translacao(double tx, double ty);
public static Matriz3 escala(double sx, double sy);
public static Matriz3 rotacao(double graus);
```

Depois que ela existir no pacote `cg`, substitua a criação do motor em
`JanelaPrincipal` pelo trecho abaixo. É uma instrução de integração futura;
não é necessário adicioná-la enquanto a classe estiver ausente.

```java
MotorTransformacoes motor = new MotorComposicao(op -> switch (op.tipo()) {
    case TRANSLACAO -> TransformacoesBasicas.translacao(op.a(), op.b());
    case ESCALA -> TransformacoesBasicas.escala(op.a(), op.b());
    case ROTACAO -> TransformacoesBasicas.rotacao(op.a());
    default -> throw new IllegalArgumentException("Operação não pertence à Parte 2.");
});
```

O seletor passa a incluir essas três operações automaticamente. O fornecedor
é chamado apenas para elas; reflexões e cisalhamentos continuam delegados à
Parte 3. A multiplicação permanece exclusivamente em `Matriz3` e `Composicao`.

## Verificação

Após compilar, execute:

```powershell
java -cp out cg.TesteTransformacoes
java '-Djava.awt.headless=true' -cp out cg.TesteRasterizador out/rasterizador.png
```

`TesteTransformacoes` verifica produto 3x3 geral, identidade, associatividade,
normalização homogênea, quatro reflexões e suas deduções, cisalhamentos,
ordem, casos de comutatividade, ponto fixo, precisão e preservação das entradas.
Também verifica o adaptador oficial e o fornecimento de matrizes por outro módulo.
As matrizes básicas usadas nos testes são dados de teste, sem criar a Parte 2.

`TesteRasterizador` continua verificando o exemplo da G3, oito octantes,
continuidade, extremos, fechamento e arredondamento, e salva a demonstração em PNG.

## Materiais de referência

Materiais fornecidos na pasta `Slides e materiais`, ao lado do repositório:

- `atividade_transformacoes_geometricas.pdf`: páginas 3–6, contratos e escopo
  das Partes 3 e 4; página 7, ordem de composição e precisão.
- `aulag5-2026.pptx`: slides 12–17, coordenadas homogêneas; 18–20, reflexões e
  cisalhamentos; 21, pré-multiplicação; 22–27, ponto fixo; 28, comutatividade.
- `aulag3_2026.pptx`: slides 26–28, algoritmo do ponto médio e exemplo numérico.
- `apostilausp.pdf`: capítulo 5, seções 5.2, 5.3 e 5.5, representação homogênea,
  reflexão, cisalhamento e composição. A referência adotada para ponto fixo é
  o produto `T(p) · S/R · T(-p)` mostrado em G5 e conferido numericamente.

# Relatório — Transformações de Coordenadas

Relatório em LaTeX do Lab-01 (Computação Gráfica), sobre as transformações
Mundo → NDC → Dispositivo.

## Estrutura

```
relatorio/
├── relatorio.tex               # arquivo principal (capa, sumário, \input das seções)
├── uepb_relatorio_config.sty   # capa/formatação padrão UEPB
├── paineis_tikz.sty            # macros dos diagramas (painel, mapafig, presets)
├── secoes/
│   ├── 01-introducao.tex
│   ├── 02-objetivos.tex
│   ├── 03-fundamentacao.tex
│   ├── 04-metodologia.tex
│   ├── 05-implementacao.tex
│   ├── 06-resultados.tex
│   ├── 07-conclusao.tex
│   └── 08-referencias.tex
└── imagens/
    ├── marca-uepb.png           # logomarca da UEPB usada na capa
    └── resultados/              # capturas de tela da seção de Resultados
```

Cada integrante pode editar sua seção dentro de `secoes/` sem mexer no
`relatorio.tex` principal, o que evita conflito de merge.

## Antes de compilar

A capa usa a logomarca em `imagens/marca-uepb.png`. Sem esse arquivo, a
compilação falha ao gerar a capa. Ele deve ser o PNG em si, e não um
*symlink* para um caminho local.
## Como compilar

Requer uma distribuição LaTeX com o pacote de idioma português do
`babel` (`texlive-lang-portuguese` no Linux, ou o TeX Live/MiKTeX
completos no Windows/macOS).

```bash
cd Lab-01/relatorio
latexmk -pdf relatorio.tex
```

Ou, sem `latexmk`, rode `pdflatex relatorio.tex` três vezes (a primeira
gera o sumário e as referências cruzadas, a segunda e a terceira as
resolvem).

## Editando seções

Basta editar o arquivo correspondente em `secoes/`. Não é preciso
mexer no `relatorio.tex`, a menos que uma seção nova seja criada — nesse
caso, adicione um novo `\input{secoes/nome-do-arquivo}` nele.

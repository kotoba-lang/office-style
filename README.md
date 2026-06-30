# office-style

[![CI](https://github.com/kotoba-lang/office-style/actions/workflows/ci.yml/badge.svg)](https://github.com/kotoba-lang/office-style/actions/workflows/ci.yml)

Pure CLJC / EDN StyleIR extraction for PowerPoint `.pptx` packages.

`office-style` reads deterministic PowerPoint style data directly from OOXML:
theme colors, theme fonts, slide size, slide parts, layouts, and masters. It can
also render a compact SVG preview and project the result into a svgraph-friendly
EDN shape.

## Runtime

- Implementation: Clojure / ClojureScript portable `.cljc`
- Host zip support: JVM Clojure for package reads
- Data surface: EDN maps under `:office-style/*`
- JavaScript / TypeScript core runtime: none. The npm package only provides a
  thin `node` bin wrapper that invokes the Clojure CLI.

## API

```clojure
(require '[office-style.opc :as opc]
         '[office-style.style :as style]
         '[office-style.preview :as preview]
         '[office-style.svgraph :as svgraph])

(def bytes (java.nio.file.Files/readAllBytes
            (java.nio.file.Path/of "deck.pptx" (into-array String []))))

(def ir (style/extract-bytes bytes))

(:office-style/colors ir)
(:office-style/fonts ir)
(preview/preview-svg ir)
(svgraph/presentation ir)
```

## Namespaces

- `office-style.opc`: pptx zip reader for XML and relationship parts
- `office-style.style`: deterministic StyleIR extraction
- `office-style.preview`: SVG preview from StyleIR
- `office-style.svgraph`: svgraph-friendly EDN projection

## CLI / npm

The CLI works directly with Clojure or through the npm wrapper. In both cases
`clojure` must be installed on the host.

```bash
clojure -M:cli extract deck.pptx style.edn
clojure -M:cli template deck.pptx template.edn
clojure -M:cli preview deck.pptx preview.svg
clojure -M:cli svgraph deck.pptx svgraph.edn

npx @kotoba-lang/office-style template deck.pptx template.edn
```

## Test

```bash
clojure -X:test
```

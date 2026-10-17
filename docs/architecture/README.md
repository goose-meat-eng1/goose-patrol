# Architecture diagrams

D1 to D5 are written in PlantUML. The rendered SVG and PNG files go in `out/`.

- **Java:** 17
- **Tool:** PlantUML 1.2026.8, the `plantuml-1.2026.8.jar` file from the [release page](https://github.com/plantuml/plantuml/releases/tag/v1.2026.8)
- **Docs:** [PlantUML command line](https://plantuml.com/command-line)

From the repo root, with `path/to/` replaced by the folder that holds the jar:

```
java -jar path/to/plantuml-1.2026.8.jar -tsvg -o out "docs/architecture/*.puml"
java -jar path/to/plantuml-1.2026.8.jar -tpng -o out "docs/architecture/*.puml"
```

# Bitácora

Registro fechado de pasos, versiones y resultados. Cada entrada responde:
qué hice, con qué versión, qué resultado obtuve y qué decidí.

## 2026-10-30 — Presentación de propuesta

- Presentación oral del tema 1.4. Dictamen de la comisión: favorable.
- Cambio de foco: el núcleo es la calidad de conversión y agrupación; la
  comparación económica queda condicionada a datos y escala comparable.
- Pendiente: matriz de factibilidad de agrupadores (MS-DRG, OpenDRG/CHS-DRG,
  europeos).

## 2026-11-01 — Instalación del agrupador MS-DRG

- Descargados desde CMS: `MSDRG.V440.R1.Java.Standalone.Jars.P.zip` y
  `MCE.V440.R0.Java.Standalone.Jars.P.zip`.
- Guías leídas: *MS-DRG Software Java API guide v44.0* (PBL-161) y
  *MCE Java API guide v440*. Ambas exigen Java 17 o superior.
- Java instalado: `java 23.0.1 2024-10-15` (no es versión LTS; funciona, pero
  evaluar migrar a 21 LTS).
- Dependencias faltantes en el ZIP: `gfc-base-api-3.4.9`, `protobuf-java-3.25.5`,
  `slf4j-api-1.7.36`.
- `gfc-base-api 3.4.9` no está en Maven Central: se compila desde el código
  fuente oficial (`solventum-oss/GFC-Grouper-Foundation-Classes`, commit
  `1b8df311f833f4ec8b1224c4047dacb23fcc5e8e`, `--release 8`).
- Hallazgo: el código de ejemplo de la guía MS-DRG no compila tal cual (la
  declaración de la lista de diagnósticos secundarios quedó dentro de un
  comentario). Corregido en `prueba/PruebaMsdrg.java`.

### Resultados de la prueba mínima

| Caso | Entrada | Código de retorno | MDC | MS-DRG | Observación |
| --- | --- | --- | --- | --- | --- |
| 1 | Ejemplo oficial CMS | _pendiente_ | | | |
| 2 | C50911 + 0HTT0ZZ (mama derecha, mastectomía) | _pendiente_ | | | |
| 3 | C509 (código OMS sin traducir) + 0HTT0ZZ | _pendiente_ | | | |

### Preguntas abiertas para el Taller

- ¿Dejar `gfc-base-api` compilado dentro del repositorio o mantener el script?
- ¿Integrar el agrupador desde Python (subproceso, JPype) o como servicio Java?

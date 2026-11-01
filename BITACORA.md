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
| 1 | Ejemplo oficial CMS | OK | 11 - Kidney and Urinary Tract | 19 - Simultaneous Pancreas, Islet Cell and Kidney Transplant with Hemodialysis | Verifica que la instalación funciona |
| 2 | C50911 + 0HTT0ZZ (mama derecha, mastectomía) | OK | 9 - Skin, Subcutaneous Tissue and Breast | 583 - Mastectomy for Malignancy without CC/MCC | Código ICD-10-CM completo: agrupa correctamente |
| 3 | C509 (código OMS sin traducir) + 0HTT0ZZ | INVALID_PDX | 0 - Pre-MDC | 999 - Ungroupable | El código OMS de 4 caracteres no es válido en ICD-10-CM; el agrupador rechaza el episodio |

**Conclusión de la prueba:** un mismo episodio pasa de no agrupable (999) a un
GRD válido (583) solo por traducir el diagnóstico principal. Sin conversión de
códigos, los egresos de FALP no pueden procesarse con MS-DRG.

### Discrepancias entre la guía de CMS y el software real

1. El código de ejemplo de la guía no compila: la declaración de la lista de
   diagnósticos secundarios quedó dentro de un comentario.
2. El método `getFinalMedSurgType()` que documenta la guía no existe en
   `MsdrgOutputData` (la guía también lo escribe como `getInitialMedSugType`).
3. La guía no explica cómo leer `MsdrgValue`; se verificó con `javap` que
   expone `getValue()` y `getDescription()`.

Lección: verificar la documentación contra el software con `javap` antes de
asumir nombres de métodos.

### Preguntas abiertas para el Taller

- ¿Dejar `gfc-base-api` compilado dentro del repositorio o mantener el script?
- ¿Integrar el agrupador desde Python (subproceso, JPype) o como servicio Java?

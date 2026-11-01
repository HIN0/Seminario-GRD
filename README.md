# Seminario GRD — Conversión de códigos clínicos y reagrupación de episodios oncológicos

Trabajo de titulación, Ingeniería de Ejecución en Computación e Informática, USACH (2-2026).

- **Estudiante:** Germán Peralta Huaiquiñir
- **Profesor guía:** Manuel Villalobos Cid (DIINF, USACH)
- **Co-guía:** Macarena Vera Rivero (FALP e Ingeniería Biomédica, USACH)

## Objetivo general (sugerido por la comisión)

Desarrollar una metodología reproducible de transformación de códigos clínicos y
reagrupación de episodios oncológicos de FALP mediante agrupadores GRD
internacionales accesibles, con validación de correspondencia clínica, cobertura
y diferencias frente al sistema chileno.

## Estructura

| Carpeta | Contenido |
| --- | --- |
| `prueba/` | Prueba mínima del agrupador MS-DRG v44 con casos sintéticos |
| `scripts/` | Scripts para obtener dependencias de forma reproducible |
| `lib/` | Archivos `.jar` (no se suben; ver más abajo) |
| `datos/` | Datos locales (nunca se suben) |
| `BITACORA.md` | Registro fechado de decisiones, versiones y resultados |

## Versiones fijadas

| Componente | Versión | Origen |
| --- | --- | --- |
| MS-DRG (agrupador) | V440.R1 (v44, FY 2027) | CMS, ZIP `MSDRG.V440.R1.Java.Standalone.Jars.P.zip` |
| MCE (editor de códigos) | V440.R0 | CMS, ZIP `MCE.V440.R0.Java.Standalone.Jars.P.zip` |
| msdrg-core | 44.0.0.3 | Incluido en el ZIP de CMS |
| msdrg-model-v2 | 2.12.2 | Incluido en el ZIP de CMS |
| msdrg-binary-access | 1.6.4 | Incluido en el ZIP de CMS |
| gfc-base-api | 3.4.9 | Compilado desde `solventum-oss/GFC-Grouper-Foundation-Classes`, commit `1b8df31` |
| protobuf-java | 3.25.5 | Maven Central |
| slf4j-api | 1.7.36 | Maven Central |
| Java | 17 o superior (probado con 23.0.1) | — |

## Cómo ejecutar la prueba (Windows, PowerShell)

1. Descargar desde la [página MS-DRG de CMS](https://www.cms.gov/Medicare/Medicare-Fee-for-Service-Payment/AcuteInpatientPPS/MS-DRG-Classifications-and-Software)
   el ZIP de Java de la versión 44 y copiar a `lib/` estos tres archivos:
   `msdrg-core-44.0.0.3.jar`, `msdrg-model-v2-2.12.2.jar`, `msdrg-binary-access-1.6.4.jar`.
2. Obtener el resto de las dependencias (requiere `git` y un JDK):

   ```powershell
   .\scripts\obtener_dependencias.ps1
   ```

3. Compilar y ejecutar desde la raíz del repositorio:

   ```powershell
   javac -cp "lib/*" -d build prueba/PruebaMsdrg.java
   java -cp "lib/*;build" PruebaMsdrg
   ```

   (En Mac o Linux, reemplazar `;` por `:`.)

Un aviso de SLF4J sobre `StaticLoggerBinder` es normal y no afecta la agrupación.

## Privacidad

Este repositorio no contiene ni debe contener datos de pacientes. Ver `datos/README.md`.

# Obtiene las dependencias del agrupador MS-DRG v44 de forma reproducible.
# Uso (desde la raiz del repositorio):  .\scripts\obtener_dependencias.ps1
# Requiere: git y un JDK (javac y jar en el PATH).

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$lib = Join-Path $root "lib"
New-Item -ItemType Directory -Force -Path $lib | Out-Null

# 1. Dependencias publicadas en Maven Central
$maven = [ordered]@{
    "protobuf-java-3.25.5.jar" = "https://repo1.maven.org/maven2/com/google/protobuf/protobuf-java/3.25.5/protobuf-java-3.25.5.jar"
    "slf4j-api-1.7.36.jar"     = "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar"
}
foreach ($name in $maven.Keys) {
    $dest = Join-Path $lib $name
    if (Test-Path $dest) {
        Write-Host "OK (ya existe): $name"
    } else {
        Write-Host "Descargando $name ..."
        Invoke-WebRequest -Uri $maven[$name] -OutFile $dest
    }
}

# 2. gfc-base-api 3.4.9: no esta en Maven Central, se compila desde el codigo fuente oficial
$gfcJar = Join-Path $lib "gfc-base-api-3.4.9.jar"
$gfcCommit = "1b8df311f833f4ec8b1224c4047dacb23fcc5e8e"
if (Test-Path $gfcJar) {
    Write-Host "OK (ya existe): gfc-base-api-3.4.9.jar"
} else {
    $tmp = Join-Path $env:TEMP "gfc-src"
    if (Test-Path $tmp) { Remove-Item -Recurse -Force $tmp }
    Write-Host "Clonando GFC-Grouper-Foundation-Classes ..."
    git clone --quiet https://github.com/solventum-oss/GFC-Grouper-Foundation-Classes $tmp
    git -C $tmp checkout --quiet $gfcCommit
    $build = Join-Path $tmp "build"
    $sources = Get-ChildItem -Recurse -Filter *.java -Path (Join-Path $tmp "src\main\java") |
        ForEach-Object { $_.FullName }
    Write-Host "Compilando gfc-base-api 3.4.9 ..."
    javac --release 8 -nowarn -d $build $sources
    jar cf $gfcJar -C $build .
    Write-Host "OK: gfc-base-api-3.4.9.jar"
}

# 3. Verificar los jar de CMS (se copian a mano desde el ZIP oficial)
$cms = @("msdrg-core-44.0.0.3.jar", "msdrg-model-v2-2.12.2.jar", "msdrg-binary-access-1.6.4.jar")
$faltan = 0
foreach ($j in $cms) {
    if (-not (Test-Path (Join-Path $lib $j))) {
        Write-Warning "Falta ${j}: copialo desde el ZIP MSDRG.V440.R1 de CMS a la carpeta lib"
        $faltan++
    }
}
if ($faltan -eq 0) { Write-Host "Listo: las 6 dependencias estan en lib/" }

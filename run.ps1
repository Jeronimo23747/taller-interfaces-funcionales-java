# Script PowerShell para compilar y ejecutar el Taller de Interfaces Funcionales
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$javaBin = $env:JAVA_HOME ? "$($env:JAVA_HOME)\bin" : ""
if (-not $javaBin -or -not (Test-Path "$javaBin\javac.exe")) {
    $fallbackJdk = "$env:USERPROFILE\.jdks\openjdk-26.0.2\bin"
    if (Test-Path "$fallbackJdk\javac.exe") {
        $javaBin = $fallbackJdk
    }
}

$javac = $javaBin ? "$javaBin\javac.exe" : "javac"
$java = $javaBin ? "$javaBin\java.exe" : "java"

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

Write-Host ">>> Compilando clases Java con $javac..." -ForegroundColor Cyan
$fuentes = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
& $javac -d bin -encoding UTF-8 $fuentes

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Falló la compilación." -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[OK] Compilación exitosa.`n" -ForegroundColor Green

if ($args.Count -gt 0 -and $args[0] -eq "test") {
    & $java -cp bin co.edu.unihumboldt.funcional.PruebasUnitariasRunner
} else {
    & $java -cp bin co.edu.unihumboldt.funcional.Main $args
}

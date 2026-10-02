@echo off
chcp 65001 > nul
echo ======================================================================
echo   EJECUTANDO SUITE DE PRUEBAS UNITARIAS
echo ======================================================================

set "JAVA_BIN=%JAVA_HOME%\bin"
if not exist "%JAVA_BIN%\javac.exe" (
    if exist "%USERPROFILE%\.jdks\openjdk-26.0.2\bin\javac.exe" (
        set "JAVA_BIN=%USERPROFILE%\.jdks\openjdk-26.0.2\bin"
    ) else (
        set "JAVA_BIN="
    )
)

if "%JAVA_BIN%"=="" (
    set JAVAC_CMD=javac
    set JAVA_CMD=java
) else (
    set "JAVAC_CMD=%JAVA_BIN%\javac.exe"
    set "JAVA_CMD=%JAVA_BIN%\java.exe"
)

if not exist bin mkdir bin

dir /s /b src\*.java > fuentes.txt
"%JAVAC_CMD%" -d bin -encoding UTF-8 @fuentes.txt
del fuentes.txt 2>nul

"%JAVA_CMD%" -cp bin co.edu.unihumboldt.funcional.PruebasUnitariasRunner
pause

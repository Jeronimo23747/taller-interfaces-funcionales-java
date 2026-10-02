@echo off
chcp 65001 > nul
echo ======================================================================
echo   COMPILANDO Y EJECUTANDO EL TALLER DE INTERFACES FUNCIONALES
echo   Estudiante: Jeronimo Hoyos - UniHumboldt
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

echo Compilando codigo fuente Java...
dir /s /b src\*.java > fuentes.txt
"%JAVAC_CMD%" -d bin -encoding UTF-8 @fuentes.txt
if errorlevel 1 (
    echo [ERROR] La compilacion ha fallado.
    del fuentes.txt 2>nul
    pause
    exit /b 1
)
del fuentes.txt 2>nul
echo [OK] Compilacion exitosa.
echo.
echo Iniciando aplicacion principal...
"%JAVA_CMD%" -cp bin co.edu.unihumboldt.funcional.Main %*
pause

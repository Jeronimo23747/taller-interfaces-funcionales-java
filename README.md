# Taller: Interfaces Funcionales en Java (Java 21 LTS)

**Estudiante:** Jerónimo Hoyos  
**Institución:** Corporación Universitaria Alexander von Humboldt (UniHumboldt)  
**Correo Institucional:** `jhoyos_1280@unihumboldt.edu.co`  
**Tecnología Base:** Java 21 LTS / OpenJDK 26  
**Paradigma:** Programación Funcional en Java moderno  

---

## 📌 1. Descripción del Proyecto

Este repositorio contiene la solución completa, rigurosa y ordenada de la guía técnica **"Interfaces funcionales en Java: El contrato de un solo método que convirtió al comportamiento en un dato"**.

El proyecto aborda desde los fundamentos conceptuales de las interfaces SAM (*Single Abstract Method*), expresiones lambda, referencias a métodos y closures, hasta la composición avanzada con `java.util.function`, especializaciones primitivas para evitar sobrecostos por autoboxing, manejo profesional de excepciones verificadas, patrones de diseño clásicos expresados funcionalmente, y los cuatro retos para profundizar.

---

## 📂 2. Estructura del Proyecto

El código está estructurado bajo el estándar de paquetes de Java y diseño limpio:

```text
taller-interfaces-funcionales-java/
├── pom.xml                               # Configuración de compilación Maven
├── README.md                             # Documentación técnica y académica completa
├── compilar_y_ejecutar.bat               # Script Windows batch para compilar y lanzar la aplicación
├── ejecutar_pruebas.bat                  # Script Windows batch para correr la suite de pruebas
├── run.ps1                               # Script PowerShell multiplataforma
├── .gitignore                            # Exclusión de binarios y archivos temporales
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── co/edu/unihumboldt/funcional/
│   │           │
│   │           ├── Main.java             # Menú interactivo y ejecutor maestro del taller
│   │           │
│   │           ├── modelo/               # Modelos de datos compartidos
│   │           │   ├── Producto.java     # Record del producto del catálogo
│   │           │   └── Catalogo.java     # Catálogo con dataset de muestra
│   │           │
│   │           ├── lab1_slugs/           # Laboratorio 1: Generador de Slugs
│   │           │   ├── GeneradorSlug.java
│   │           │   └── Laboratorio1Main.java
│   │           │
│   │           ├── lab2_validacion/      # Laboratorio 2: Motor de Validación Componible
│   │           │   ├── Regla.java
│   │           │   ├── Registro.java
│   │           │   └── Laboratorio2Main.java
│   │           │
│   │           ├── lab3_calculadora/     # Laboratorio 3: Calculadora con Estrategias
│   │           │   ├── CalculadoraEstrategias.java
│   │           │   └── Laboratorio3Main.java
│   │           │
│   │           ├── lab4_promociones/     # Laboratorio 4: Proyecto Integrador Promociones
│   │           │   ├── Promocion.java
│   │           │   ├── AjustePrecio.java
│   │           │   └── Laboratorio4Main.java
│   │           │
│   │           ├── retos/                # Retos para profundizar (§33 de la guía)
│   │           │   ├── TriFunction.java  # Reto 1: Currificación y orden superior
│   │           │   ├── MemoizadorConcurrente.java # Reto 3: Caché segura y análisis recursivo
│   │           │   ├── Reintentar.java   # Reto 4: Retry pattern funcional
│   │           │   └── RetosMain.java    # Demostración integrada de los retos
│   │           │
│   │           ├── ejemplos/             # Implementación de ejemplos teóricos
│   │           │   ├── FundamentosEjemplo.java           # Listados 2 a 7
│   │           │   ├── BibliotecaEstandarEjemplo.java    # Listados 8 a 13
│   │           │   ├── ExcepcionesVerificadasEjemplo.java # Listado 15
│   │           │   └── PatronesDisenoEjemplo.java        # Listados 14, 16 a 20
│   │           │
│   │           └── autoevaluacion/       # Módulo de autoevaluación oficial
│   │               └── Autoevaluacion.java # 10 preguntas argumentadas
│   │
│   └── test/
│       └── java/
│           └── co/edu/unihumboldt/funcional/
│               └── PruebasUnitariasRunner.java # Suite de pruebas automatizadas (24 pruebas)
```

---

## 🔬 3. Desarrollo de los Laboratorios

### 🏷️ Laboratorio 1 · Generador de slugs por composición
* **Objetivo:** Construir una transformación de texto compleja componiendo operadores unarios (`UnaryOperator<String>`).
* **Pasos declarados:**
  1. `RECORTAR`: Referencia a método `String::strip`.
  2. `MINUSCULAS`: Referencia a método `String::toLowerCase`.
  3. `SIN_TILDES`: Descomposición canónica `Normalizer.Form.NFD` eliminando marcas diacríticas con `\p{M}`.
  4. `SOLO_VALIDOS`: Expresión regular `[^a-z0-9\s-]` para descartar caracteres especiales.
  5. `GUIONES`: Compactación de secuencias de espacios y guiones con `[\s-]+` por `-`.
* **Composición:** Implementada en `tuberia(List<? extends Function<String, String>> pasos)` partiendo del elemento neutro `Function.identity()` y encadenando con `andThen`.
* **Análisis de Reordenamiento (Punto 4):**
  > Al invertir el orden y ejecutar `SOLO_VALIDOS` antes de `SIN_TILDES`, las letras acentuadas (ej. `ó`, `á`) no coinciden con `[a-z0-9\s-]` y son eliminadas por completo en lugar de normalizarse a sus caracteres base (`o`, `a`). La composición no es conmutativa ($f \circ g \neq g \circ f$), demostrando la criticidad del orden en la tubería funcional.

### 🛡️ Laboratorio 2 · Motor de validación componible
* **Objetivo:** Diseñar una interfaz funcional genérica `@FunctionalInterface Regla<T>` con `List<String> validar(T valor)`.
* **Composición y Fábricas:**
  - `y(Regla<? super T> otra)`: Conjunción lógica acumulativa que combina listas de errores sin perder información.
  - `exigir(Predicate<? super T> condicion, String mensaje)`: Fábrica básica que evalúa una condición pura.
  - `campo(Function<? super T, ? extends C> extractor, Regla<? super C> regla)`: Proyección de reglas sobre atributos de objetos compuestos usando la regla PECS (*Producer Extends, Consumer Super*).
* **Evaluación de Casos de Prueba:**
  - `Registro("camila_r", "camila@correo.co", 19)` $\rightarrow$ **VÁLIDO** `[]`
  - `Registro("", "sin-arroba", 12)` $\rightarrow$ **INVÁLIDO** `[usuario: obligatorio, usuario: mínimo 4 caracteres, correo: formato inválido, edad: mínimo 14 años]`
  - `Registro("ana", "ana@correo", 15)` $\rightarrow$ **INVÁLIDO** `[usuario: mínimo 4 caracteres, correo: formato inválido]`

### 🧮 Laboratorio 3 · Calculadora con estrategias
* **Objetivo:** Reemplazar estructuras rígidas como `switch` o `if-else` por un catálogo extensible de funciones `DoubleBinaryOperator`.
* **Operaciones registradas:** `+` (`Double::sum`), `-`, `*`, `/` (con protección aritmética ante división por cero), `^` (`Math::pow`) y `max` (`Math::max`).
* **Reflexión sobre el Principio Abierto/Cerrado (OCP - Punto 5):**
  > Con el mapa de estrategias, incorporar una nueva operación como `%` se reduce a:
  > `CalculadoraEstrategias.registrarOperacion("%", (a, b) -> a % b);`
  > El método `evaluar` permanece **cerrado a modificación y abierto a extensión**, evitando recompilar o alterar el núcleo del sistema. En contraste, un enfoque con `switch` exige abrir y modificar el método original, incrementando la fragilidad del software.

### 🎁 Laboratorio 4 · Proyecto integrador: Motor de promociones
* **Objetivo:** Modelar reglas de negocio desacopladas mediante el record:
  `Promocion(String nombre, Predicate<Producto> aplicaA, DoubleUnaryOperator ajuste)`.
* **Fábricas de Ajustes:** `AjustePrecio.porcentaje(pct)` y `AjustePrecio.montoFijo(monto)` (garantizando un piso de cero con `Math.max(0, precio - monto)`).
* **Selección Óptima:**
  `BinaryOperator<Promocion> masConveniente = BinaryOperator.minBy(Comparator.comparingDouble(p -> p.precioCon(producto)));`
* **Salida del Catálogo:**
  ```text
  Portátil          Tecno 10 %     $  3.200.000 → $  2.880.000
  Mouse             Tecno 10 %     $     85.000 → $     76.500
  Audífonos         Bono $50.000   $    240.000 → $    190.000
  Lápiz             Liquidación    $      2.500 → $      1.750
  Silla ergonómica  Bono $50.000   $    890.000 → $    840.000
  Ahorro total para el cliente: $429.250
  ```
  *(Nota: El producto "Cuaderno" tiene stock 0 y se omite automáticamente por disponibilidad).*

---

## 🚀 4. Retos para Profundizar (Página 33)

1. **Reto 1: Currificación con `TriFunction.curry()`**
   - Transforma $(A, B, C) \rightarrow R$ en $A \rightarrow (B \rightarrow (C \rightarrow R))$, permitiendo la aplicación parcial de argumentos en diferentes momentos del ciclo de vida de la aplicación. Se incluyó además `descurry()`.
2. **Reto 2: Métodos `.o()` y `.cuando()` en `Regla<T>`**
   - `.o(Regla)`: Disyunción funcional (pasa si al menos una regla es válida; acumula ambos errores si ambas fallan).
   - `.cuando(Predicate)`: Ejecución condicional (solo evalúa la regla si la condición previa es verdadera).
3. **Reto 3: Memoización concurrente con `ConcurrentHashMap` y análisis de recursión**
   - **Análisis Teórico:** `ConcurrentHashMap.computeIfAbsent` bloquea la cubeta/nodo (*bin lock*) de la clave en cálculo. Si la función computada es recursiva e intenta calcular la misma clave o una clave que colisiona en el mismo bin hash, se genera un **DEADLOCK** o lanza `IllegalStateException: Recursive update`. Para funciones recursivas se debe separar la lectura con `get()` del cálculo e inserción.
4. **Reto 4: Patrón de reintentos funcional `Reintentar.de(Supplier<T>, int intentos)`**
   - Envuelve la ejecución de proveedores propensos a fallos intermitentes, reintentando hasta $N$ veces y preservando la causa del último error.

---

## 📝 5. Autoevaluación Oficial (10 Preguntas y Justificaciones)

| # | Pregunta | Rta | Sección | Justificación Técnica |
|---|---|:---:|:---:|---|
| **1** | ¿Cuál de estas es una interfaz funcional? | **b** | §2 | Métodos públicos de `Object` (como `equals`) no cuentan para el SAM. `interface B` tiene un único método abstracto propio (`void x()`). |
| **2** | ¿Qué ocurre con `var f = x -> x + 1;`? | **b** | §3 | Las lambdas no poseen tipo intrínseco; necesitan un *target type*. Con `var` se produce un conflicto de inferencia mutua. |
| **3** | Dentro del cuerpo de una lambda, `this` se refiere a… | **b** | §3 / §5 | Ámbito léxico: La lambda no crea un ámbito de objeto propio; `this` referencia a la instancia de la clase que la contiene. |
| **4** | `String::length` como `Function<String, Integer>` es… | **c** | §4 | Referencia a método de instancia de un objeto arbitrario (*unbound*): el receptor pasa como primer argumento. |
| **5** | ¿Qué calcula `f.andThen(g).apply(x)`? | **b** | §7 | Orden secuencial: ejecuta primero $f$ y luego $g$ sobre el resultado: $g(f(x))$. |
| **6** | ¿Qué interfaz convierte un `Producto` en `double` sin autoboxing? | **b** | §8 | `ToDoubleFunction<Producto>` define `applyAsDouble(T)` retornando el primitivo directo. |
| **7** | Una lambda hace `contador++` sobre una variable local. ¿Qué pasa? | **b** | §5 | Error de compilación: Las variables locales capturadas deben ser `final` o efectivamente finales. |
| **8** | `Function<String, URI> f = s -> new URI(s);` | **b** | §10 | Error: `new URI(s)` declara `URISyntaxException` (checked) y `Function` no declara `throws`. |
| **9** | ¿Para qué es especialmente útil `Supplier<T>`? | **a** | §11 | Evaluación diferida (*lazy evaluation*): evita cómputos pesados a menos que sean estrictamente necesarios. |
| **10**| ¿Qué pasa con `ejecutar(() -> "x")` ante `Supplier` y `Callable`? | **c** | §12 | Error del compilador por llamada ambigua entre dos interfaces funcionales con el mismo descriptor `() -> String`. |

**Calificación obtenida:** 10 de 10 aciertos (100% de dominio).

---

## 💻 6. Cómo Compilar y Ejecutar

### Opción A: Usando los scripts directos (Recomendado)
- **Ejecutar todo el taller con menú interactivo:**
  ```cmd
  compilar_y_ejecutar.bat
  ```
- **Ejecutar la suite de pruebas unitarias automáticas:**
  ```cmd
  ejecutar_pruebas.bat
  ```
- **Desde PowerShell:**
  ```powershell
  .\run.ps1        # Abre el menú interactivo
  .\run.ps1 all    # Ejecuta todo el taller de una vez
  .\run.ps1 test   # Ejecuta las pruebas unitarias
  ```

### Opción B: Usando Maven (si está disponible)
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="co.edu.unihumboldt.funcional.Main"
```

### Opción C: Compilación manual con `javac` y `java`
```bash
# Compilar todas las clases
javac -d bin -encoding UTF-8 (Get-ChildItem -Recurse -Filter *.java src | % FullName)

# Ejecutar el menú principal
java -cp bin co.edu.unihumboldt.funcional.Main

# Ejecutar directamente las pruebas
java -cp bin co.edu.unihumboldt.funcional.PruebasUnitariasRunner
```

---

## 👨‍💻 Créditos
- **Autor:** Jerónimo Hoyos
- **Universidad:** Alexander von Humboldt
- **Año:** 2026

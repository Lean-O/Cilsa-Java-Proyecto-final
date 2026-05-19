# Trabajo Práctico Final – JAVA 2026
## Sistema de Búsqueda de Visitas a Parques Nacionales de Argentina

---

## Índice

1. [Descripción del proyecto](#descripción-del-proyecto)
2. [Dataset utilizado](#dataset-utilizado)
3. [Estructura del proyecto](#estructura-del-proyecto)
4. [Cómo ejecutar el programa](#cómo-ejecutar-el-programa)
5. [Explicación de cada archivo](#explicación-de-cada-archivo)
   - [VisitaParque.java](#visitaparquejava)
   - [Main.java](#mainjava)
6. [Flujo completo del programa](#flujo-completo-del-programa)
7. [Requisitos del TP y cómo se cumplen](#requisitos-del-tp-y-cómo-se-cumplen)
8. [Conceptos de Java aplicados](#conceptos-de-java-aplicados)
9. [Ejemplo de ejecución real](#ejemplo-de-ejecución-real)

---

## Descripción del proyecto

Programa en Java que **lee un archivo CSV real**, permite **buscar registros** por dos criterios distintos, **valida** todas las entradas del usuario y **guarda los resultados** en un nuevo archivo CSV con nombre descriptivo.

El programa usa datos reales del **Ministerio de Turismo y Deportes de Argentina**, lo que le da un contexto auténtico y relevante al trabajo.

---

## Dataset utilizado

| Campo | Detalle |
|---|---|
| **Archivo** | `visitas-residentes-y-no-residentes-por-region.csv` |
| **Fuente** | Ministerio de Turismo y Deportes – SINTA (Sistema de Información Turística de la Argentina) |
| **Registros** | 3.960 filas |
| **Período** | Enero 2008 – Abril 2026 |
| **Separador** | Coma (`,`) |

### Columnas del CSV

| Columna | Nombre | Descripción | Ejemplo |
|---|---|---|---|
| 0 | `indice_tiempo` | Fecha del registro | `2023-1-01` |
| 1 | `region_de_destino` | Región turística | `patagonia` |
| 2 | `origen_visitantes` | Tipo de visitante | `residentes` |
| 3 | `visitas` | Cantidad de visitas | `230087` |
| 4 | `observaciones` | Nota aclaratoria | _(vacío)_ |

### Regiones disponibles
`buenos aires` | `cordoba` | `cuyo` | `litoral` | `norte` | `patagonia`

### Orígenes disponibles
`residentes` | `no residentes` | `total`

---

## Estructura del proyecto

```
ProyectoFinal/                        ← raíz del proyecto NetBeans
│
├── visitas-residentes-y-no-...csv    ← archivo CSV fuente (DEBE estar aquí)
├── build.xml                         ← archivo de compilación de NetBeans
├── README.md                         ← este archivo
│
└── src/
    └── proyecto/
        └── pkgfinal/
            ├── VisitaParque.java     ← clase modelo (representa una fila del CSV)
            └── Main.java             ← clase principal (menú, búsqueda, guardado)
```

> **Importante:** el archivo CSV debe colocarse en la **raíz del proyecto**
> (la carpeta que contiene `build.xml`), no dentro de `src/`.
> Esto es porque NetBeans ejecuta los programas con el directorio raíz como directorio de trabajo.

---

## Cómo ejecutar el programa

### En NetBeans
1. Abrir el proyecto `ProyectoFinal`
2. Verificar que el CSV esté en la raíz del proyecto
3. Clic derecho sobre `Main.java` → **Run File** (o `Shift + F6`)

### Desde terminal
```bash
# Compilar (desde la raíz del proyecto)
javac -d build/classes src/proyecto/pkgfinal/VisitaParque.java src/proyecto/pkgfinal/Main.java

# Ejecutar
java -cp build/classes proyecto.pkgfinal.Main
```

---

## Explicación de cada archivo

---

### VisitaParque.java

Esta clase representa **un único registro del CSV**, es decir, una fila con sus cinco columnas. Es el modelo de datos del programa.

#### Declaración del paquete
```java
package proyecto.pkgfinal;
```
Indica que la clase pertenece al paquete `proyecto.pkgfinal`. En NetBeans, los paquetes organizan las clases en carpetas. Sin esta línea, el IDE no puede compilar correctamente.

#### Atributos privados
```java
private String fecha;
private String region;
private String origen;
private String visitas;
private String observaciones;
```
Todos los atributos son `private` aplicando el principio de **encapsulamiento**: ninguna clase externa puede leerlos o modificarlos directamente. Se usa `String` para todos, incluso para `visitas` (que es un número), porque el CSV trae todo como texto y la conversión a número se hace solo cuando es necesario (al calcular la suma).

#### Constructor
```java
public VisitaParque(String fecha, String region, String origen,
                    String visitas, String observaciones) { ... }
```
Recibe los 5 valores de una fila ya separados y los asigna a los atributos con `this.campo = valor`. Es invocado en `Main.cargarDatos()` por cada línea leída del archivo.

#### Getters
```java
public String getFecha()  { return fecha;  }
public String getRegion() { return region; }
// ... etc
```
Son los únicos puntos de acceso a los datos privados. No hay setters porque una vez cargado un registro, sus datos no se modifican.

#### Método toCSV()
```java
public String toCSV() {
    return fecha + "," + region + "," + origen + "," + visitas + "," + observaciones;
}
```
Reconstruye la línea en formato CSV para poder guardar el objeto en un archivo de salida. Es llamado en `guardarResultados()` por cada resultado encontrado.

#### Método toString()
```java
@Override
public String toString() {
    return String.format("  %-12s | %-13s | %-14s | %10s | %s", ...);
}
```
`@Override` indica que este método reemplaza al `toString()` heredado de la clase `Object`.
`String.format()` aplica formato de columnas fijas:
- `%-12s` → texto alineado a la izquierda en 12 caracteres
- `%10s` → texto alineado a la derecha en 10 caracteres

Cuando se ejecuta `System.out.println(vp)`, Java llama automáticamente a este método.

---

### Main.java

Es la clase principal. Contiene el método `main()` y todos los métodos de lógica del programa organizados de forma estática.

#### Importaciones
```java
import java.io.*;         // Para leer y escribir archivos
import java.util.ArrayList;  // Lista dinámica de objetos
import java.util.Scanner;    // Lectura desde teclado
```

#### Constante del archivo
```java
private static final String ARCHIVO_CSV =
    "visitas-residentes-y-no-residentes-por-region.csv";
```
- `static` → pertenece a la clase, no a una instancia
- `final` → su valor no puede cambiar (es una constante)
- Al estar en la raíz del proyecto, NetBeans lo encuentra sin especificar una ruta completa

---

#### Método main()

```java
public static void main(String[] args) { ... }
```

Coordina todo el programa en cuatro pasos:

**Paso 1 – Cargar datos:**
```java
ArrayList<VisitaParque> visitas = cargarDatos(ARCHIVO_CSV);
if (visitas == null) return;
```
Llama a `cargarDatos()` y si devuelve `null` (error de archivo) termina el programa.

**Paso 2 – Mostrar encabezado:**
Imprime el título del sistema y los valores de búsqueda disponibles para orientar al usuario.

**Paso 3 – Crear Scanner:**
```java
Scanner scanner = new Scanner(System.in);
```
`System.in` es el flujo de entrada estándar (el teclado). El Scanner lo convierte en texto legible.

**Paso 4 – Menú con do-while:**
```java
do {
    // mostrar opciones
    opcion = leerOpcionValida(scanner, 1, 3);
    switch (opcion) { ... }
} while (opcion != 3);
```
El `do-while` garantiza que el menú se muestre al menos una vez. El `switch` dirige la ejecución al método correspondiente. El bucle continúa hasta que el usuario elige la opción 3.

---

#### Método cargarDatos()

```java
private static ArrayList<VisitaParque> cargarDatos(String ruta)
```

Lee el archivo CSV usando tres clases encadenadas:
```java
new BufferedReader(
    new InputStreamReader(
        new FileInputStream(ruta), "UTF-8"))
```

- `FileInputStream` abre el archivo en modo binario
- `InputStreamReader` interpreta los bytes como caracteres UTF-8 (necesario para acentos)
- `BufferedReader` agrupa la lectura en bloques para mayor eficiencia y permite `readLine()`

El bloque `try-with-resources` cierra automáticamente el `BufferedReader` al terminar, sin necesidad de llamar a `br.close()` manualmente.

**Limpieza del BOM:**
```java
linea = linea.replace("\uFEFF", "");
```
El BOM (_Byte Order Mark_) es un carácter invisible que algunos programas (como Excel) agregan al inicio de archivos UTF-8. Si no se elimina, la primera columna de la cabecera queda como `﻿Localidad` en lugar de `Localidad`.

**Split con -1:**
```java
String[] partes = linea.split(",", -1);
```
El `-1` como segundo parámetro indica que no se ignoren los campos vacíos al final de la línea. Sin él, `split()` descartaría los últimos campos si están vacíos.

**Manejo de excepciones:**
```java
catch (FileNotFoundException ex) { ... return null; }
catch (IOException ex)           { ... return null; }
```
`FileNotFoundException` ocurre cuando el archivo no existe. `IOException` cubre errores más generales de entrada/salida (disco lleno, sin permisos, etc.). Ambas son capturadas por separado para dar mensajes específicos.

---

#### Método realizarBusqueda()

```java
private static void realizarBusqueda(Scanner scanner,
                                      ArrayList<VisitaParque> visitas,
                                      String criterio)
```

**Filtrado:**
```java
for (VisitaParque vp : visitas) {
    String campo = criterio.equals("region") ? vp.getRegion() : vp.getOrigen();
    if (campo.toLowerCase().contains(clave.toLowerCase())) {
        resultados.add(vp);
    }
}
```
El operador ternario `? :` elige qué campo comparar según el criterio.
`toLowerCase()` en ambos lados hace la búsqueda **insensible a mayúsculas**: buscar `"Patagonia"` o `"PATAGONIA"` da el mismo resultado que `"patagonia"`.
`contains()` permite búsqueda parcial: buscar `"pat"` también encuentra `"patagonia"`.

**Límite de 50 filas en pantalla:**
```java
if (mostrados == 50 && resultados.size() > 50) {
    System.out.println("  ... (mostrando 50 de " + resultados.size() + ")");
    break;
}
```
Patagonia tiene 660 registros. Mostrarlos todos satura la consola, por eso se limita la visualización pero se guardan todos en el archivo.

**Suma de visitas:**
```java
if (!vp.getOrigen().equalsIgnoreCase("total")) {
    suma += Long.parseLong(vp.getVisitas());
}
```
Se excluyen las filas de origen `"total"` porque ya son la suma de residentes + no residentes. Incluirlas duplicaría el conteo. Se usa `Long` (en lugar de `int`) porque la suma de millones de visitas supera el límite de 2.147.483.647 de un `int`.

---

#### Método guardarResultados()

```java
private static void guardarResultados(ArrayList<VisitaParque> resultados,
                                       String criterio, String clave)
```

**Nombre del archivo:**
```java
String nombreArchivo = "resultados_" + criterio + "_"
                       + clave.replaceAll("\\s+", "_") + ".csv";
```
`replaceAll("\\s+", "_")` reemplaza uno o más espacios consecutivos por guión bajo. Así `"no residentes"` genera `resultados_origen_no_residentes.csv`.

La escritura usa la misma cadena de clases que la lectura pero en dirección contraria:
- `FileOutputStream` crea el archivo de salida
- `OutputStreamWriter` con UTF-8 garantiza que los acentos se escriban correctamente
- `BufferedWriter` escribe de forma eficiente

---

#### Métodos de validación

**leerOpcionValida():**
```java
private static int leerOpcionValida(Scanner scanner, int min, int max)
```
Bucle `while(true)` que solo termina con `return` cuando la entrada es válida.
`Integer.parseInt()` lanza `NumberFormatException` si el texto no es un número entero; el `catch` lo captura y pide volver a ingresar.

**leerPalabraClave():**
```java
private static String leerPalabraClave(Scanner scanner, String etiqueta)
```
`trim()` elimina espacios al inicio y al final. `isEmpty()` detecta si después del `trim()` no quedó nada. El bucle `while` repite hasta obtener texto real.

---

## Flujo completo del programa

```
INICIO
  │
  ▼
cargarDatos()
  ├─ Abre el CSV con BufferedReader + UTF-8
  ├─ Salta la cabecera
  ├─ Por cada línea: crea un VisitaParque y lo agrega al ArrayList
  ├─ FileNotFoundException → mensaje de error → termina
  └─ devuelve ArrayList (3960 objetos)
  │
  ▼
Muestra encabezado y valores disponibles
  │
  ▼
┌─ MENÚ (do-while) ──────────────────────────┐
│                                             │
│  leerOpcionValida() → valida 1, 2 o 3      │
│                                             │
│  opción 1 → realizarBusqueda("region")     │
│  opción 2 → realizarBusqueda("origen")     │
│  opción 3 → salir del bucle                │
│                                             │
└─────────────────────────────────────────────┘
  │
  ▼ (dentro de realizarBusqueda)
leerPalabraClave() → valida que no esté vacío
  │
  ▼
Filtrar ArrayList:
  campo.toLowerCase().contains(clave.toLowerCase())
  │
  ▼
Mostrar resultados en tabla (máx. 50 filas)
  │
  ▼
Calcular suma de visitas (excluye "total")
  │
  ▼
guardarResultados()
  ├─ Nombre: resultados_criterio_clave.csv
  ├─ Escribe cabecera CSV
  ├─ Escribe cada registro con toCSV()
  └─ IOException → mensaje de error
  │
  ▼
Volver al MENÚ (si opción != 3)
  │
  ▼
FIN → scanner.close()
```

---

## Requisitos del TP y cómo se cumplen

| # | Requisito | Implementación |
|---|---|---|
| **1** | Leer archivo CSV | `BufferedReader` + `FileInputStream` con encoding UTF-8 en `cargarDatos()` |
| **2a** | Menú con ≥3 opciones | Opciones 1 (Región), 2 (Origen), 3 (Salir) con `do-while` y `switch` |
| **2b** | Búsqueda insensible a mayúsculas | `campo.toLowerCase().contains(clave.toLowerCase())` |
| **3a** | Validar opción del menú | `leerOpcionValida()` rechaza letras y números fuera de rango con `try-catch` |
| **3b** | Validar palabra clave no vacía | `leerPalabraClave()` repite hasta obtener texto con `isEmpty()` |
| **4** | Guardar en nuevo archivo CSV | `guardarResultados()` genera `resultados_region_patagonia.csv` |
| **5** | Manejar excepciones de archivo | `catch (FileNotFoundException)` y `catch (IOException)` con mensajes claros |

---

## Conceptos de Java aplicados

| Concepto | Dónde se usa |
|---|---|
| **Paquetes** | `package proyecto.pkgfinal` en ambas clases |
| **Encapsulamiento** | Atributos `private` + getters en `VisitaParque` |
| **Constructor** | `VisitaParque(String, String, ...)` |
| **Override** | `toString()` redefinido en `VisitaParque` |
| **ArrayList** | Lista dinámica para almacenar los 3.960 registros |
| **Lectura de archivos** | `BufferedReader` + `FileInputStream` + `InputStreamReader` |
| **Escritura de archivos** | `BufferedWriter` + `FileOutputStream` + `OutputStreamWriter` |
| **try-with-resources** | Cierre automático de streams |
| **Excepciones** | `FileNotFoundException`, `IOException`, `NumberFormatException` |
| **Scanner** | Lectura de entrada del usuario por teclado |
| **do-while** | Menú que se ejecuta al menos una vez |
| **switch** | Dirección de ejecución según opción elegida |
| **String.format()** | Formato de tabla en consola con anchos fijos |
| **Long.parseLong()** | Conversión de String a número entero largo |
| **toLowerCase() + contains()** | Búsqueda insensible a mayúsculas y parcial |
| **Operador ternario** | `criterio.equals("region") ? vp.getRegion() : vp.getOrigen()` |

---

## Ejemplo de ejecución real

```
=============================================================
  SISTEMA DE BUSQUEDA - PARQUES NACIONALES ARGENTINA
=============================================================
  Registros cargados: 3960
  Periodo: 2008 - 2026

  Regiones: buenos aires | cordoba | cuyo | litoral | norte | patagonia
  Origenes: residentes | no residentes | total

-------------------------------------------------------------
  MENU PRINCIPAL
-------------------------------------------------------------
  1. Buscar por Region
  2. Buscar por Origen del visitante
  3. Salir
-------------------------------------------------------------
  Ingrese una opcion (1-3): 1

  Ingrese Region a buscar: Patagonia

  Resultados para Region = "Patagonia":
  -------------------------------------------------------------
  Fecha        | Region        | Origen         |    Visitas
  -------------------------------------------------------------
  2008-1-01    | patagonia     | no residentes  |     141973 | -
  2008-1-01    | patagonia     | residentes     |     230087 | -
  2008-1-01    | patagonia     | total          |     372060 | -
  ...          | ...           | ...            |        ... | ...
  (mostrando 50 de 660)
  Total: 660 registros.
  Suma de visitas: 89.456.231
  [OK] Guardado en: resultados_region_Patagonia.csv
```

---

*Trabajo realizado con datos abiertos del Ministerio de Turismo y Deportes de Argentina.*
*Dataset disponible en: https://datos.yvera.gob.ar*

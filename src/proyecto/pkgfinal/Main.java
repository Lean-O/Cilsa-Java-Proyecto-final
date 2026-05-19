package proyecto.pkgfinal;

// ── Importaciones necesarias ─────────────────────────────────────────────────
import java.io.*;        // FileInputStream, FileOutputStream, BufferedReader,
                         // BufferedWriter, InputStreamReader, OutputStreamWriter,
                         // FileNotFoundException, IOException
import java.util.ArrayList;  // Lista dinámica para guardar los registros
import java.util.Scanner;    // Lectura de entrada del usuario por teclado

/**
 * ============================================================
 *  CLASE PRINCIPAL: Main
 * ============================================================
 *  Trabajo Práctico Final – JAVA 2026
 *
 *  Dataset real utilizado:
 *    "visitas-residentes-y-no-residentes-por-region.csv"
 *  Fuente: Ministerio de Turismo y Deportes de Argentina (SINTA)
 *  Registros: 3.960 filas | Período: 2008 – 2026
 *
 *  FUNCIONALIDADES:
 *    1. Leer datos desde archivo CSV
 *    2. Menú interactivo con 3 opciones
 *    3. Búsqueda por Región o por Origen (insensible a mayúsculas)
 *    4. Validación de entradas del usuario
 *    5. Guardar resultados en nuevo archivo CSV
 *    6. Manejo de excepciones de archivo
 * ============================================================
 */
public class Main {

    /**
     * Constante con el nombre del archivo CSV fuente.
     * Se declara como "static final" porque es un valor fijo que
     * no cambia durante la ejecución del programa.
     * El archivo debe estar en la RAÍZ del proyecto NetBeans
     * (junto a build.xml), no dentro de src/.
     */
    private static final String ARCHIVO_CSV =
        "visitas-residentes-y-no-residentes-por-region.csv";

    // ── Método principal ─────────────────────────────────────────────────────
    /**
     * Punto de entrada del programa. Java siempre arranca aquí.
     * Coordina la carga de datos, el menú y las búsquedas.
     *
     * @param args argumentos de línea de comandos (no se usan)
     */
    public static void main(String[] args) {

        // ── PASO 1: Cargar todos los registros del CSV en memoria ────────────
        // cargarDatos() devuelve null si hay un error de archivo
        ArrayList<VisitaParque> visitas = cargarDatos(ARCHIVO_CSV);

        // Si hubo error al cargar, terminar el programa
        if (visitas == null) return;

        // ── PASO 2: Mostrar encabezado y datos del dataset ───────────────────
        System.out.println("=============================================================");
        System.out.println("  SISTEMA DE BUSQUEDA - PARQUES NACIONALES ARGENTINA");
        System.out.println("=============================================================");
        System.out.println("  Registros cargados: " + visitas.size());
        System.out.println("  Periodo: 2008 - 2026");
        System.out.println("\n  Regiones: buenos aires | cordoba | cuyo | litoral | norte | patagonia");
        System.out.println("  Origenes: residentes | no residentes | total");

        // ── PASO 3: Crear el scanner para leer la entrada del teclado ────────
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;

        // ── PASO 4: Menú principal con bucle do-while ────────────────────────
        // do-while garantiza que el menú se muestre AL MENOS UNA VEZ
        // El bucle continúa mientras la opción elegida no sea 3 (Salir)
        do {
            System.out.println("\n-------------------------------------------------------------");
            System.out.println("  MENU PRINCIPAL");
            System.out.println("-------------------------------------------------------------");
            System.out.println("  1. Buscar por Region");
            System.out.println("  2. Buscar por Origen del visitante");
            System.out.println("  3. Salir");
            System.out.println("-------------------------------------------------------------");
            System.out.print("  Ingrese una opcion (1-3): ");

            // leerOpcionValida() valida que sea un número entre 1 y 3
            opcion = leerOpcionValida(scanner, 1, 3);

            // switch ejecuta el bloque correspondiente a la opción elegida
            switch (opcion) {
                case 1:
                    // Búsqueda por la columna "region"
                    realizarBusqueda(scanner, visitas, "region");
                    break;
                case 2:
                    // Búsqueda por la columna "origen"
                    realizarBusqueda(scanner, visitas, "origen");
                    break;
                case 3:
                    System.out.println("\n  Saliendo. Hasta pronto!");
                    break;
            }

        } while (opcion != 3);

        // Cerrar el Scanner al terminar para liberar recursos
        scanner.close();
    }

    // ────────────────────────────────────────────────────────────────────────
    //  MÉTODO: cargarDatos()
    // ────────────────────────────────────────────────────────────────────────
    /**
     * Lee el archivo CSV línea por línea y crea un objeto VisitaParque
     * por cada fila, almacenándolos en un ArrayList.
     *
     * TÉCNICAS USADAS:
     *  - FileInputStream:      abre el archivo en modo lectura binaria
     *  - InputStreamReader:    convierte bytes a caracteres con encoding UTF-8
     *  - BufferedReader:       lee líneas completas de forma eficiente
     *  - try-with-resources:   cierra el BufferedReader automáticamente
     *
     * MANEJO DE CASOS ESPECIALES:
     *  - BOM (\uFEFF):  algunos CSVs tienen un marcador invisible al inicio
     *  - \r (retorno):  Windows agrega \r antes de \n, hay que eliminarlo
     *  - Líneas vacías: se ignoran con continue
     *  - split(",", -1): el -1 evita que se ignoren campos vacíos al final
     *
     * @param ruta nombre/ruta del archivo CSV
     * @return ArrayList con todos los registros, o null si hay error
     */
    private static ArrayList<VisitaParque> cargarDatos(String ruta) {
        ArrayList<VisitaParque> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(ruta), "UTF-8"))) {

            String linea;
            boolean esCabecera = true; // bandera para saltar la primera fila

            while ((linea = br.readLine()) != null) {

                if (esCabecera) {
                    // Limpiar la primera línea (cabecera) y saltarla
                    linea = linea.replace("\uFEFF", "").replace("\r", "");
                    esCabecera = false;
                    continue; // ir a la siguiente iteración del while
                }

                // Limpiar caracteres extra de cada línea de datos
                linea = linea.replace("\r", "").trim();
                if (linea.isEmpty()) continue; // ignorar líneas en blanco

                // Separar los campos por coma
                // El -1 como límite en split() preserva los campos vacíos al final
                String[] partes = linea.split(",", -1);
                if (partes.length < 4) continue; // fila incompleta, ignorar

                // El campo observaciones puede no existir (columna 5 opcional)
                String obs = (partes.length >= 5) ? partes[4].trim() : "";

                // Crear el objeto y agregarlo a la lista
                lista.add(new VisitaParque(
                    partes[0].trim(),   // fecha
                    partes[1].trim(),   // region
                    partes[2].trim(),   // origen
                    partes[3].trim(),   // visitas
                    obs                 // observaciones
                ));
            }

        } catch (FileNotFoundException ex) {
            // El archivo no existe en la ruta indicada
            System.out.println("[ERROR] Archivo no encontrado: " + ruta);
            System.out.println("  >> Coloque el CSV en la raiz del proyecto (junto a build.xml)");
            return null;

        } catch (IOException ex) {
            // Error durante la lectura del archivo (disco, permisos, etc.)
            System.out.println("[ERROR] Error al leer: " + ex.getMessage());
            return null;
        }

        return lista;
    }

    // ────────────────────────────────────────────────────────────────────────
    //  MÉTODO: realizarBusqueda()
    // ────────────────────────────────────────────────────────────────────────
    /**
     * Orquesta una búsqueda completa:
     *   1. Pide la palabra clave al usuario
     *   2. Filtra los registros que coincidan
     *   3. Muestra los resultados en pantalla (máximo 50)
     *   4. Calcula la suma de visitas
     *   5. Guarda los resultados en un nuevo CSV
     *
     * @param scanner   objeto Scanner para leer del teclado
     * @param visitas   lista completa de registros cargados
     * @param criterio  "region" u "origen" según la opción elegida
     */
    private static void realizarBusqueda(Scanner scanner,
                                          ArrayList<VisitaParque> visitas,
                                          String criterio) {

        // Etiqueta legible para mostrar al usuario
        String etiqueta = criterio.equals("region") ? "Region" : "Origen";

        // Pedir y validar la palabra clave (no puede estar vacía)
        String clave = leerPalabraClave(scanner, etiqueta);

        // ── Filtrar registros ────────────────────────────────────────────────
        // Se crea una nueva lista solo con los resultados que coincidan
        ArrayList<VisitaParque> resultados = new ArrayList<>();

        for (VisitaParque vp : visitas) {
            // Elegir qué campo comparar según el criterio de búsqueda
            String campo = criterio.equals("region") ? vp.getRegion() : vp.getOrigen();

            // contains() busca si la clave está contenida en el campo
            // toLowerCase() hace la búsqueda insensible a mayúsculas/minúsculas
            if (campo.toLowerCase().contains(clave.toLowerCase())) {
                resultados.add(vp);
            }
        }

        // ── Mostrar resultados en pantalla ───────────────────────────────────
        System.out.println("\n  Resultados para " + etiqueta + " = \"" + clave + "\":");
        System.out.println("  -------------------------------------------------------------");

        if (resultados.isEmpty()) {
            System.out.println("  No se encontraron registros.");
        } else {
            // Encabezado de la tabla (printf con formato fijo)
            System.out.printf("  %-12s | %-13s | %-14s | %10s%n",
                              "Fecha", "Region", "Origen", "Visitas");
            System.out.println("  -------------------------------------------------------------");

            // Mostrar filas (limitamos a 50 para no saturar la consola)
            int mostrados = 0;
            for (VisitaParque vp : resultados) {
                System.out.println(vp); // llama automáticamente a toString()
                mostrados++;
                if (mostrados == 50 && resultados.size() > 50) {
                    System.out.println("  ... (mostrando 50 de " + resultados.size() + ")");
                    break;
                }
            }

            System.out.println("  Total: " + resultados.size() + " registros.");

            // ── Estadística: suma de visitas ─────────────────────────────────
            // Se excluyen las filas "total" para evitar contar doble
            long suma = 0;
            for (VisitaParque vp : resultados) {
                if (!vp.getOrigen().equalsIgnoreCase("total")) {
                    try {
                        // Long.parseLong convierte el String "141973" al número 141973
                        suma += Long.parseLong(vp.getVisitas());
                    } catch (NumberFormatException e) {
                        // Si el campo no es número (vacío o texto), se ignora
                    }
                }
            }
            System.out.printf("  Suma de visitas: %,d%n", suma); // %,d agrega separador de miles

            // Guardar en archivo CSV
            guardarResultados(resultados, criterio, clave);
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    //  MÉTODO: guardarResultados()
    // ────────────────────────────────────────────────────────────────────────
    /**
     * Escribe los resultados filtrados en un nuevo archivo CSV.
     * El nombre del archivo refleja el criterio y la clave usados.
     * Ejemplo: resultados_region_patagonia.csv
     *
     * TÉCNICAS USADAS:
     *  - FileOutputStream:      crea/sobreescribe el archivo de salida
     *  - OutputStreamWriter:    convierte caracteres a bytes con UTF-8
     *  - BufferedWriter:        escribe líneas de forma eficiente
     *  - try-with-resources:    cierra el BufferedWriter automáticamente
     *
     * @param resultados   lista de registros filtrados a guardar
     * @param criterio     "region" u "origen"
     * @param clave        término buscado (ej: "patagonia")
     */
    private static void guardarResultados(ArrayList<VisitaParque> resultados,
                                           String criterio, String clave) {

        // Construir el nombre del archivo:
        // replaceAll("\\s+", "_") reemplaza espacios por guiones bajos
        // Ej: "no residentes" → "no_residentes"
        String nombreArchivo = "resultados_" + criterio + "_"
                               + clave.replaceAll("\\s+", "_") + ".csv";

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(nombreArchivo), "UTF-8"))) {

            // Escribir la fila de encabezados
            bw.write("indice_tiempo,region_de_destino,origen_visitantes,visitas,observaciones");
            bw.newLine(); // salto de línea compatible con el SO

            // Escribir cada registro usando el método toCSV() de VisitaParque
            for (VisitaParque vp : resultados) {
                bw.write(vp.toCSV());
                bw.newLine();
            }

            System.out.println("  [OK] Guardado en: " + nombreArchivo);

        } catch (IOException ex) {
            System.out.println("  [ERROR] No se pudo guardar: " + ex.getMessage());
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    //  MÉTODO: leerOpcionValida()
    // ────────────────────────────────────────────────────────────────────────
    /**
     * Lee repetidamente del teclado hasta obtener un número entero
     * dentro del rango [min, max].
     *
     * VALIDACIONES:
     *  - Si el usuario escribe letras → NumberFormatException → pide de nuevo
     *  - Si escribe un número fuera del rango → pide de nuevo
     *  - Solo retorna cuando la entrada es válida
     *
     * @param scanner  objeto Scanner activo
     * @param min      valor mínimo aceptado (inclusive)
     * @param max      valor máximo aceptado (inclusive)
     * @return número entero válido ingresado por el usuario
     */
    private static int leerOpcionValida(Scanner scanner, int min, int max) {
        while (true) {
            String entrada = scanner.nextLine().trim(); // leer y limpiar espacios
            try {
                int valor = Integer.parseInt(entrada); // convertir String a int
                if (valor >= min && valor <= max) {
                    return valor; // entrada válida, salir del bucle
                }
                System.out.print("  Opcion invalida. Ingrese entre " + min + " y " + max + ": ");
            } catch (NumberFormatException e) {
                // El usuario escribió algo que no es un número
                System.out.print("  Ingrese un numero valido: ");
            }
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    //  MÉTODO: leerPalabraClave()
    // ────────────────────────────────────────────────────────────────────────
    /**
     * Solicita al usuario una palabra clave para la búsqueda.
     * Repite la solicitud si el usuario ingresa una cadena vacía.
     *
     * VALIDACIÓN:
     *  - trim() elimina espacios al inicio y al final
     *  - isEmpty() detecta si quedó vacío después del trim
     *
     * @param scanner   objeto Scanner activo
     * @param etiqueta  nombre del campo que se busca ("Region" u "Origen")
     * @return String no vacío ingresado por el usuario
     */
    private static String leerPalabraClave(Scanner scanner, String etiqueta) {
        String clave = "";
        while (clave.isEmpty()) {
            System.out.print("\n  Ingrese " + etiqueta + " a buscar: ");
            clave = scanner.nextLine().trim();
            if (clave.isEmpty()) {
                System.out.println("  [AVISO] La busqueda no puede estar vacia.");
            }
        }
        return clave;
    }
}
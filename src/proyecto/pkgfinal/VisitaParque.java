package proyecto.pkgfinal;

/**
 * ============================================================
 *  CLASE: VisitaParque
 * ============================================================
 *  Representa un único registro (una fila) del archivo CSV:
 *  "visitas-residentes-y-no-residentes-por-region.csv"
 *
 *  Cada fila del CSV tiene este formato:
 *  indice_tiempo , region_de_destino , origen_visitantes , visitas , observaciones
 *  Ejemplo:
 *  2023-1-01 , patagonia , residentes , 230087 ,
 *
 *  Esta clase aplica el principio de ENCAPSULAMIENTO:
 *  todos los atributos son privados y se acceden mediante getters.
 * ============================================================
 */
public class VisitaParque {

    // ── Atributos privados ───────────────────────────────────────────────────
    // Cada atributo corresponde a una columna del CSV

    private String fecha;          // columna 0: "2023-1-01"
    private String region;         // columna 1: "patagonia", "litoral", etc.
    private String origen;         // columna 2: "residentes", "no residentes", "total"
    private String visitas;        // columna 3: cantidad numérica como texto
    private String observaciones;  // columna 4: nota adicional (puede estar vacía)

    // ── Constructor ──────────────────────────────────────────────────────────
    /**
     * Crea un objeto VisitaParque con los 5 valores de una fila CSV.
     * Es llamado por Main.cargarDatos() al leer cada línea del archivo.
     *
     * @param fecha         fecha del registro (ej: "2023-1-01")
     * @param region        región turística (ej: "patagonia")
     * @param origen        tipo de visitante (ej: "residentes")
     * @param visitas       cantidad de visitas como String
     * @param observaciones nota aclaratoria (puede ser vacío "")
     */
    public VisitaParque(String fecha, String region, String origen,
                        String visitas, String observaciones) {
        this.fecha         = fecha;
        this.region        = region;
        this.origen        = origen;
        this.visitas       = visitas;
        this.observaciones = observaciones;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    // Permiten leer los atributos privados desde otras clases (Main)
    // No hay setters porque los datos del CSV no se modifican

    public String getFecha()         { return fecha;         }
    public String getRegion()        { return region;        }
    public String getOrigen()        { return origen;        }
    public String getVisitas()       { return visitas;       }
    public String getObservaciones() { return observaciones; }

    // ── Método toCSV() ───────────────────────────────────────────────────────
    /**
     * Convierte el objeto de vuelta a formato CSV.
     * Es usado por guardarResultados() para escribir cada línea
     * en el archivo de resultados.
     *
     * @return String con los campos separados por coma
     */
    public String toCSV() {
        return fecha + "," + region + "," + origen + "," + visitas + "," + observaciones;
    }

    // ── Método toString() ────────────────────────────────────────────────────
    /**
     * Sobrescribe el método toString() heredado de Object.
     * Devuelve una representación formateada para mostrar en consola.
     *
     * String.format() alinea las columnas con ancho fijo:
     *   %-12s → texto alineado a la izquierda en 12 caracteres
     *   %10s  → texto alineado a la derecha en 10 caracteres
     *
     * Es usado automáticamente cuando se hace: System.out.println(vp)
     *
     * @return línea formateada para la tabla en consola
     */
    @Override
    public String toString() {
        return String.format("  %-12s | %-13s | %-14s | %10s | %s",
                             fecha, region, origen, visitas,
                             (observaciones == null || observaciones.isEmpty())
                             ? "-" : observaciones);
    }
}
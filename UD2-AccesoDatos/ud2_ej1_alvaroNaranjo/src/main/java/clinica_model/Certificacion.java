package clinica_model;

import clinica_persistence.CertificacionPersistence;

/*
 * Clase certificacion. Contiene los datos de las certificaciones
 * de cada veterinario
 * 1:N Veterinario - Certificacion
 */
public class Certificacion {

    // ---- ATRIBUTOS DE LA TABLA Certificacion ---
    private int id;
    private String institucion_emisora;
    private String nombre_especialidad;
    
    // Relación con Veterinario 
    private int veterinario_licencia; // Atributo para la relación con Veterinario
    private Veterinario veterinario; // Objeto Veterinario asociado
    
    /**
     * Constructor vacío de la clase Certificacion.
     * Inicializa los atributos con valores por defecto.
     */
    public Certificacion() {
        super();
        this.id = 0;
        this.institucion_emisora = "";
        this.nombre_especialidad = "";
        this.veterinario_licencia = 0;
        this.veterinario = null;
    }
    
    /**
     * Constructor principal de la clase Certificacion.
     *
     * @param id                  Identificador único de la certificación.
     * @param institucion_emisora Nombre de la institución que emite la certificación.
     * @param nombre_especialidad Nombre de la especialidad certificada.
     * @param veterinario_licencia Número de licencia del veterinario asociado.
     */
    public Certificacion(int id, String institucion_emisora, String nombre_especialidad, int veterinario_licencia) {
        this.id = (id >= 0) ? id : 0;
        this.institucion_emisora = 
            (institucion_emisora != null && !institucion_emisora.trim().isEmpty())
            ? institucion_emisora.trim()
            : "";
        this.nombre_especialidad =
            (nombre_especialidad != null && !nombre_especialidad.trim().isEmpty())
            ? nombre_especialidad.trim()
            : "";
        this.veterinario_licencia = (veterinario_licencia > 0) ? veterinario_licencia : 0;
        this.veterinario = null;
    }
    
    /**
     * Obtiene el identificador de la certificación.
     *
     * @return ID de la certificación.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el identificador de la certificación.
     *
     * @param id ID de la certificación (debe ser mayor o igual a 0).
     */
    public void setId(int id) {
        this.id = (id >= 0) ? id : 0;
    }

    /**
     * Obtiene el nombre de la institución emisora de la certificación.
     *
     * @return Nombre de la institución emisora.
     */
    public String getInstitucion_emisora() {
        return institucion_emisora;
    }

    /**
     * Establece el nombre de la institución emisora de la certificación.
     *
     * @param institucion_emisora Nombre de la institución emisora (no debe ser nulo ni vacío).
     */
    public void setInstitucion_emisora(String institucion_emisora) {
        this.institucion_emisora =
            (institucion_emisora != null && !institucion_emisora.trim().isEmpty())
            ? institucion_emisora.trim()
            : "";
    }

    /**
     * Obtiene el nombre de la especialidad de la certificación.
     *
     * @return Nombre de la especialidad.
     */
    public String getNombre_especialidad() {
        return nombre_especialidad;
    }

    /**
     * Establece el nombre de la especialidad de la certificación.
     *
     * @param nombre_especialidad Nombre de la especialidad (no debe ser nulo ni vacío).
     */
    public void setNombre_especialidad(String nombre_especialidad) {
        this.nombre_especialidad =
            (nombre_especialidad != null && !nombre_especialidad.trim().isEmpty())
            ? nombre_especialidad.trim()
            : "";
    }

    /**
     * Obtiene el número de licencia del veterinario asociado.
     *
     * @return Número de licencia del veterinario.
     */
    public int getVeterinario_licencia() {
        return veterinario_licencia;
    }

    /**
     * Establece el número de licencia del veterinario asociado.
     *
     * @param veterinario_licencia Número de licencia del veterinario (debe ser mayor a 0).
     */
    public void setVeterinario_licencia(int veterinario_licencia) {
        this.veterinario_licencia = (veterinario_licencia > 0) ? veterinario_licencia : 0;
    }

    /**
     * Obtiene el objeto Veterinario asociado mediante carga perezosa.
     * Si el objeto Veterinario no está cargado o no coincide con la licencia actual,
     * se consulta en la capa de persistencia.
     *
     * @return Objeto {@link Veterinario} asociado o null si no existe.
     */
    public Veterinario getVeterinario() {
        if ((veterinario == null && veterinario_licencia > 0) 
                || (veterinario != null && veterinario.getNum_licencia() != veterinario_licencia)) {
        	Veterinario v = CertificacionPersistence.readVeterinarioByLicencia(veterinario_licencia);            
            this.setVeterinario(v);
        }
        return veterinario;
    }

    /**
     * Establece el objeto Veterinario asociado y sincroniza automáticamente
     * el número de licencia.
     *
     * @param veterinario Objeto {@link Veterinario} que se desea asociar.
     */
    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
        this.veterinario_licencia = (veterinario != null) ? veterinario.getNum_licencia() : 0;
    }
    
    /**
     * Devuelve una representación en texto de la certificación.
     *
     * @return Cadena con los datos principales de la certificación.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id)
          .append(" | Especialidad: ").append(nombre_especialidad)
          .append(" | Institución: ").append(institucion_emisora)
          .append(" | Licencia Veterinario: ").append(veterinario_licencia);
        return sb.toString();
    }

}

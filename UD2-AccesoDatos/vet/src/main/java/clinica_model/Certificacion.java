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
    
    // Relación con Veterinario - SIGUIENDO PATRÓN VEHICULO
    private int veterinario_licencia; // Atributo para la relación con Veterinario
    private Veterinario veterinario; // Objeto Veterinario asociado
    
    // Constructor vacio
    public Certificacion() {
        super();
        this.id = 0;
        this.institucion_emisora = "";
        this.nombre_especialidad = "";
        this.veterinario_licencia = 0;
        this.veterinario = null;
    }
    
    // Constructor completo
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
    
    // Getters y setters básicos
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = (id >= 0) ? id : 0;
    }

    public String getInstitucion_emisora() {
        return institucion_emisora;
    }

    public void setInstitucion_emisora(String institucion_emisora) {
        this.institucion_emisora =
            (institucion_emisora != null && !institucion_emisora.trim().isEmpty())
            ? institucion_emisora.trim()
            : "";
    }

    public String getNombre_especialidad() {
        return nombre_especialidad;
    }

    public void setNombre_especialidad(String nombre_especialidad) {
        this.nombre_especialidad =
            (nombre_especialidad != null && !nombre_especialidad.trim().isEmpty())
            ? nombre_especialidad.trim()
            : "";
    }

    public int getVeterinario_licencia() {
        return veterinario_licencia;
    }

    public void setVeterinario_licencia(int veterinario_licencia) {
        this.veterinario_licencia = (veterinario_licencia > 0) ? veterinario_licencia : 0;
    }

    /**
     * Obtiene el objeto Veterinario asociado - CARGA PEREZOSA
     * Si no está cargado o no coincide con el ID, lo carga desde VeterinarioPersistence
     */
    public Veterinario getVeterinario() {
        if ((veterinario == null && veterinario_licencia > 0) 
                || (veterinario != null && veterinario.getNum_licencia() != veterinario_licencia)) {
        	Veterinario v = CertificacionPersistence.readVeterinarioByLicencia(veterinario_licencia);            this.setVeterinario(v);
        }
        return veterinario;
    }

    /**
     * Establece el veterinario y sincroniza automáticamente el ID
     */
    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
        this.veterinario_licencia = (veterinario != null) ? veterinario.getNum_licencia() : 0;
    }
    
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
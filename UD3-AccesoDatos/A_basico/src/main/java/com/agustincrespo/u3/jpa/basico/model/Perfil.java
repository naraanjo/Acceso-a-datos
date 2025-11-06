package com.agustincrespo.u3.jpa.basico.model;

import jakarta.persistence.*;
import java.util.Objects;

// JPA: @Entity y @Table igual que en Usuario
@Entity
@Table(name = "perfiles")
public class Perfil {

	// JPA: ¡LA CLAVE! El Id del Perfil.
	// NOTA: NO es @GeneratedValue. Su valor vendrá del Usuario.
	@Id
	@Column(name = "usuario_id")
	private int id; // Usamos un 'id' simple
	@Column(name = "biografia")
	private String biografia;
	@Column(name = "sitio_web")
	private String sitioWeb;
	@Column(name = "ubicacion")
	private String ubicacion;

    /*
     * --- RELACIÓN 1:1 (LADO DUEÑO) ---
     * Esta es la "entidad dueña" de la relación 1:1, ya que define
     * la clave foránea física en la base de datos.
     *
     * @OneToOne(fetch = FetchType.LAZY):
     * Define la relación 1:1. LAZY es una optimización crucial que indica a
     * JPA que NO cargue el objeto 'Usuario' de la BBDD hasta que se
     * llame explícitamente a getUsuario().
     *
     * @MapsId:
     * Esta es la anotación "mágica" para una clave primaria compartida.
     * Le dice a JPA: "El campo marcado con @Id en ESTA clase ('id')
     * NO se genera. Su valor debe ser copiado (mapeado) desde el ID
     * de la entidad en esta relación (es decir, el ID del 'usuario')".
     *
     * @JoinColumn(name = "usuario_id"):
     * Especifica el nombre de la columna en la BBDD ('usuario_id')
     * que funciona SIMULTÁNEAMENTE como Clave Primaria de esta tabla
     * y Clave Foránea que referencia a la tabla 'usuarios'.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

	public Perfil() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBiografia() {
		return biografia;
	}

	public void setBiografia(String biografia) {
		this.biografia = biografia;
	}

	public String getSitioWeb() {
		return sitioWeb;
	}

	public void setSitioWeb(String sitioWeb) {
		this.sitioWeb = sitioWeb;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public String toString() {
		return "Perfil{" + "id=" + id + ", biografia='" + biografia + '\'' + ", sitioWeb='" + sitioWeb + '\''
				+ ", ubicacion='" + ubicacion + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Perfil perfil = (Perfil) o;
		return id == perfil.id && id != 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}

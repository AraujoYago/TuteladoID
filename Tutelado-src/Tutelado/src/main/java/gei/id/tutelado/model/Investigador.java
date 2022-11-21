package gei.id.tutelado.model;

import javax.persistence.*;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

@TableGenerator(name="generadorIdsInvestigadores", table="tabla_ids",
pkColumnName="nombre_id", pkColumnValue="idInvestigador",
valueColumnName="ultimo_valor_id",
initialValue=0, allocationSize=1)

@NamedQueries ({
	@NamedQuery (name="Investigador.recuperaPorDni",
				 query="SELECT i FROM Investigador i where i.dni=:dni"),
	@NamedQuery (name="Investigador.recuperaTodos",
				 query="SELECT i FROM Investigador i ORDER BY i.dni"),
	@NamedQuery (name="Investigador.recuperaPublicacionesInvestigadorPorDni",
	 			 query="SELECT pu FROM Investigador i INNER JOIN i.publicaciones pu WHERE i.dni=:dni"),
	@NamedQuery (name="Investigador.recuperaInvestigadorSinPublicaciones",
	 			 query="SELECT i FROM Investigador i LEFT OUTER JOIN i.publicaciones pu WHERE pu IS NULL"),
	@NamedQuery(name="Investigador.recuperaCiudadMasInvestigador", 
				 query="SELECT i.ciudad AS ciudad, count(*) AS investigadores FROM Investigador i GROUP BY i.ciudad "
					    + "HAVING count(*) >= ALL (SELECT count(*) FROM Investigador i2 GROUP BY i2.ciudad)")
})

@Entity
public class Investigador implements Comparable<Investigador>{
    @Id
    @GeneratedValue (generator="generadorIdsInvestigadores")
    private Long id;

    @Column(nullable = false, unique = true)
    private String dni;

    @Column(nullable = false, unique=false)
    private String nombre;

    @Column(nullable = false, unique=false)
    private String apellidos;
    
    @Column(nullable = false, unique=true)
    private String telefono;
    
    @Column(nullable = false, unique=false)
    private String ciudad;
    
    @OneToMany (mappedBy="investigador", fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.REMOVE} )
    @OrderBy("fecha ASC")
    private SortedSet<Publicacion> publicaciones = new TreeSet<Publicacion>();
    // NOTA: necesitamos @OrderBy, aún que la colección está definida como LAZY, por si en algún momento accedemos a la propiedad DENTRO de sesión.
    // Garantizamos así que cuando Hibernate cargue la colección, lo haga en la orden adecuada en la consulta que lanza contra la BD
    
    public Long getId() {
		return id;
	}

	public String getDni() {
		return dni;
	}

	public String getNombre() {
		return nombre;
	}

	public String getApellidos() {
		return apellidos;
	}
	
	public String getTelefono() {
		return telefono;
	}
	
	public String getCiudad() {
		return ciudad;
	}

	public SortedSet<Publicacion> getPublicaciones() {
		return publicaciones;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public void setNombre(String nome) {
		this.nombre = nome;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public void setPublicaciones(SortedSet<Publicacion> publicaciones) {
		this.publicaciones = publicaciones;
	}
	
	// Metodo de conveniencia para asegurarnos de que actualizamos los dos extremos de la asociación al mismo tiempo
	public void addPublicacion(Publicacion post) {
		if (post.getInvestigador() != null) throw new RuntimeException ("¡Investigador ya asignado!");
		post.setInvestigador(this);
		// Es un sorted set, añadimos siempre por el orden de fecha (ascendiente)
		this.publicaciones.add(post);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dni == null) ? 0 : dni.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Investigador other = (Investigador) obj;
		if (dni == null) {
			if (other.dni != null)
				return false;
		} else if (!dni.equals(other.dni))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Investigador [id=" + id + ", dni=" + dni + ", nombre=" + nombre + ", apellidos=" + apellidos +  ", telefono=" + telefono + ", ciudad=" + ciudad + ", localidad=" + "]";
	}

	@Override
	public int compareTo(Investigador other) {
		return Comparator.comparing(Investigador::getNombre).thenComparing(Investigador::getApellidos).compare(this, other);
	}
	
}

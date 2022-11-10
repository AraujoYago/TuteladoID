package gei.id.tutelado.model;

import javax.persistence.*;
import java.util.SortedSet;
import java.util.TreeSet;

@TableGenerator(name="xeradorIdsInvestigadores", table="taboa_ids",
pkColumnName="nome_id", pkColumnValue="idInvestigador",
valueColumnName="ultimo_valor_id",
initialValue=0, allocationSize=1)

@NamedQueries ({
	@NamedQuery (name="Investigador.recuperaPorDni",
				 query="SELECT i FROM Investigador i where i.dni=:dni"),
	@NamedQuery (name="Investigador.recuperaTodos",
				 query="SELECT i FROM Investigador i ORDER BY i.dni"),
	@NamedQuery (name="Investigador.recuperaTodosProyecto",
	 			 query="SELECT i FROM Investigador i JOIN i.proyecto pr WHERE pr=:pr ORDER BY i.nombre ASC"),
	@NamedQuery (name="Investigador.recuperaPublicacionesInvestigadorPorDni",
	 			 query="SELECT pu FROM Investigador i INNER JOIN i.publicaciones pu WHERE i.dni=:dni"),
	@NamedQuery (name="Investigador.recuperaInvestigadorSinPublicaciones",
	 			 query="SELECT i FROM Investigador i LEFT OUTER JOIN i.publicaciones pu WHERE pu IS NULL"),
	@NamedQuery(name="Investigador.recuperaCiudadMasInvestigador", 
				 query="SELECT i.ciudad AS ciudad, count(*) AS investigadores FROM Investigador i GROUP BY i.ciudad "
					    + "HAVING count(*) >= ALL (SELECT count(*) FROM Investigador i2 GROUP BY i2.ciudad)")
})

@Entity
public class Investigador {
    @Id
    @GeneratedValue (generator="xeradorIdsInvestigadores")
    private Long id;

    @Column(nullable = false, unique = true)
    private String dni;

    @Column(nullable = false, unique=false)
    private String nome;

    @Column(nullable = false, unique=false)
    private String apellidos;
    
    @Column(nullable = false, unique=true)
    private String telefono;
    
    @Column(nullable = true, unique=false)
    private String ciudad;
    
    @Column(nullable = true, unique=false)
    private String localidad;
    
    @OneToMany (mappedBy="investigador", fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.REMOVE} )
    @OrderBy("fecha ASC")
    private SortedSet<Publicacion> publicaciones = new TreeSet<Publicacion>();
    // NOTA: necesitamos @OrderBy, ainda que a colección estea definida como LAZY, por se nalgun momento accedemos á propiedade DENTRO de sesión.
    // Garantimos así que cando Hibernate cargue a colección, o faga na orde axeitada na consulta que lanza contra a BD
    
    @ManyToOne (cascade={}, fetch=FetchType.EAGER)
    @JoinColumn (nullable=true, unique=false)
    private Proyecto proyecto;
    
    public Long getId() {
		return id;
	}

	public String getDni() {
		return dni;
	}

	public String getNome() {
		return nome;
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
	
	public String getLocalidad() {
		return localidad;
	}

	public SortedSet<Publicacion> getPublicaciones() {
		return publicaciones;
	}
	
	public Proyecto getProyecto() {
		return proyecto;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public void setNome(String nome) {
		this.nome = nome;
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
	
	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public void setPublicaciones(SortedSet<Publicacion> publicaciones) {
		this.publicaciones = publicaciones;
	}
	
	public void setProyecto(Proyecto proyecto) {
		this.proyecto = proyecto;
	}
	
	// Metodo de conveniencia para asegurarnos de que actualizamos os dous extremos da asociación ao mesmo tempo
	public void engadirPublicacion(Publicacion post) {
		if (post.getInvestigador() != null) throw new RuntimeException ("");
		post.setInvestigador(this);
		// É un sorted set, engadimos sempre por orde de data (ascendente)
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
		return "Investigador [id=" + id + ", dni=" + dni + ", nome=" + nome + ", apellidos=" + apellidos +  ", telefono=" + telefono + ", ciudad=" + ciudad + ", localidad=" + localidad + "]";
	}

    
}

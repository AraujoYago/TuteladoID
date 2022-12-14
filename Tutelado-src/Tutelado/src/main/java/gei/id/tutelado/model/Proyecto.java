package gei.id.tutelado.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import javax.persistence.*;

@TableGenerator(name="generadorIdsProyectos", table="tabla_ids",
pkColumnName="nombre_id", pkColumnValue="idProyecto",
valueColumnName="ultimo_valor_id",
initialValue=0, allocationSize=1)

@NamedQueries ({
	@NamedQuery (name="Proyecto.recuperaPorNombre",
				 query="SELECT pr FROM Proyecto pr where pr.nombre=:nombre"),
	@NamedQuery (name="Proyecto.recuperaTodos",
	 			 query="SELECT pr FROM Proyecto pr")
})

@Entity
@Inheritance (strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Proyecto {

	@Id
    @GeneratedValue(generator="generadorIdsProyectos")
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @Column(unique=false, nullable = false)
    private LocalDateTime fechaInicio;

    @Column(unique=false, nullable = true)
    private LocalDateTime fechaFin;

    @ManyToMany (fetch=FetchType.LAZY, cascade= {CascadeType.PERSIST})
    @JoinTable(
    		joinColumns=@JoinColumn(name="idProyecto"),
    		inverseJoinColumns=@JoinColumn(name="idInvestigador"))
    private Set<Investigador> investigadores = new HashSet<Investigador>();
    
    public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public LocalDateTime getFechaInicio() {
		return fechaInicio;
	}
	
	public LocalDateTime getFechaFin() {
		return fechaFin;
	}

	public Set<Investigador> getInvestigadores() {
		return investigadores;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNombre(String nome) {
		this.nombre = nome;
	}

	public void setFechaInicio(LocalDateTime fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	
	public void setFechaFin(LocalDateTime fechaFin) {
		this.fechaFin = fechaFin;
	}

	public void setInvestigadores(SortedSet<Investigador> investigadores) {
		this.investigadores = investigadores;
	}
	
	public void addInvestigador(Investigador investigador) {
		this.investigadores.add(investigador);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
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
		Proyecto other = (Proyecto) obj;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Proyecto [id=" + id + ", nome=" + nombre + ", fechaInicio=" + fechaInicio +  ", fechaFin=" + fechaFin + "]";
	}

}

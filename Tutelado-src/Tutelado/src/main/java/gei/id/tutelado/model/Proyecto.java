package gei.id.tutelado.model;

import java.time.LocalDateTime;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.*;

@TableGenerator(name="xeradorIdsProyectos", table="taboa_ids",
pkColumnName="nome_id", pkColumnValue="idProyecto",
valueColumnName="ultimo_valor_id",
initialValue=0, allocationSize=1)

@NamedQueries ({
	@NamedQuery (name="Publicacion.recuperaPorNombre",
				 query="SELECT p FROM Publicacion p where p.nombre=:nombre"),
	@NamedQuery (name="Publicacion.recuperaTodasInvestigador",
	 			 query="SELECT p FROM Publicacion p JOIN p.investigador i WHERE i=:i ORDER BY p.fecha DESC"),
	@NamedQuery (name="Publicacion.recuperaTodas",
	 			 query="SELECT p FROM Publicacion p")
})

@Entity
@Inheritance (strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Proyecto {

	@Id
    @GeneratedValue(generator="xeradorIdsProyectos")
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;

    @Column(unique=false, nullable = false)
    private LocalDateTime fechaInicio;

    @Column(unique=false, nullable = true)
    private LocalDateTime fechaFin;

    @OneToMany (mappedBy="proyecto", fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.REMOVE} )
    @OrderBy("fechaInicio ASC")
    private SortedSet<Investigador> investigadores = new TreeSet<Investigador>();
    
    public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public LocalDateTime getFechaInicio() {
		return fechaInicio;
	}
	
	public LocalDateTime getFechaFin() {
		return fechaFin;
	}

	public SortedSet<Investigador> getInvestigadores() {
		return investigadores;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
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
	
	// Metodo de conveniencia para asegurarnos de que actualizamos os dous extremos da asociación ao mesmo tempo
	public void engadirInvestigador(Investigador investigador) {
		if (investigador.getProyecto() != null) throw new RuntimeException ("");
		investigador.setProyecto(this);
		// É un sorted set, engadimos sempre por orde de data (ascendente)
		this.investigadores.add(investigador);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
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
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Proyecto [id=" + id + ", nome=" + nome + ", fechaInicio=" + fechaInicio +  ", fechaFin=" + fechaFin + "]";
	}

}

package gei.id.tutelado.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@TableGenerator(name="xeradorIdsPublicaciones", table="taboa_ids",
pkColumnName="nome_id", pkColumnValue="idPublicacion",
valueColumnName="ultimo_valor_id",
initialValue=0, allocationSize=1)

@NamedQueries ({
	@NamedQuery (name="Publicacion.recuperaPornome",
				 query="SELECT p FROM Publicacion p where p.nome=:nome"),
	@NamedQuery (name="Publicacion.recuperaTodasInvestigador",
	 			 query="SELECT p FROM Publicacion p JOIN p.investigador i WHERE i=:i ORDER BY p.fecha DESC"),
	@NamedQuery (name="Publicacion.recuperaTodas",
	 			 query="SELECT p FROM Publicacion p")
})

@Entity
public class Publicacion implements Comparable<Publicacion> {
    @Id
    @GeneratedValue(generator="xeradorIdsPublicaciones")
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;

    @Column(unique=false, nullable = false)
    private String revista;

    @Column(unique=false, nullable = false)
    private LocalDateTime fecha;

    @ManyToOne (cascade={}, fetch=FetchType.EAGER)
    @JoinColumn (nullable=false, unique=false)
    private Investigador investigador;

	public Long getId() {
		return id;
	}

	public String getnome() {
		return nome;
	}

	public String getRevista() {
		return revista;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public Investigador getInvestigador() {
		return investigador;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setnome(String nome) {
		this.nome = nome;
	}

	public void setRevista(String revista) {
		this.revista = revista;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	public void setInvestigador(Investigador investigador) {
		this.investigador = investigador;
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
		Publicacion other = (Publicacion) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Publicacion [id=" + id + ", nome=" + nome + ", revista=" + revista + ", fecha=" + fecha + "]";
	}

	@Override
	public int compareTo(Publicacion other) {
		return (this.fecha.isBefore(other.getFecha())? -1:1);
	}

    

}

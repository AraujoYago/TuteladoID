package gei.id.tutelado.model;

import javax.persistence.*;

@Entity
public class Revision extends Proyecto {

    @Column(nullable = false, unique = false)
    private String motivo;
    
    public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	} 

}
package gei.id.tutelado.model;

import javax.persistence.*;

@Entity
public class Nuevo extends Proyecto {

    @Column(nullable = false, unique = false)
    private Float presupuesto;
    
    public Float getPresupuesto() {
		return presupuesto;
	}

	public void setPresupuesto(Float presupuesto) {
		this.presupuesto = presupuesto;
	} 

}
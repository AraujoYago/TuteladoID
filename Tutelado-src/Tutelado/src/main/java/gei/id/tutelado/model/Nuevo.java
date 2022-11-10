package gei.id.tutelado.model;

import javax.persistence.*;

@Entity
public class Nuevo extends Proyecto {

    @Column(nullable = false, unique = false)
    private float presupuesto;
    
    public float getPresupuesto() {
		return presupuesto;
	}

	public void setPresupuesto(float presupuesto) {
		this.presupuesto = presupuesto;
	} 

}
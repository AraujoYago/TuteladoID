package gei.id.tutelado.dao;

import java.util.List;
import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Proyecto;

public interface ProyectoDao {
    
	void setup (Configuracion config);
	
	// OPERACIONS CRUD BASICAS
	Proyecto almacena (Proyecto pr);
	Proyecto modifica (Proyecto pr);
	void elimina (Proyecto pr);
	Proyecto recuperaPorNombre (String nombre);
	
	//QUERIES ADICIONAIS
	List<Proyecto> recuperaTodos();

}

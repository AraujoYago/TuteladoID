package gei.id.tutelado.dao;

import java.util.List;
import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Proyecto;

public interface ProyectoDao {
    
	void setup (Configuracion config);
	
	// OPERACIONS CRUD BASICAS
	Proyecto almacena (Proyecto log);
	Proyecto modifica (Proyecto log);
	void elimina (Proyecto log);
	Proyecto recuperaPorNombre (String codigo);
	
	//QUERIES ADICIONAIS
		List<Proyecto> recuperaTodos(Proyecto p);

}

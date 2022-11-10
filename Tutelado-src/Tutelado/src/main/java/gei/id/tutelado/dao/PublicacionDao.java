package gei.id.tutelado.dao;

import java.util.List;
import java.util.Map;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Publicacion;
import gei.id.tutelado.model.Investigador;

public interface PublicacionDao {
    
	void setup (Configuracion config);
	
	// OPERACIONES CRUD BASICAS
	Publicacion almacena (Publicacion post);
	Publicacion modifica (Publicacion post);
	void elimina (Publicacion post);
	Publicacion recuperaPorNombre (String nombre);
	
	// QUERIES ADICIONALES
	List<Publicacion> recuperaTodas();
	List<Publicacion> recuperaTodasInvestigador(Investigador i);
	Map<String, Integer> recuperaNumeroPublicacionesRevista(String revista); // AGREGACIÓN

}
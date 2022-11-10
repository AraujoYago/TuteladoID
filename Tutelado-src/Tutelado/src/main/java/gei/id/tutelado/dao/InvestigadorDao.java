package gei.id.tutelado.dao;

import java.util.List;
import java.util.Map;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Investigador;
import gei.id.tutelado.model.Proyecto;
import gei.id.tutelado.model.Publicacion;

public interface InvestigadorDao {
    	
	void setup (Configuracion config);
	
	// OPERACIONESS CRUD BASICAS
	Investigador almacena (Investigador investigador);
	Investigador modifica (Investigador investigador);
	void elimina (Investigador investigador);	
	Investigador recuperaPorDni (String dni);
	
	// OPERACIONES POR ATRIBUTOS LAZY
	Investigador restauraPublicaciones (Investigador investigador);   
	
	// QUERIES ADICIONALES
	List<Investigador> recuperaTodos();
	List<Investigador> recuperaTodosProyecto(Proyecto pr);
	List<Publicacion> recuperaPublicacionesInvestigadorPorDni(String dni); // INNER JOIN
	List<Investigador> recuperaInvestigadorSinPublicaciones(); // OUTER JOIN
	Map<String, Integer> recuperaCiudadMasInvestigador(); // SUBCONSULTA
	
}

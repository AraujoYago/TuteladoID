package gei.id.tutelado.dao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Tuple;

import org.hibernate.LazyInitializationException;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Investigador;
import gei.id.tutelado.model.Publicacion;


public class InvestigadorDaoJPA implements InvestigadorDao {

	private EntityManagerFactory emf; 
	private EntityManager em;

	@Override
	public void setup (Configuracion config) {
		this.emf = (EntityManagerFactory) config.get("EMF");
	}

	@Override
	public Investigador almacena(Investigador investigador) {

		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			em.persist(investigador);

			em.getTransaction().commit();
			em.close();

		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return investigador;
	}

	@Override
	public Investigador modifica(Investigador investigador) {

		try {
			
			em = emf.createEntityManager();
			em.getTransaction().begin();

			investigador= em.merge (investigador);

			em.getTransaction().commit();
			em.close();		
			
		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return (investigador);
	}

	@Override
	public void elimina(Investigador investigador) {
		try {
			
			em = emf.createEntityManager();
			em.getTransaction().begin();

			Investigador investigadorTmp = em.find (Investigador.class, investigador.getId());
			em.remove (investigadorTmp);

			em.getTransaction().commit();
			em.close();
			
		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
	}


	@Override
	public Investigador recuperaPorDni(String dni) {
		List <Investigador> investigadores=null;

		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			investigadores = em.createNamedQuery("Investigador.recuperaPorDni", Investigador.class).setParameter("dni", dni).getResultList(); 

			em.getTransaction().commit();
			em.close();	

		}
		catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}

		return (investigadores.size()!=0?investigadores.get(0):null);
	}


	@Override
	public Investigador restauraPublicaciones(Investigador investigador) {
		// Devolve o obxecto investigador coa coleccion de entradas cargada (se non o estaba xa)

		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			try {
				investigador.getPublicaciones().size();
			} catch (Exception ex2) {
				if (ex2 instanceof LazyInitializationException)

				{
					/* OPCION DE IMPLEMENTACION 1 (comentada): Cargar la propiedad "manualmente" con una consulta, 
					 *  y actualizar tambien "manualmente" el valor de la propiedad  */  
					//List<Publicacion> publicaciones = (List<Publicacion>) entityManager.createQuery("From Publicacion l where l.Investigador=:Investigador order by fecha").setParameter("Investigador",investigador).getResultList();
					//investigador.setPublicaciones (publicaciones);

					/* OPCION DE IMPLEMENTACIÓN 2: Volver a ligar el objecto Investigador a un nuevo CP, 
					 * y acceder a la propiedad en ese momento, para que Hibernate la cargue.*/
					investigador = em.merge(investigador);
					investigador.getPublicaciones().size();

				} else {
					throw ex2;
				}
			}
			em.getTransaction().commit();
			em.close();
		}
		catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		
		return (investigador);

	}

	@Override
	public List<Investigador> recuperaTodos() {
		List <Investigador> investigadores=null;

		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			investigadores = em.createNamedQuery("Investigador.recuperaTodos", Investigador.class).getResultList(); 

			em.getTransaction().commit();
			em.close();	

		}
		catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}

		return investigadores;
	}

	@Override
	public List<Publicacion> recuperaPublicacionesInvestigadorPorDni(String dni) {
		List<Publicacion> publicaciones = null;
		
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();
			
			publicaciones = em.createNamedQuery("Investigador.recuperaPublicacionesInvestigadorPorDni", Publicacion.class).setParameter("dni", dni).getResultList();
			
			em.getTransaction().commit();
			em.close();
		} catch(Exception ex) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		
		return publicaciones;
	}

	@Override
	public List<Investigador> recuperaInvestigadorSinPublicaciones() {
		List<Investigador> investigadores = null;
		
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();
			
			investigadores = em.createNamedQuery("Investigador.recuperaInvestigadorSinPublicaciones", Investigador.class).getResultList();
			
			em.getTransaction().commit();
			em.close();
		} catch (Exception ex) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return investigadores;
	}

	@Override
	public Map<String, Integer> recuperaCiudadMasInvestigador() {
		Map<String, Integer> result = null;

		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			result = em.createNamedQuery("Investigador.recuperaCiudadMasInvestigador", Tuple.class).getResultStream()
					.collect(Collectors.toMap(tuple -> tuple.get("ciudad").toString(),
							tuple -> ((Number) tuple.get("investigadores")).intValue()));

			em.getTransaction().commit();
			em.close();
		} catch (Exception ex) {
			if (em != null && em.isOpen()) {
				if (em.getTransaction().isActive())
					em.getTransaction().rollback();
				em.close();
				throw (ex);
			}
		}
		return result;
	}
	
}

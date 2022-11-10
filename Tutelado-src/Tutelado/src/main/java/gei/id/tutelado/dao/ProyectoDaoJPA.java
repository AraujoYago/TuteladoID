package gei.id.tutelado.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.LazyInitializationException;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Proyecto;


public class ProyectoDaoJPA implements ProyectoDao {

	private EntityManagerFactory emf; 
	private EntityManager em;

	@Override
	public void setup (Configuracion config) {
		this.emf = (EntityManagerFactory) config.get("EMF");
	}

	@Override
	public Proyecto almacena(Proyecto Proyecto) {

		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			em.persist(Proyecto);

			em.getTransaction().commit();
			em.close();

		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return Proyecto;
	}

	@Override
	public Proyecto modifica(Proyecto Proyecto) {

		try {
			
			em = emf.createEntityManager();
			em.getTransaction().begin();

			Proyecto= em.merge (Proyecto);

			em.getTransaction().commit();
			em.close();		
			
		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return (Proyecto);
	}

	@Override
	public void elimina(Proyecto Proyecto) {
		try {
			
			em = emf.createEntityManager();
			em.getTransaction().begin();

			Proyecto ProyectoTmp = em.find (Proyecto.class, Proyecto.getId());
			em.remove (ProyectoTmp);

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
	public Proyecto recuperaPorNombre(String nombre) {
		List <Proyecto> proyectos=null;

		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			proyectos = em.createNamedQuery("Proyecto.recuperaPorNombre", Proyecto.class).setParameter("nombre", nombre).getResultList(); 

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

		return (proyectos.size()!=0?proyectos.get(0):null);
	}

	
	@Override
	public List<Proyecto> recuperaTodos() {
		List <Proyecto> proyectos=null;

		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			proyectos = em.createNamedQuery("Proyecto.recuperaTodos", Proyecto.class).getResultList(); 

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

		return proyectos;
	}
	
}
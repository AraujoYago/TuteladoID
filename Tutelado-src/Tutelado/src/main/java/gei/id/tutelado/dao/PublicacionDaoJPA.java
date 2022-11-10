package gei.id.tutelado.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Publicacion;
import gei.id.tutelado.model.Investigador;

public class PublicacionDaoJPA implements PublicacionDao {

	private EntityManagerFactory emf; 
	private EntityManager em;
    
	@Override
	public void setup (Configuracion config) {
		this.emf = (EntityManagerFactory) config.get("EMF");
	}
	

	@Override
	public Publicacion almacena(Publicacion post) {
		try {
				
			em = emf.createEntityManager();
			em.getTransaction().begin();

			em.persist(post);

			em.getTransaction().commit();
			em.close();
		
		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return post;
	}

	@Override
	public Publicacion modifica(Publicacion post) {
		try {

			em = emf.createEntityManager();		
			em.getTransaction().begin();

			post = em.merge (post);

			em.getTransaction().commit();
			em.close();
			
		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return post;
	}

	@Override
	public void elimina(Publicacion post) {
		try {

			em = emf.createEntityManager();
			em.getTransaction().begin();

			Publicacion postTmp = em.find (Publicacion.class, post.getId());
			em.remove (postTmp);

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
	public Publicacion recuperaPorNombre(String nombre) {

		List<Publicacion> publicaciones=null;
		
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			publicaciones = em.createNamedQuery("Publicacion.recuperaPorNombre", Publicacion.class)
					.setParameter("nombre", nombre).getResultList(); 

			em.getTransaction().commit();
			em.close();
		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return (publicaciones.size()==0?null:publicaciones.get(0));
	}

	
	@Override
	public List<Publicacion> recuperaTodasInvestigador(Investigador i) {
		List <Publicacion> publicaciones=null;

		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			publicaciones = em.createNamedQuery("Publicacion.recuperaTodasInvestigador", Publicacion.class).setParameter("i", i).getResultList(); 

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

		return publicaciones;
	}

}

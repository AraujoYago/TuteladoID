package gei.id.tutelado;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.*;

public class ProdutorDatosProba {


	// Crea un conjunto de objectos para utilizar en los casos de prueba
	
	private EntityManagerFactory emf=null;
	
	public Investigador i0, i1;
	public List<Investigador> listaI;
	
	public Publicacion pu1A, pu1B;
	public List<Publicacion> listaPu;
	
	public Nuevo n0, n1;
	
	public Revision r0, r1;
	
	
	
	public void Setup (Configuracion config) {
		this.emf=(EntityManagerFactory) config.get("EMF");
	}
	
	public void creaInvestigadoresSueltos() {

		this.i0 = new Investigador();
		this.i0.setDni("000000000");
        this.i0.setNombre("Juan");
		this.i0.setApellidos("Martinez Lopez");
		this.i0.setTelefono("674456444");
		this.i0.setCiudad("Pontevedra");

		this.i1 = new Investigador();
		this.i1.setDni("000000001");
		this.i1.setNombre("Marta");
		this.i1.setApellidos("Carregal Jimenez");
		this.i1.setTelefono("654564333");
		this.i1.setCiudad("A Coruña");

        this.listaI = new ArrayList<Investigador> ();
        this.listaI.add(0,i0);
        this.listaI.add(1,i1);        

	}
	
	public void creaPublicacionesSueltas() {
		
		this.pu1A=new Publicacion();
        this.pu1A.setNombre("pu001");
        this.pu1A.setRevista("revista1");
        this.pu1A.setFecha(LocalDateTime.of(2018,10,30,12,00));

        this.pu1B=new Publicacion();
        this.pu1B.setNombre("pu002");
        this.pu1B.setRevista("revista2");
        this.pu1B.setFecha(LocalDateTime.of(2019,02,15,22,30));

        this.listaPu = new ArrayList<Publicacion> ();
        this.listaPu.add(0,this.pu1A);
        this.listaPu.add(1,this.pu1B);        

	}
	
	public void creaNuevosSueltos() {
		
		this.n0 = new Nuevo();
		this.n0.setNombre("Nuevo1");
		this.n0.setFechaInicio(LocalDateTime.of(2018,10,01,10,18));
		this.n0.setFechaFin(LocalDateTime.of(2019,8,07,20,00));
		this.n0.setPresupuesto(1000f);

		this.n1 = new Nuevo();
		this.n1.setNombre("Nuevo2");
		this.n1.setFechaInicio(LocalDateTime.of(2019,02,15,22,30));
		this.n1.setFechaFin(null);
		this.n1.setPresupuesto(2000f);       

	}
	
	public void creaRevisionesSueltos() {
		
		this.r0 = new Revision();
		this.r0.setNombre("Revision1");
		this.r0.setFechaInicio(LocalDateTime.of(2019,03,03,11,54));
		this.r0.setFechaFin(LocalDateTime.of(2019,06,07,20,00));
		this.r0.setMotivo("Motivo1 de la revisión del proyecto");

		this.r1 = new Revision();
		this.r1.setNombre("Revision2");
		this.r1.setFechaInicio(LocalDateTime.of(2019,02,15,22,30));
		this.r1.setFechaFin(null);
		this.r1.setMotivo("Motivo2 de la revisión del proyecto");        

	}
	
	public void creaInvestigadoresConPublicaciones() {

		this.creaInvestigadoresSueltos();
		this.creaPublicacionesSueltas();
		
        this.i1.addPublicacion(this.pu1A);
        this.i1.addPublicacion(this.pu1B);

	}
	
	public void creaProyectosConInvestigadores() {

		this.creaNuevosSueltos();
		this.creaInvestigadoresSueltos();
		
        this.n1.addInvestigador(this.i1);
        this.n1.addInvestigador(this.i1);

	}
	
	public void registrarInvestigadores() {
		EntityManager em=null;
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			Iterator<Investigador> itI = this.listaI.iterator();
			while (itI.hasNext()) {
				Investigador i = itI.next();
				em.persist(i);
				// DESCOMENTAR SI LA PROPAGACION DEL PERSIST NO ESTA ACTIVADA
				/*
				Iterator<Publicacion> itPu = i.getPublicaciones().iterator();
				while (itPu.hasNext()) {
					em.persist(itPu.next());
				}
				*/
			}
			em.getTransaction().commit();
			em.close();
		} catch (Exception e) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw (e);
			}
		}	
	}
	
	
	// CREO QUE FALTARÍA HACER EL MÉTODO registrarProyectos() //
	
	
	public void limpaBD () {
		EntityManager em=null;
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();
			
			Iterator <Investigador> itI = em.createNamedQuery("Investigador.recuperaTodos", Investigador.class).getResultList().iterator();
			while (itI.hasNext()) em.remove(itI.next());
			Iterator <Publicacion> itPu = em.createNamedQuery("Publicacion.recuperaTodas", Publicacion.class).getResultList().iterator();
			while (itPu.hasNext()) em.remove(itPu.next());
			Iterator <Proyecto> itPr = em.createNamedQuery("Proyecto.recuperaTodos", Proyecto.class).getResultList().iterator();
			while (itPr.hasNext()) em.remove(itPr.next());

			
			em.createNativeQuery("UPDATE taboa_ids SET ultimo_valor_id=0 WHERE nome_id='idInvestigador'" ).executeUpdate();
			em.createNativeQuery("UPDATE taboa_ids SET ultimo_valor_id=0 WHERE nome_id='idPublicacion'" ).executeUpdate();

			em.getTransaction().commit();
			em.close();
		} catch (Exception e) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw (e);
			}
		}
	}
	
	
}

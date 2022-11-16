package gei.id.tutelado;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.configuracion.ConfiguracionJPA;
import gei.id.tutelado.dao.InvestigadorDao;
import gei.id.tutelado.dao.InvestigadorDaoJPA;
import gei.id.tutelado.dao.ProyectoDao;
import gei.id.tutelado.dao.ProyectoDaoJPA;
import gei.id.tutelado.model.Investigador;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.runners.MethodSorters;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.lang.Exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.LazyInitializationException;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class P04_Proyectos_Investigadores {

    private Logger log = LogManager.getLogger("gei.id.tutelado");

    private static ProdutorDatosProba produtorDatos = new ProdutorDatosProba();
    
    private static Configuracion cfg;
    private static InvestigadorDao invDao;
    private static ProyectoDao proDao;
    
    @Rule
    public TestRule watcher = new TestWatcher() {
       protected void starting(Description description) {
    	   log.info("");
    	   log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    	   log.info("Iniciando test: " + description.getMethodName());
    	   log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
       }
       protected void finished(Description description) {
    	   log.info("");
    	   log.info("-----------------------------------------------------------------------------------------------------------------------------------------");
    	   log.info("Finalizado test: " + description.getMethodName());
    	   log.info("-----------------------------------------------------------------------------------------------------------------------------------------");
       }
    };
    
    
    @BeforeClass
    public static void init() throws Exception {
    	cfg = new ConfiguracionJPA();
    	cfg.start();

    	invDao = new InvestigadorDaoJPA();
    	proDao = new ProyectoDaoJPA();
    	invDao.setup(cfg);
    	proDao.setup(cfg);
    	
    	produtorDatos = new ProdutorDatosProba();
    	produtorDatos.Setup(cfg);
    }
    
    @AfterClass
    public static void endclose() throws Exception {
    	cfg.endUp();    	
    }
    
    
    
	@Before
	public void setUp() throws Exception {		
		log.info("");	
		log.info("Limpando BD -----------------------------------------------------------------------------------------------------");
		produtorDatos.limpaBD();
	}

	@After
	public void tearDown() throws Exception {
	}	

    @Test 
    public void test01_Recuperacion() {
   	
    	Investigador i;
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

		produtorDatos.creaProyectosConInvestigadores();
    	produtorDatos.registrarProyectos();


		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba da recuperación (por dni) de investigadores sueltos\n"   
		+ "\t\t\t\t Casos contemplados:\n"
		+ "\t\t\t\t a) Recuperación por dni existente\n"
		+ "\t\t\t\t b) Recuperacion por dni inexistente\n");     	

    	
		log.info("Probando recuperacion por dni EXISTENTE --------------------------------------------------");

    	i = invDao.recuperaPorDni(produtorDatos.i0.getDni());

    	Assert.assertEquals (produtorDatos.i0.getDni(),         i.getDni());
    	Assert.assertEquals (produtorDatos.i0.getNombre(),      i.getNombre());
    	Assert.assertEquals (produtorDatos.i0.getApellidos(),   i.getApellidos());
    	Assert.assertEquals (produtorDatos.i0.getTelefono(),    i.getTelefono());
    	Assert.assertEquals (produtorDatos.i0.getCiudad(),      i.getCiudad());


    	log.info("");	
		log.info("Probando recuperacion por dni INEXISTENTE --------------------------------------------------");
    	
    	i = invDao.recuperaPorDni("iwbvyhuebvuwebvi");
    	Assert.assertNull (i);

    } 	

    @Test 
    public void test02_Alta() {


    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

		produtorDatos.creaNuevosSueltos();
		produtorDatos.creaInvestigadoresSueltos();
    	produtorDatos.registrarProyectos();

    	log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba da grabación de investigadores sueltos\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Primer investigador vinculado a un proyecto\n"
    			+ "\t\t\t\t b) Nuevo investigador para un proyecto con investigadores previos\n");     	


    	produtorDatos.n1.addInvestigador(produtorDatos.i0);
		
    	log.info("");	
		log.info("Grabando primeir investigador de un proyecto --------------------------------------------------------------------");
    	Assert.assertNull(produtorDatos.i0.getId());
    	invDao.almacena(produtorDatos.i0);
    	Assert.assertNotNull(produtorDatos.i0.getId());

    	produtorDatos.n1.addInvestigador(produtorDatos.i1);

    	log.info("");	
		log.info("Grabando segundo investigador de un proyecto ---------------------------------------------------------------------");
    	Assert.assertNull(produtorDatos.i1.getId());
    	invDao.almacena(produtorDatos.i1);
    	Assert.assertNotNull(produtorDatos.i1.getId());
    	
    }
    
    @Test 
    public void test03_Eliminacion() {
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

    	produtorDatos.creaProyectosConInvestigadores();
    	produtorDatos.registrarProyectos();

    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba de eliminación de investigador suelto (asignado a proyecto)\n");
    	
		Assert.assertNotNull(invDao.recuperaPorDni(produtorDatos.i0.getDni()));
    	invDao.elimina(produtorDatos.i0);    	
		Assert.assertNull(invDao.recuperaPorDni(produtorDatos.i0.getDni()));

    } 	
    
    @Test 
    public void test04_Modificacion() {

    	Investigador ii1, ii2;
    	String nuevaCiudad;
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");
  
		produtorDatos.creaProyectosConInvestigadores();
    	produtorDatos.registrarProyectos();

    	log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba de modificación de la información de un investigador suelto\n");
    	
		nuevaCiudad = new String ("Nombre ciudad nueva");

		ii1 = invDao.recuperaPorDni(produtorDatos.i0.getDni());
		Assert.assertNotEquals(nuevaCiudad, ii1.getCiudad());
    	ii1.setCiudad(nuevaCiudad);

    	invDao.modifica(ii1);  
    
        ii2 = invDao.recuperaPorDni(produtorDatos.i0.getDni());
		Assert.assertEquals(nuevaCiudad, ii2.getCiudad());
		
    }
    
    @Test 
    public void test05_Propagacion_Persist() {

   	    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

		produtorDatos.creaNuevosSueltos();
    	produtorDatos.creaInvestigadoresSueltos();
        
    	produtorDatos.n1.addInvestigador(produtorDatos.i0);
    	produtorDatos.n1.addInvestigador(produtorDatos.i1);


    	log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba da grabación de nuevo proyecto con investigadores asociados\n");

    	Assert.assertNull(produtorDatos.n1.getId());
    	Assert.assertNull(produtorDatos.i0.getId());
    	Assert.assertNull(produtorDatos.i1.getId());
    	
		log.info("Grabando na BD proyecto con  investigadores----------------------------------------------------------------------");

    	// Aquí el persist sobre r1 debe propagarse a i0 e i1
		proDao.almacena(produtorDatos.n1);

		Assert.assertNotNull(produtorDatos.n1.getId());
    	Assert.assertNotNull(produtorDatos.i0.getId());
    	Assert.assertNotNull(produtorDatos.i1.getId());
    	
    }

    @Test 
    public void test06_EAGER() {
    	
    	Investigador i;
    	Boolean excepcion;
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

		produtorDatos.creaProyectosConInvestigadores();
    	produtorDatos.registrarProyectos();

		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba da recuperación de propiedades EAGER\n");   
    	
		log.info("Probando (que no hay excepcion tras) acceso inicial a la propiedad EAGER fuera de sesion ----------------------------------------");
    	
    	i = invDao.recuperaPorDni(produtorDatos.i0.getDni());  
		log.info("Acceso a proyecto de investigador");
    	try	{
        	Assert.assertEquals(produtorDatos.n1, i.getProyecto());
        	excepcion=false;
    	} catch (LazyInitializationException ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	};    	
    	Assert.assertFalse(excepcion);
    	
    }

    @Test
    public void test07_Excepcions() {
    	
    	Boolean excepcion;
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

		produtorDatos.creaNuevosSueltos();
		produtorDatos.creaInvestigadoresSueltos();
		produtorDatos.registrarProyectos();
		
		produtorDatos.n1.addInvestigador(produtorDatos.i0);		
		invDao.almacena(produtorDatos.i0);
		
    	log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Prueba de violacion de restriciones not null y unique\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Grabación de investigador con dni nulo\n"
    			+ "\t\t\t\t b) Grabación de investigador con dni duplicado\n");

		produtorDatos.n1.addInvestigador(produtorDatos.i1);
    	    	
    	log.info("");	
		log.info("Probando grabación de investigador con dni nulo -------------------------------------------------------------------");
		produtorDatos.i1.setDni(null);
    	try {
        	invDao.almacena(produtorDatos.i1);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);

    	log.info("");	
		log.info("Probando grabación de investigador con dni duplicado --------------------------------------------------------------");
		produtorDatos.i1.setDni(produtorDatos.i0.getDni());
    	try {
        	invDao.almacena(produtorDatos.i1);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);
    	
    }

}
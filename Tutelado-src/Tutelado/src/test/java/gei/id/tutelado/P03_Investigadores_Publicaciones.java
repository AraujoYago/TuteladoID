package gei.id.tutelado;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.configuracion.ConfiguracionJPA;
import gei.id.tutelado.dao.InvestigadorDao;
import gei.id.tutelado.dao.InvestigadorDaoJPA;
import gei.id.tutelado.dao.PublicacionDao;
import gei.id.tutelado.dao.PublicacionDaoJPA;
import gei.id.tutelado.model.Investigador;
import gei.id.tutelado.model.Publicacion;

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
public class P03_Investigadores_Publicaciones {

    private Logger log = LogManager.getLogger("gei.id.tutelado");

    private static ProdutorDatosProba produtorDatos = new ProdutorDatosProba();
    
    private static Configuracion cfg;
    private static InvestigadorDao invDao;
    private static PublicacionDao publiDao;
    
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
    	publiDao = new PublicacionDaoJPA();
    	invDao.setup(cfg);
    	publiDao.setup(cfg);
    	
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
   	
    	Publicacion p;
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

		produtorDatos.creaInvestigadoresConPublicaciones();
    	produtorDatos.registrarInvestigadores();


		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba da recuperación (por nombre) de publicaciones sueltas\n"   
		+ "\t\t\t\t Casos contemplados:\n"
		+ "\t\t\t\t a) Recuperación por nombre existente\n"
		+ "\t\t\t\t b) Recuperacion por nombre inexistente\n");     	

    	// Situación de partida:
    	// i1, pu1A, pu1B desligados
    	
		log.info("Probando recuperacion por nombre EXISTENTE --------------------------------------------------");

    	p = publiDao.recuperaPorNombre(produtorDatos.pu1A.getNombre());

    	Assert.assertEquals (produtorDatos.pu1A.getNombre(),           p.getNombre());
    	Assert.assertEquals (produtorDatos.pu1A.getRevista(),          p.getRevista());
    	Assert.assertEquals (produtorDatos.pu1A.getFecha(),            p.getFecha());
    	Assert.assertEquals (produtorDatos.pu1A.getInvestigador(),     p.getInvestigador());


    	log.info("");	
		log.info("Probando recuperacion por nombre INEXISTENTE --------------------------------------------------");
    	
    	p = publiDao.recuperaPorNombre("iwbvyhuebvuwebvi");
    	Assert.assertNull (p);

    } 	

    @Test 
    public void test02_Alta() {


    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

		produtorDatos.creaPublicacionesSueltas();
		produtorDatos.creaInvestigadoresSueltos();
    	produtorDatos.registrarInvestigadores();

    	log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba da grabación de publicaciones sueltas\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Primeira publicación vinculada a un investigador\n"
    			+ "\t\t\t\t b) Nueva publicación para un investigador con publicaciones previas\n");     	

    	// Situación de partida:
    	// i1 desligado    	
    	// pu1A, pu1B transitorios

    	produtorDatos.i1.addPublicacion(produtorDatos.pu1A);
		
    	log.info("");	
		log.info("Grabando primera publicación de un investigador --------------------------------------------------------------------");
    	Assert.assertNull(produtorDatos.pu1A.getId());
    	publiDao.almacena(produtorDatos.pu1A);
    	Assert.assertNotNull(produtorDatos.pu1A.getId());

    	produtorDatos.i1.addPublicacion(produtorDatos.pu1B);

    	log.info("");	
		log.info("Grabando segunda publicación de un investigador ---------------------------------------------------------------------");
    	Assert.assertNull(produtorDatos.pu1B.getId());
    	publiDao.almacena(produtorDatos.pu1B);
    	Assert.assertNotNull(produtorDatos.pu1B.getId());

    }

    @Test 
    public void test03_Eliminacion() {
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

    	produtorDatos.creaInvestigadoresConPublicaciones();
    	produtorDatos.registrarInvestigadores();

    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba de eliminación de publicación suelta (asignada a investigador)\n");
    	
    	// Situación de partida:
    	// pu1A desligado

		Assert.assertNotNull(publiDao.recuperaPorNombre(produtorDatos.pu1A.getNombre()));
    	publiDao.elimina(produtorDatos.pu1A);    	
		Assert.assertNull(publiDao.recuperaPorNombre(produtorDatos.pu1A.getNombre()));

    } 	
    
    @Test 
    public void test04_Modificacion() {

    	Publicacion p1, p2;
    	String nuevaRevista;
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");
  
		produtorDatos.creaInvestigadoresConPublicaciones();
    	produtorDatos.registrarInvestigadores();

    	log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba de modificación de la información de una publicación suelta\n");
 
    	
    	// Situación de partida:
    	// pu1A desligado
    	
		nuevaRevista = new String ("Nombre revista nuevo");


		p1 = publiDao.recuperaPorNombre(produtorDatos.pu1A.getNombre());
		Assert.assertNotEquals(nuevaRevista, p1.getRevista());
    	p1.setRevista(nuevaRevista);

    	publiDao.modifica(p1);  
    
        p2 = publiDao.recuperaPorNombre(produtorDatos.pu1A.getNombre());
		Assert.assertEquals(nuevaRevista, p2.getRevista());

    	// NOTA: No probamos modificación de investigador de la publicación entrada porque no tiene sentido en el dominio considerado

    } 	
    
    @Test 
    public void test05_Propagacion_Persist() {

   	    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

		produtorDatos.creaInvestigadoresSueltos();
    	produtorDatos.creaPublicacionesSueltas();
    	produtorDatos.i1.addPublicacion(produtorDatos.pu1A);
    	produtorDatos.i1.addPublicacion(produtorDatos.pu1B);


    	log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba da grabación de nuevo investigador con publicaciones (nuevas) asociadas\n");   

    	// Situación de partida:
    	// i1, pu1A, pu1B transitorios

    	Assert.assertNull(produtorDatos.i1.getId());
    	Assert.assertNull(produtorDatos.pu1A.getId());
    	Assert.assertNull(produtorDatos.pu1B.getId());
    	
		log.info("Grabando en la BD investigador con publicaciones----------------------------------------------------------------------");

    	// Aquí el persist sobre i1 debe propagarse a pu1A y pu1B
		invDao.almacena(produtorDatos.i1);

		Assert.assertNotNull(produtorDatos.i1.getId());
    	Assert.assertNotNull(produtorDatos.pu1A.getId());
    	Assert.assertNotNull(produtorDatos.pu1B.getId());    	
    }

    @Test 
    public void test06_Propagacion_Remove() {
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");
   	
    	produtorDatos.creaInvestigadoresConPublicaciones();
    	produtorDatos.registrarInvestigadores();

    	log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba de eliminación de investigador con publicaciones asociadas\n");

    	// Situación de partida:
    	// i1, pu1A, pu1B desligados

    	Assert.assertNotNull(invDao.recuperaPorDni(produtorDatos.i1.getDni()));
		Assert.assertNotNull(publiDao.recuperaPorNombre(produtorDatos.pu1A.getNombre()));
		Assert.assertNotNull(publiDao.recuperaPorNombre(produtorDatos.pu1B.getNombre()));
		
		// Aquí el remove sobre i1 debe propagarse a pu1A y pu1B
		invDao.elimina(produtorDatos.i1);    	
		
		Assert.assertNull(invDao.recuperaPorDni(produtorDatos.i1.getDni()));
		Assert.assertNull(publiDao.recuperaPorNombre(produtorDatos.pu1A.getNombre()));
		Assert.assertNull(publiDao.recuperaPorNombre(produtorDatos.pu1B.getNombre()));

    } 	

    @Test 
    public void test07_EAGER() {
    	
    	Publicacion p;
    	Boolean excepcion;
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

		produtorDatos.creaInvestigadoresConPublicaciones();
    	produtorDatos.registrarInvestigadores();

		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba da recuperación de propiedades EAGER\n");   

    	// Situación de partida:
    	// i1, pu1A, pu1B desligados
    	
		log.info("Probando (que no hay excepcion tras) acceso inicial a la propiedad EAGER fuera de sesion ----------------------------------------");
    	
    	p = publiDao.recuperaPorNombre(produtorDatos.pu1A.getNombre());  
		log.info("Acceso a investigador de publicación");
    	try	{
        	Assert.assertEquals(produtorDatos.i1, p.getInvestigador());
        	excepcion=false;
    	} catch (LazyInitializationException ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	};    	
    	Assert.assertFalse(excepcion);    
    } 	

    @Test 
    public void test08_LAZY() {
    	
    	Investigador i;
    	Publicacion p;
    	Boolean excepcion;
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

		produtorDatos.creaInvestigadoresConPublicaciones();
    	produtorDatos.registrarInvestigadores();

		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba da recuperación de propiedades LAZY\n"   
		+ "\t\t\t\t Casos contemplados:\n"
		+ "\t\t\t\t a) Recuperación de investigador con colección (LAZY) de publicaciones \n"
		+ "\t\t\t\t b) Carga forzada de colección LAZY de la dicha coleccion\n"     	
		+ "\t\t\t\t c) Recuperacion de publicación suelta con referencia (EAGER) a investigador\n");     	

    	// Situación de partida:
    	// i1, pu1A, pu1B desligados
    	
		log.info("Probando (excepcion tras) recuperacion LAZY ---------------------------------------------------------------------");
    	
    	i = invDao.recuperaPorDni(produtorDatos.i1.getDni());
		log.info("Acceso a publicación de investigador");
    	try	{
        	Assert.assertEquals(2, i.getPublicaciones().size());
        	Assert.assertEquals(produtorDatos.pu1A, i.getPublicaciones().first());
        	Assert.assertEquals(produtorDatos.pu1B, i.getPublicaciones().last());	
        	excepcion=false;
    	} catch (LazyInitializationException ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	};    	
    	Assert.assertTrue(excepcion);
    
    	log.info("");
    	log.info("Probando carga forzada de coleccion LAZY ------------------------------------------------------------------------");
    	
    	i = invDao.recuperaPorDni(produtorDatos.i1.getDni());   // Invesitgador i con proxy sin inicializar
    	i = invDao.restauraPublicaciones(i);						// Investigador i con proxy ya inicializado
    	
    	Assert.assertEquals(2, i.getPublicaciones().size());
    	Assert.assertEquals(produtorDatos.pu1A, i.getPublicaciones().first());
    	Assert.assertEquals(produtorDatos.pu1B, i.getPublicaciones().last());

    	log.info("");
    	log.info("Probando acceso a referencia EAGER ------------------------------------------------------------------------------");
    
    	p = publiDao.recuperaPorNombre(produtorDatos.pu1A.getNombre());
    	Assert.assertEquals(produtorDatos.i1, p.getInvestigador());
    } 	

    @Test
    public void test09_Excepcions() {
    	
    	Boolean excepcion;
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

		produtorDatos.creaInvestigadoresSueltos();
		produtorDatos.registrarInvestigadores();
		produtorDatos.creaPublicacionesSueltas();		
		produtorDatos.i1.addPublicacion(produtorDatos.pu1A);		
		publiDao.almacena(produtorDatos.pu1A);
		
    	log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Prueba de violacion de restriciones not null y unique\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Grabación de publicacion con investigador nulo\n"
    			+ "\t\t\t\t b) Grabación de publicacion con nombre nulo\n"
    			+ "\t\t\t\t c) Grabación de publicacion con nombre duplicado\n");
    	
		log.info("Probando grabación de publicacion con investigador nulo ------------------------------------------------------------------");
    	try {
    		publiDao.almacena(produtorDatos.pu1B);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);

    	// Ligar publicación a investigador para poder probar otros errores
		produtorDatos.i1.addPublicacion(produtorDatos.pu1B);
    	    	
    	log.info("");	
		log.info("Probando grabación de pulicacion con nombre nulo -------------------------------------------------------------------");
		produtorDatos.pu1B.setNombre(null);
    	try {
        	publiDao.almacena(produtorDatos.pu1B);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);

    	log.info("");	
		log.info("Probando grabación de publicacion con nombre duplicado --------------------------------------------------------------");
		produtorDatos.pu1B.setNombre(produtorDatos.pu1A.getNombre());
    	try {
        	publiDao.almacena(produtorDatos.pu1B);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);
    }

}
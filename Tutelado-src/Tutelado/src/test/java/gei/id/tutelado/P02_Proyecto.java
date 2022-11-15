package gei.id.tutelado;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.configuracion.ConfiguracionJPA;
import gei.id.tutelado.dao.InvestigadorDao;
import gei.id.tutelado.dao.InvestigadorDaoJPA;
import gei.id.tutelado.dao.ProyectoDao;
import gei.id.tutelado.dao.ProyectoDaoJPA;
import gei.id.tutelado.model.Investigador;
import gei.id.tutelado.model.Nuevo;
import gei.id.tutelado.model.Proyecto;
import gei.id.tutelado.model.Revision;

//import org.apache.log4j.Logger;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class P02_Proyecto {

    private Logger log = LogManager.getLogger("gei.id.tutelado");

    private static ProdutorDatosProba produtorDatos = new ProdutorDatosProba();
    
    private static Configuracion cfg;
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

	
    	proDao = new ProyectoDaoJPA();
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
		log.info("Limpiando BD --------------------------------------------------------------------------------------------");
		produtorDatos.limpaBD();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test 
    public void test01_Recuperacion() {

        Nuevo n, inex;
    	Revision r;
    	
    	log.info("");
        log.info("Configurando situación de partida do test ----------------------------------------------------------------------");
        
        // Crea p0, p1, pSinPruebas
        produtorDatos.creaNuevosSueltos();
        // Crea s0, s1
        produtorDatos.creaRevisionesSueltos();
        // Rexistra creacións na bd
        produtorDatos.registrarProyectos();
        
        log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba de recuperación desde a BD de proyectos (sen entradas asociadas) e sanitarios por dni\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Recuperación por nombre existente\n"
    			+ "\t\t\t\t b) Recuperacion por nombre inexistente\n");
    	
    	
    	log.info("Probando nombre existente (Nuevo)\n");
    	
    	// Situación de partida:
    	// p0 desligado
    	n = (Nuevo) proDao.recuperaPorNombre(produtorDatos.p0.getNombre());
        Assert.assertEquals(produtorDatos.i0.getNombre(),       n.getNombre());
    	Assert.assertEquals(produtorDatos.i0.getfechaInicio(),  n.getfechaInicio());
    	Assert.assertEquals(produtorDatos.i0.getfechaFin(),     n.getApellidos());
    	Assert.assertEquals(produtorDatos.i0.getPresupuesto(),  n.getPresupuesto());
    	
    	log.info("Probando Nombre existente (Revision)\n");

    	// Situación de partida:
    	// s0 desligado
    	r = (Revision) proDao.recuperaPorDni(produtorDatos.s0.getDni());
        Assert.assertEquals(produtorDatos.i0.getNombre(),       r.getNombre());
    	Assert.assertEquals(produtorDatos.i0.getfechaInicio(),  r.getfechaInicio());
    	Assert.assertEquals(produtorDatos.i0.getfechaFin(),     r.getApellidos());
        Assert.assertEquals(produtorDatos.i0.getMotivo(),       r.getPresupuesto());

    	log.info("Probando Nombre inexistente\n");
    	inex = (Proyecto) proDao.recuperaPorNombre("noexiste");
    	Assert.assertNull(inex);
    } 	

    @Test 
    public void test02_Alta() {

    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");
  
		produtorDatos.creaInvestigadoresSueltos();
    	
    	log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba de grabación en la BD de nuevo investigador (sin publicaciones asociadas)\n");
    	
    	// Situación de partida:
    	// i0 transitorio    	
    	
    	Assert.assertNull(produtorDatos.i0.getId());
    	invDao.almacena(produtorDatos.i0);    	
    	Assert.assertNotNull(produtorDatos.i0.getId());

        // i1 transitorio   (?)
        Assert.assertNull(produtorDatos.i1.getId());
    	invDao.almacena(produtorDatos.i1);    	
    	Assert.assertNotNull(produtorDatos.i1.getId());	
    }

    @Test 
    public void test03_Eliminacion() {
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

		produtorDatos.creaInvestigadoresSueltos();
    	produtorDatos.registrarInvestigadores();

    	
    	log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba de eliminación de la BD de investigador sin publicaciones asociadas\n");   
 
    	// Situación de partida:
    	// u0 desligado  

    	Assert.assertNotNull(invDao.recuperaPorDni(produtorDatos.i0.getDni()));
    	invDao.elimina(produtorDatos.i0);    	
    	Assert.assertNull(invDao.recuperaPorDni(produtorDatos.i0.getDni()));
    } 	

    @Test 
    public void test04_Modificacion() {
    	
    	Investigador i1, i2;
    	String nuevoNombre;
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

		produtorDatos.creaInvestigadoresSueltos();
    	produtorDatos.registrarInvestigadores();

    	log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba de modificación de la información básica de un investigador sin publicaciones\n");

    	// Situación de partida:
    	// u0 desligado  

		nuevoNombre = new String ("Nombre nuevo");

		i1 = invDao.recuperaPorDni(produtorDatos.i0.getDni());
		Assert.assertNotEquals(nuevoNombre, i1.getNombre());
    	i1.setNombre(nuevoNombre);

    	invDao.modifica(i1);    	
    	
		i2 = invDao.recuperaPorDni(produtorDatos.i0.getDni());
		Assert.assertEquals (nuevoNombre, i2.getNombre());
    } 	

    @Test
    public void test09_Excepcions() {
    	
    	Boolean excepcion;
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

		produtorDatos.creaInvestigadoresSueltos();
    	invDao.almacena(produtorDatos.i0);
    	
    	log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba de violación de restricións not null y unique\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Grabación de usuario con dni duplicado\n"
    			+ "\t\t\t\t b) Grabación de usuario con dni nulo\n");

    	// Situación de partida:
    	// u0 desligado, u1 transitorio
    	
		log.info("Probando grabación de usuario con Dni duplicado -----------------------------------------------");
    	produtorDatos.i1.setDni(produtorDatos.i0.getDni());
    	try {
        	invDao.almacena(produtorDatos.i1);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);
    	
    	// Dni nulo
    	log.info("");	
		log.info("Probando grabación de usuario con Dni nulo ----------------------------------------------------");
    	produtorDatos.i1.setDni(null);
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

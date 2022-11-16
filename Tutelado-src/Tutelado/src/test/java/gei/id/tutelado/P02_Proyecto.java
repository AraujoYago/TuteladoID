package gei.id.tutelado;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.configuracion.ConfiguracionJPA;
import gei.id.tutelado.dao.ProyectoDao;
import gei.id.tutelado.dao.ProyectoDaoJPA;
import gei.id.tutelado.model.Nuevo;
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
        
        // Crea n0, n1
        produtorDatos.creaNuevosSueltos();
        // Crea r0, r1
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
    	// n0 desligado
    	n = (Nuevo) proDao.recuperaPorNombre(produtorDatos.n0.getNombre());
        Assert.assertEquals(produtorDatos.n0.getNombre(),       n.getNombre());
    	Assert.assertEquals(produtorDatos.n0.getFechaInicio(),  n.getFechaInicio());
    	Assert.assertEquals(produtorDatos.n0.getFechaFin(),     n.getFechaFin());
    	Assert.assertEquals(produtorDatos.n0.getPresupuesto(),  n.getPresupuesto(), 0.0f);
    	
    	log.info("Probando Nombre existente (Revision)\n");

    	// Situación de partida:
    	// r0 desligado
    	r = (Revision) proDao.recuperaPorNombre(produtorDatos.r0.getNombre());
        Assert.assertEquals(produtorDatos.r0.getNombre(),       r.getNombre());
    	Assert.assertEquals(produtorDatos.r0.getFechaInicio(),  r.getFechaInicio());
    	Assert.assertEquals(produtorDatos.r0.getFechaFin(),     r.getFechaFin());
        Assert.assertEquals(produtorDatos.r0.getMotivo(),       r.getMotivo());

    	log.info("Probando Nombre inexistente\n");
    	inex = (Nuevo) proDao.recuperaPorNombre("noexiste");
    	Assert.assertNull(inex);
    } 	

    @Test 
    public void test02_Alta() {

    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");
  
		// Crea n0, n1
        produtorDatos.creaNuevosSueltos();
        // Crea r0, r1
        produtorDatos.creaRevisionesSueltos();
    	
    	log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba de grabación en la BD de un nuevos proyectos (nuevos y de revisión)\n");
    	
    	log.info("Probando creación de Paciente 0\n");

        // Situación de partida:
        // n0 transitorio
        Assert.assertNull(produtorDatos.n0.getId());
        proDao.almacena(produtorDatos.n0);
        Assert.assertNotNull(produtorDatos.n0.getId());
        
        log.info("Probando creación de Paciente 1\n");

        // Situación de partida:
        // n1 transitorio
        Assert.assertNull(produtorDatos.n1.getId());
        proDao.almacena(produtorDatos.n1);
        Assert.assertNotNull(produtorDatos.n1.getId());
        
        log.info("Probando creación de Sanitario 0\n");
        
        // Situación de partida:
        // r0 transitorio
        Assert.assertNull(produtorDatos.r0.getId());
        proDao.almacena(produtorDatos.r0);
        Assert.assertNotNull(produtorDatos.r0.getId());
        
        log.info("Probando creación de Sanitario 1\n");

        // Situación de partida:
        // r1 transitorio
        Assert.assertNull(produtorDatos.r1.getId());
        proDao.almacena(produtorDatos.r1);
        Assert.assertNotNull(produtorDatos.r1.getId());
    }

    @Test 
    public void test03_Eliminacion() {
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

		// Crea n0, n1
        produtorDatos.creaNuevosSueltos();
        // Crea r0, r1
        produtorDatos.creaRevisionesSueltos();
        // Rexistra creacións na bd
        produtorDatos.registrarProyectos();

    	
    	log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba de eliminación de la BD de proyectos\n");   
 
    	log.info("Probando borrado proyecto nuevo\n");
    	
    	// Situación de partida
    	// n0 desligado
    	Assert.assertNotNull(proDao.recuperaPorNombre(produtorDatos.n0.getNombre()));
    	proDao.elimina(produtorDatos.n0);
    	Assert.assertNull(proDao.recuperaPorNombre(produtorDatos.n0.getNombre()));
    	
    	log.info("Probando borrado poryecto de revisión\n");
    	
    	// Situación de partida
    	// r0 desligado
    	Assert.assertNotNull(proDao.recuperaPorNombre(produtorDatos.r0.getNombre()));
    	proDao.elimina(produtorDatos.r0);
    	Assert.assertNull(proDao.recuperaPorNombre(produtorDatos.r0.getNombre()));
    } 	

    @Test 
    public void test04_Modificacion() {
    	
    	float presupuesto = 500.0f;
    	String motivo = "Nuevo Motivo";
    	Nuevo n, nMod;
    	Revision r, rMod;
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

		// Crea n0, n1
        produtorDatos.creaNuevosSueltos();
        // Crea r0, r1
        produtorDatos.creaRevisionesSueltos();
        // Rexistra creacións na bd
        produtorDatos.registrarProyectos();

    	log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba de modificación de la información básica de un proyecto (nuevo y de revisión)\n");

    	log.info("Probando modificación de proyecto nuevo 0\n");
    	
    	// Situación de partida
    	// n0 desligado
    	n = (Nuevo) proDao.recuperaPorNombre(produtorDatos.n0.getNombre());
    	Assert.assertNotEquals(presupuesto, n.getPresupuesto());
    	n.setPresupuesto(presupuesto);
    	proDao.modifica(n);
    	nMod = (Nuevo) proDao.recuperaPorNombre(produtorDatos.n0.getNombre());
    	Assert.assertEquals(presupuesto, nMod.getPresupuesto(), 0.0f);
    	
    	log.info("Probando modificación de proyecto de revisión 0\n");
    	
    	// Situación de partida
    	// r0 desligado
    	r = (Revision) proDao.recuperaPorNombre(produtorDatos.r0.getNombre());
    	Assert.assertNotEquals(motivo, r.getMotivo());
    	r.setMotivo(motivo);
    	proDao.modifica(r);
    	rMod = (Revision) proDao.recuperaPorNombre(produtorDatos.r0.getNombre());
    	Assert.assertEquals(motivo, rMod.getMotivo());
    	
    } 	

    @Test
    public void test05_Excepcions() {
    	
    	Boolean excepcion;
    	
    	log.info("");	
		log.info("Configurando situación de partida del test -----------------------------------------------------------------------");

		// Crea n0, n1
        produtorDatos.creaNuevosSueltos();
        
    	proDao.almacena(produtorDatos.n0);
    	
    	log.info("");	
		log.info("Inicio del test --------------------------------------------------------------------------------------------------");
    	log.info("Objetivo: Prueba de violación de restricións not null y unique\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Grabación de proyecto con nombre duplicado\n"
    			+ "\t\t\t\t b) Grabación de proyecto con nombre nulo\n");

    	// Situación de partida:
    	// n0 desligado, n1 transitorio
    	
		log.info("Probando grabación de proyecto con nombre duplicado -----------------------------------------------");
		
    	produtorDatos.n1.setNombre(produtorDatos.n0.getNombre());
    	try {
        	proDao.almacena(produtorDatos.n1);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);
    	
    	// Dni nulo
    	log.info("");	
		log.info("Probando grabación de proyecto con nombre nulo ----------------------------------------------------");
    	produtorDatos.n1.setNombre(null);
    	try {
        	proDao.almacena(produtorDatos.n1);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);
    } 	
}

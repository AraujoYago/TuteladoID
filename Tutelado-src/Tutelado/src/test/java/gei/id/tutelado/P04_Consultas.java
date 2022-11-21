package gei.id.tutelado;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.configuracion.ConfiguracionJPA;
import gei.id.tutelado.dao.InvestigadorDao;
import gei.id.tutelado.dao.InvestigadorDaoJPA;
import gei.id.tutelado.dao.ProyectoDao;
import gei.id.tutelado.dao.ProyectoDaoJPA;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class P04_Consultas {
	
	private Logger log = LogManager.getLogger("gei.id.tutelado");

    private static ProdutorDatosProba produtorDatos = new ProdutorDatosProba();

    private static Configuracion cfg;
    private static InvestigadorDao invDao;
    private static PublicacionDao publiDao;
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
        invDao.setup(cfg);
        
        publiDao = new PublicacionDaoJPA();
        publiDao.setup(cfg);
        
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
        log.info("Limpando a BD ----------------------------------------------------------------------------------------");
        produtorDatos.limpaBD();
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void test01_RecuperaPublicacionesPorDni() {
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
		
		// Crea i1, i2, pu1A, pu1B
		produtorDatos.creaInvestigadoresConPublicaciones();
		// Rexistra creacións na bd
		produtorDatos.registrarInvestigadores();
		
		log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba da consulta Investigador.recuperaPublicacionesInvestigadorPorDni\n");
    	
    	List<Publicacion> queryResult = new ArrayList<Publicacion>();
    	
    	queryResult = invDao.recuperaPublicacionesInvestigadorPorDni(produtorDatos.i1.getDni());
    	Assert.assertEquals(2, queryResult.size());
    	Assert.assertEquals(true, queryResult.contains(produtorDatos.pu1A));
    	Assert.assertEquals(true, queryResult.contains(produtorDatos.pu1B));
    }
    
    @Test
    public void test02_RecuperaInvestigadoresSinPublicaciones() {
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
		
		// Crea i1, i2, pu1A, pu1B
		produtorDatos.creaInvestigadoresConPublicaciones();
		// Rexistra creacións na bd
		produtorDatos.registrarInvestigadores();
		
		log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba da consulta Investigador.recuperaInvestigadorSinPublicaciones\n");
    	
    	List<Investigador> queryResult = new ArrayList<Investigador>();
    	queryResult = invDao.recuperaInvestigadorSinPublicaciones();
    	
    	Assert.assertEquals(1, queryResult.size());
    	Assert.assertEquals(true, queryResult.contains(produtorDatos.i0));
    }
    
    @Test
    public void test03_RecuperaCiudadMasInvestigadores() {
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
		
		// Crea i1, i2, pu1A, pu1B
		produtorDatos.creaInvestigadoresConPublicaciones();
		// Rexistra creacións na bd
		produtorDatos.registrarInvestigadores();
		
		log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba da consulta Investigador.recuperaCiudadMasInvestigador\n");
    	
    	Map<String, Integer> queryResult = new HashMap<String, Integer>();
    	queryResult = invDao.recuperaCiudadMasInvestigador();
    	Assert.assertEquals(2, queryResult.size());
    	Assert.assertEquals(1, queryResult.get("Pontevedra").intValue());
    	Assert.assertEquals(1, queryResult.get("A Coruña").intValue());
    }
    
    @Test
    public void test04_RecuperaNumeroPublicacionesPorRevista() {
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
		
		// Crea i1, i2, pu1A, pu1B
		produtorDatos.creaInvestigadoresConPublicaciones();
		// Rexistra creacións na bd
		produtorDatos.registrarInvestigadores();
		
		log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba da consulta Publicacion.recuperaNumeroPublicacionesRevista\n");
    	
    	Map<String, Integer> queryResult = new HashMap<String, Integer>();
    	queryResult = publiDao.recuperaNumeroPublicacionesRevista(produtorDatos.pu1A.getRevista());
    	Assert.assertEquals(1, queryResult.size());
    	Assert.assertEquals(1, queryResult.get("revista1").intValue());
    }
}

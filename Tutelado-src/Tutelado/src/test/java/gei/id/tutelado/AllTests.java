package gei.id.tutelado;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ P01_ClaveNatural.class, P02_Alta.class, P03_Eliminacion.class, P04_Modificacion.class, P05_Propagacion.class, P06_Consultas.class, P07_Eager.class, P08_Lazy.class} )
public class AllTests {

}

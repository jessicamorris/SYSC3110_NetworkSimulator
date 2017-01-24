package network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class RouterTest {

    private final String routerAName = "A";
    private final String routerBName = "B";

    private Router routerA;
    private Router routerB;

    @Before
    public void setUp() throws Exception {
	routerA = new Router(routerAName);
	routerB = new Router(routerBName);
    }

    @Test
    public void Router_RouterInit_NameSet() {
	assertEquals(routerAName, routerA.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void Router_RouterInit_IllegalName() {
	routerA = new Router("");
    }

    @Test
    public void Router_RouterInit_ConnectionsEmpty() {
	assertTrue(routerA.getConnections().isEmpty());
    }

    @Test
    public void Router_RouterConnect_DuplexConnection() {
	routerA.connectTo(routerB);
	assertTrue(routerA.getConnections().contains(routerB));
	assertTrue(routerB.getConnections().contains(routerA));
    }

}

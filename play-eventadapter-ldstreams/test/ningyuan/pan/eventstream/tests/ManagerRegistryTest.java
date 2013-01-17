package ningyuan.pan.eventstream.tests;

import static org.junit.Assert.*;

import ningyuan.pan.eventstream.ManagerRegistry;

import org.junit.Test;

public class ManagerRegistryTest {
	private ManagerRegistry inst = ManagerRegistry.getInstance();
	
	@Test
	public void testGetInstance() {
		ManagerRegistry inst2 = ManagerRegistry.getInstance();
		assertEquals("ManagerRegistry is not gloabally unqiue.", inst, inst2);
	}
    
	@Test(expected = IllegalArgumentException.class)
	public void testSetManager(){
		inst.setManager("testManager", null);
	}
}

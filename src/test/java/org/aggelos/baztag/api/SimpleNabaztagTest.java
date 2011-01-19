package org.aggelos.baztag.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import org.aggelos.baztag.api.inst.RetrieveInfoInstruction;
import org.aggelos.baztag.api.parts.Lang;
import org.aggelos.baztag.api.xml.ApiAnswer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleNabaztagTest {

	private static SimpleNabaztag tag;

	@BeforeClass
	public static void setUp() throws Exception {
		ResourceBundle props = ResourceBundle.getBundle("apitest");
		String sn = props.getString("test.api.sn");
		String token = props.getString("test.api.token");
		tag = new SimpleNabaztag(sn, token);
	}
	
	/**
	 * Introduce a delay between each test to not overload the API
	 */
	@Before
	public void pauseBetweenTest(){
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testSayString() {
		assertTrue(tag.say("N'importe quel message"));
	}

	@Test
	public void testSayStringString() {
		assertTrue(tag.say("My name is Billy",Lang.USBillye));
		//assertFalse(tag.say("My name is Billy","Roger"));
	}

	@Test
	public void testMoveLeftEar() {
		assertTrue(tag.moveLeftEar(5));
		assertFalse(tag.moveLeftEar(25));
	}

	@Test
	public void testMoveRightEar() {
		assertTrue(tag.moveRightEar(8));
		assertFalse(tag.moveRightEar(25));
	}
	
	@Test
	public void testSleep() {
		
		assertTrue(tag.sleep());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean update = tag
							.addInstruction(RetrieveInfoInstruction.SLEEPING_STATUS)
							.execute();
		if(!update) {
			for(ApiAnswer answer:tag.getLastErrors()) {
				System.out.println(answer.getMessage());
			}
		}
		assertTrue(update);		
		assertTrue("Ton Nabaztag devrait être endormi!!",!tag.isAwake());
			
	}
	
	@Test
	public void testAwake() {
		
		assertTrue(tag.awake());	
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean update = tag
							.addInstruction(RetrieveInfoInstruction.SLEEPING_STATUS)
							.execute();
							
		if(!update) {
			for(ApiAnswer answer:tag.getLastErrors()) {
				System.out.println(answer.getMessage());
			}
		}
		assertTrue(update);
		assertTrue("Ton Nabaztag devrait être réveillé!!",tag.isAwake());
	}
	
	

}

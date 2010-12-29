package org.aggelos.baztag.api;

import static org.junit.Assert.*;

import java.util.ResourceBundle;

import org.aggelos.baztag.api.parts.Lang;
import org.junit.Before;
import org.junit.Test;

public class SimpleNabaztagTest {

	private SimpleNabaztag tag;

	@Before
	public void setUp() throws Exception {
		ResourceBundle props = ResourceBundle.getBundle("apitest");
		String sn = props.getString("test.api.sn");
		String token = props.getString("test.api.token");
		tag = new SimpleNabaztag(sn, token);
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
		assertTrue(tag.moveLeftEar(10));
		assertFalse(tag.moveLeftEar(25));
	}

	@Test
	public void testMoveRightEar() {
		assertTrue(tag.moveRightEar(10));
		assertFalse(tag.moveRightEar(25));
	}

}

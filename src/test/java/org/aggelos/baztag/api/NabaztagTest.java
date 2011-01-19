package org.aggelos.baztag.api;

import static org.aggelos.baztag.api.parts.NabaztagLed.LEFT;
import static org.aggelos.baztag.api.parts.NabaztagLed.MIDDLE;
import static org.aggelos.baztag.api.parts.NabaztagLed.RIGHT;
import static org.aggelos.baztag.api.parts.NabaztagLed.TOP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.aggelos.baztag.api.inst.LeftEarInstruction;
import org.aggelos.baztag.api.inst.RightEarInstruction;
import org.aggelos.baztag.api.inst.TextInstruction;
import org.aggelos.baztag.api.inst.VoiceInstruction;
import org.aggelos.baztag.api.inst.chor.ChoregraphyInstruction;
import org.aggelos.baztag.api.inst.chor.LedChoregraphyStep;
import org.aggelos.baztag.api.inst.streaming.OnlineStreamInstruction;
import org.aggelos.baztag.api.parts.Lang;
import org.aggelos.baztag.api.xml.ApiAnswer;
import org.aggelos.baztag.api.xml.ApiAnswers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class NabaztagTest{
	//Radio FIP - playing some Jazz
	private static String webradioTestUrl = "http://mp3.live.tv-radio.com/fip/all/fip-32k.mp3";
	
	private static Nabaztag tag;	
	
	@BeforeClass
	public static void setUp() throws Exception {
		ResourceBundle props = ResourceBundle.getBundle("apitest");
		String sn = props.getString("test.api.sn");
		String token = props.getString("test.api.token");
		tag = new Nabaztag(sn, token);
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
	public void testExecute() {
		TextInstruction ti = new TextInstruction("Ceci est un test unitaire!");
		TextInstruction ti2 = new TextInstruction("Le Java c'est quand même mieux que le PHP");
		ChoregraphyInstruction ci = new ChoregraphyInstruction(1);
		LeftEarInstruction lei = new LeftEarInstruction((short)10);
		RightEarInstruction rei = new RightEarInstruction((short)2);
		
		
		
		LedChoregraphyStep s1 = new LedChoregraphyStep(0, TOP, 255, 255, 255);
		LedChoregraphyStep s2 = new LedChoregraphyStep(0, LEFT, 162, 35, 204);
		LedChoregraphyStep s3 = new LedChoregraphyStep(0, MIDDLE, 162, 35, 204);
		LedChoregraphyStep s4 = new LedChoregraphyStep(0, RIGHT, 162, 35, 204);
		LedChoregraphyStep s5 = new LedChoregraphyStep(1, TOP, 0, 0, 0);
		LedChoregraphyStep s6 = new LedChoregraphyStep(2, LEFT, 0, 0, 0);
		LedChoregraphyStep s7 = new LedChoregraphyStep(3, MIDDLE, 0, 0, 0);
		LedChoregraphyStep s8 = new LedChoregraphyStep(4, RIGHT, 0, 0, 0);
		
		ci.addStep(s1);
		ci.addStep(s2);
		ci.addStep(s3);
		ci.addStep(s4);
		ci.addStep(s5);
		ci.addStep(s6);
		ci.addStep(s7);
		ci.addStep(s8);
		
		
		tag.addInstruction(ci);
		tag.addInstruction(rei);
		tag.addInstruction(lei);
		
		assertTrue(tag.execute());
		assertTrue("The sequence was not cleared", tag.getSequence().size() == 0);
		
		tag
			.addInstruction(ti)			
			.execute();
		
		tag
			.addInstruction(ti2)
			.addInstruction(new VoiceInstruction(Lang.FRArchibald))
			.execute();
		
		rei = new RightEarInstruction((short)24);
		tag.addInstruction(rei);		
		assertFalse(tag.execute());
		List<ApiAnswer> messages = tag.getLastErrors();
		assertEquals(1,messages.size());
		assertEquals(messages.get(0).getAnswertype(), ApiAnswers.EARPOSITIONNOTSENT);
		System.err.println(messages.get(0).getMessage());
		
	}
	
	@Test
	public void testUpdateStatus() {
		System.out.println("updating status");
		boolean update = tag.updateStatus();
		if(!update) {
			for(ApiAnswer answer:tag.getLastErrors()) {
				System.out.println(answer.getMessage());
			}
		}
		assertTrue(update);
		
		assertNotNull("name should not be null",tag.getName());
		System.out.println("name : "+tag.getName());
		assertNotNull("version should not be null",tag.getVersion());
		System.out.println("version : "+tag.getVersion());
		assertNotNull("signature should not be null",tag.getSignature());
		System.out.println("signature : "+tag.getSignature());
		
		System.out.println("ear positions : "+tag.getLeftEarPos()+" - "+tag.getRightEarPos());
		
		System.out.println("\nMessages : ");
		for(Message m : tag.getMessages()) {
			System.out.println("\t"+m.getDate()+" - "+m.getTitle()+" from "+m.getFrom()+" at "+m.getUrl());
		}
		
		System.out.println("friends : ");
		for(String friend:tag.getFriends()) {
			System.out.println("\t"+friend);
		}
	}
	
	@Test
	public void testPlay() {
		
		try {
			boolean ok = tag
				.addStream(new OnlineStreamInstruction(new URL(webradioTestUrl)))
				.play();
			assertTrue(ok);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

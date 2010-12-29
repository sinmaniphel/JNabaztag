package org.aggelos.baztag.api;

import java.util.List;
import java.util.ResourceBundle;

import org.aggelos.baztag.api.inst.LeftEarInstruction;
import org.aggelos.baztag.api.inst.RightEarInstruction;
import org.aggelos.baztag.api.inst.TextInstruction;
import org.aggelos.baztag.api.inst.chor.ChoregraphyInstruction;
import org.aggelos.baztag.api.inst.chor.LedChoregraphyStep;
import org.aggelos.baztag.api.xml.ApiAnswer;
import org.aggelos.baztag.api.xml.ApiAnswers;

import static org.aggelos.baztag.api.parts.NabaztagLed.*;

import junit.framework.TestCase;

public class NabaztagTest extends TestCase {

	private Nabaztag tag;
	
	
	@Override
	protected void setUp() throws Exception {
		ResourceBundle props = ResourceBundle.getBundle("apitest");
		String sn = props.getString("test.api.sn");
		String token = props.getString("test.api.token");
		tag = new Nabaztag(sn, token);
	}
	
	public void testExecute() {
		TextInstruction ti = new TextInstruction("je peux écrire un peu ce que je veux");
		TextInstruction ti2 = new TextInstruction("et même lui faire dire des bétises");
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
		
		
		NabaztagInstructionSequence seq = new NabaztagInstructionSequence();
		seq.add(ti);
		seq.add(ti2);
		seq.add(ci);
		seq.add(rei);
		seq.add(lei);
		
		assertTrue(tag.execute(seq));
		seq = new NabaztagInstructionSequence();
		rei = new RightEarInstruction((short)24);
		seq.add(rei);
		seq.add(lei);
		assertFalse(tag.execute(seq));
		List<ApiAnswer> messages = tag.getLastErrors();
		assertEquals(1,messages.size());
		assertEquals(messages.get(0).getAnswertype(), ApiAnswers.EARPOSITIONNOTSENT);
		System.err.println(messages.get(0).getMessage());
		
	}
	
	public void testUpdateStatus() {
		System.out.println("updating status");
		boolean update = tag.updateStatus();
		if(!update) {
			for(ApiAnswer answer:tag.getLastErrors()) {
				System.out.println(answer.getMessage());
			}
		}
		assertTrue(update);
		System.out.println("name : "+tag.getName());
		System.out.println("version : "+tag.getVersion());
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
	
	public void testAwake() {
		boolean update = tag.updateStatus();
		if(!update) {
			for(ApiAnswer answer:tag.getLastErrors()) {
				System.out.println(answer.getMessage());
			}
		}
		assertTrue(update);
		boolean awake = tag.isAwake();
		assertTrue(tag.setAwake(!awake));
	}

}

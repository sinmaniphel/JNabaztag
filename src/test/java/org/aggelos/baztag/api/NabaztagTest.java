package org.aggelos.baztag.api;

import org.aggelos.baztag.api.inst.LeftEarInstruction;
import org.aggelos.baztag.api.inst.RightEarInstruction;
import org.aggelos.baztag.api.inst.TextInstruction;
import org.aggelos.baztag.api.inst.chor.ChoregraphyInstruction;
import org.aggelos.baztag.api.inst.chor.LedChoregraphyStep;
import static org.aggelos.baztag.api.parts.NabaztagLed.*;

import junit.framework.TestCase;

public class NabaztagTest extends TestCase {

	public void testExecute() {
		TextInstruction ti = new TextInstruction("je peux écrire un peu ce que je veux");
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
		//seq.add(ti);
		seq.add(ci);
		seq.add(rei);
		seq.add(lei);
		
		Nabaztag tag = new Nabaztag("","" );
		
		tag.execute(seq);
		
	}

}

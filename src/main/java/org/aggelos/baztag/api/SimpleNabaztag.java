package org.aggelos.baztag.api;

import org.aggelos.baztag.api.inst.LeftEarInstruction;
import org.aggelos.baztag.api.inst.RightEarInstruction;
import org.aggelos.baztag.api.inst.TextInstruction;
import org.aggelos.baztag.api.inst.VoiceInstruction;
import org.aggelos.baztag.api.parts.Lang;


/**
 *  A simplified version of the Nabaztag. Here, no more instructions, simple operations, like Say or moveLeftEar
 * @author sinmaniphel
 *
 *
 */
public class SimpleNabaztag extends Nabaztag {

	/**
	 * See {@link Nabaztag}'s constructor
	 * @param serialNumber
	 * @param token
	 */
	public SimpleNabaztag(String serialNumber, String token) {
		super(serialNumber, token);
	}
	
	/**
	 * Will ask the Nabaztag to say the message passed in parameter, with default voice
	 * @param message
	 * @return true if the message was transmitted correctly, false otherwise
	 */
	public boolean say(String message) {
		NabaztagInstructionSequence seq = new NabaztagInstructionSequence();
		TextInstruction ti = new TextInstruction(message);
		seq.add(ti);
		return execute(seq);
	}
	
	/**
	 * Will ask the Nabaztag to say the message passed in parameter, with the voice passed in parameter
	 * use in coordination with the {@link Lang} interface
	 * Eg : say("myMessage",Lang.USBillye);
	 * @param message
	 * @param voice
	 * @return
	 */
	public boolean say(String message, String voice) {
		NabaztagInstructionSequence seq = new NabaztagInstructionSequence();
		TextInstruction ti = new TextInstruction(message);
		VoiceInstruction vi = new VoiceInstruction(voice);
		seq.add(ti);
		seq.add(vi);
		return execute(seq);
	}
	
	/**
	 * Will ask the Nabaztag to move the left ear to the specified position between 0 and 16, 0 being vertical
	 * @param position
	 * @return
	 */
	public boolean moveLeftEar(int position) {
		NabaztagInstructionSequence seq = new NabaztagInstructionSequence();
		LeftEarInstruction lei = new LeftEarInstruction((short)position);
		seq.add(lei);
		return execute(seq);
	}
	
	/**
	 * Will ask the Nabaztag to move the right ear to the specified position between 0 and 16, 0 being vertical
	 * @param position
	 * @return
	 */
	public boolean moveRightEar(int position) {
		NabaztagInstructionSequence seq = new NabaztagInstructionSequence();
		RightEarInstruction lei = new RightEarInstruction((short)position);
		seq.add(lei);
		return execute(seq);
	}

}

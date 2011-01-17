package org.aggelos.baztag.api;

import org.aggelos.baztag.api.inst.LeftEarInstruction;
import org.aggelos.baztag.api.inst.RightEarInstruction;
import org.aggelos.baztag.api.inst.TextInstruction;
import org.aggelos.baztag.api.inst.VoiceInstruction;
import org.aggelos.baztag.api.inst.WakeUpInstruction;
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
		return say(message, null);
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
		TextInstruction ti = new TextInstruction(message);
		addInstruction(ti);
		if(voice !=null)
			addInstruction(new VoiceInstruction(voice));
		
		return execute();
	}
	
	/**
	 * Will ask the Nabaztag to move the left ear to the specified position between 0 and 16, 0 being vertical
	 * @param position
	 * @return
	 */
	public boolean moveLeftEar(int position) {
		LeftEarInstruction lei = new LeftEarInstruction((short)position);
		addInstruction(lei);
		return execute();
	}
	
	/**
	 * Will ask the Nabaztag to move the right ear to the specified position between 0 and 16, 0 being vertical
	 * @param position
	 * @return
	 */
	public boolean moveRightEar(int position) {
		RightEarInstruction lei = new RightEarInstruction((short)position);
		addInstruction(lei);
		return execute();
	}
	
	/**
	 * Will shut down the nabaztag
	 * @return
	 * 		TRUE if the instruction was successfully sent
	 */
	public boolean sleep() {
		addInstruction(new WakeUpInstruction(false));	
		return this.execute();		
	}
	
	/**
	 * Will awake the nabaztag
	 * @return
	 * 		TRUE if the instruction was successfully sent
	 */
	public boolean awake() {
		addInstruction(new WakeUpInstruction(true));	
		return this.execute();
	}

}

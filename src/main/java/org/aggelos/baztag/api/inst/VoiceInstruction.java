package org.aggelos.baztag.api.inst;

import org.aggelos.baztag.api.parts.Lang;

/**
 * A class whose purpose is to add a voice selection to a text instruction. Should always be used with a text instruction
 * To be used in coordination with {@link Lang}
 * @author Sinmaniphel
 *
 */
public class VoiceInstruction extends AbstractInstruction {

	private String voice;
	
	
	
	public VoiceInstruction(String voice) {
		super();
		this.voice = voice;
		this.paramName = "voice";
	}



	public String getParamValue() {
		return voice;
	}

}

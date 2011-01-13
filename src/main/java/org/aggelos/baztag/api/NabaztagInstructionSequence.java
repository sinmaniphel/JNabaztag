package org.aggelos.baztag.api;

import java.util.HashSet;

/**
 * This class has two purposes : 
 * <ul>
 * <li>it is a subclass of {@link HashSet} and ensures that only one instruction of a kind is sent at a time</li>
 * <li>it will send simultaneously several operations (for example a choregraphy and a text to say</li>
 * </ul>
 * @author sinmaniphel
 *
 */
public class NabaztagInstructionSequence extends HashSet<NabaztagInstruction> {
	

	/**
	 * Will convert the instruction sequence in a string of URL parameters
	 * @return
	 */
	public String toParamUrl() {
		StringBuffer outBuf = new StringBuffer();
		for(NabaztagInstruction ni : this) {
			outBuf.append("&");
			outBuf.append(ni.getParamName());
			outBuf.append("=");
			outBuf.append(ni.getParamValue());
		}
		return outBuf.toString();
	}
	
	public NabaztagInstructionSequence addInstruction(NabaztagInstruction inst){
		super.add(inst);
		return this;
	}
	
}

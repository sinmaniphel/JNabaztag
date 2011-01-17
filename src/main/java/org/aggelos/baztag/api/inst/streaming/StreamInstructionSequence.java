package org.aggelos.baztag.api.inst.streaming;

import java.util.ArrayList;

import org.aggelos.baztag.api.StreamInstruction;

/**
 * This class has two purposes : 
 * <ul>
 * <li>it is a subclass of {@link ArrayList} to keep the initial order</li>
 * <li>it support the "playlist syntax" of the Stream API</li>
 * </ul>
 * @author tvibes
 *
 */
public class StreamInstructionSequence extends ArrayList<StreamInstruction> {
	
	private static final String API_PARAM_NAME = "urlList"; 

	/**
	 * Will convert the instruction sequence in a string of URL parameters
	 * @return
	 */
	public String toParamUrl() {
		StringBuffer outBuf = new StringBuffer();
		outBuf.append("&");
		outBuf.append(API_PARAM_NAME);
		outBuf.append("=");
		boolean first = false;
		for(StreamInstruction si : this) {
			if(first)
				outBuf.append("|");
			outBuf.append(si.getStreamLocation());
			first = true;
		}
		return outBuf.toString();
	}	
}

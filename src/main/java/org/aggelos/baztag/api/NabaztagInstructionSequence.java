package org.aggelos.baztag.api;

import java.util.HashSet;


public class NabaztagInstructionSequence extends HashSet<NabaztagInstruction> {

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
	
}

package org.aggelos.baztag.api.inst;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.aggelos.baztag.api.NabaztagInstruction;

public class TextInstruction extends AbstractInstruction implements NabaztagInstruction {

	private String message;

	
	
	public TextInstruction(String message) {
		super();
		this.paramName = "tts";
		this.message = message;
	}



	public String getParamValue() {
		try {
			return URLEncoder.encode(message, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
}

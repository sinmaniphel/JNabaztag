package org.aggelos.baztag.api;

/**
 * Each kind of instruction will translate for the HTTP GET API in a key value pair, URL encoded
 * @author sinmaniphel
 *
 */

public interface NabaztagInstruction {

	/**
	 * Will return the parameter name
	 * @return the parameter name
	 */
	public String getParamName();
	
	/**
	 * Will return the parameter value, URL encoded
	 * @return
	 */
	public String getParamValue();
	
}

/**
 * 
 */
package org.aggelos.baztag.api.inst;

/**
 * <p>An instruction that's aim is to retrieve ears positions.</p>
 * 
 * @author tvibes
 *
 */
public class UpdateEarsInstruction extends AbstractInstruction {
	

	/**
	 * 
	 */
	public UpdateEarsInstruction() {		
		this.paramName = "ears";
	}

	/* (non-Javadoc)
	 * @see org.aggelos.baztag.api.NabaztagInstruction#getParamValue()
	 */
	@Override
	public String getParamValue() {		
		return "ok";
	}

}

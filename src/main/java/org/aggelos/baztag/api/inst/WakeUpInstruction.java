/**
 * 
 */
package org.aggelos.baztag.api.inst;


/**
 * A class whose purpose is to awake or put on sleep your Nabaztag
 * @author tvibes
 *
 */
public class WakeUpInstruction extends AbstractInstruction {
	
	private boolean wakeUp = false;
	
	public WakeUpInstruction(boolean wakeUp){
		this.paramName = "action";
		this.wakeUp = wakeUp;
	}

	/* (non-Javadoc)
	 * @see org.aggelos.baztag.api.NabaztagInstruction#getParamValue()
	 */
	@Override
	public String getParamValue() {		
		return wakeUp ? "14" : "13";
	}

}

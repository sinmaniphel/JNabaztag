package org.aggelos.baztag.api.inst;

public class LeftEarInstruction extends EarInstruction {

	
	
	public LeftEarInstruction(short position) {
		super(position);
		this.paramName = "posleft";
	}

	public String getParamValue() {
		return ""+position;
	}

}

package org.aggelos.baztag.api.inst;

public class RightEarInstruction extends EarInstruction {

	
	
	public RightEarInstruction(short position) {
		super(position);
		this.paramName = "posright";
	}

	public String getParamValue() {
		return ""+position;
	}

}

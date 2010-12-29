package org.aggelos.baztag.api.inst.chor;

import java.util.LinkedList;

import org.aggelos.baztag.api.inst.AbstractInstruction;

public class ChoregraphyInstruction extends AbstractInstruction {

	private int tempo;
	private LinkedList<NabaztagChoregraphyStep> steps = new LinkedList<NabaztagChoregraphyStep>();
	
	@Override
	public String getParamValue() {
		StringBuffer buff = new StringBuffer(""+tempo);
		for(NabaztagChoregraphyStep step:steps) {
			buff.append(",");
			buff.append(step.getStringValue());
		}
		return buff.toString();
	}
	
	public ChoregraphyInstruction(int tempo) {
		this.paramName = "chor";
		this.tempo = tempo;
	}
	
	public void addStep(NabaztagChoregraphyStep step) {
		steps.add(step);
	}

}

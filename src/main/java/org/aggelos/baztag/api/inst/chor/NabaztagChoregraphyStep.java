package org.aggelos.baztag.api.inst.chor;

public abstract class NabaztagChoregraphyStep {

	private int time;
	public int getTime() {
		return time;
	}

	protected String type;
	
	public NabaztagChoregraphyStep(int time) {
		this.time = time;
	}
	
	abstract String getStringValue();
	
}

package org.aggelos.baztag.api.inst.chor;

public class LedChoregraphyStep extends NabaztagChoregraphyStep {

	
	
	public LedChoregraphyStep(int time, int light, int red, int green,
			int blue) {
		super(time);
		this.light = light;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	private int light;
	private int red;
	private int green;
	private int blue;
	
	private static final String VALUE_FORMAT="%1$d,led,%2$d,%3$d,%4$d,%5$d";
	
	@Override
	String getStringValue() {
		return String.format(VALUE_FORMAT, getTime(),light,red,green,blue);
	}

	

}

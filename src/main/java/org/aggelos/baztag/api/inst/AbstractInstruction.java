package org.aggelos.baztag.api.inst;

import org.aggelos.baztag.api.NabaztagInstruction;

public abstract class AbstractInstruction implements NabaztagInstruction {

	protected String paramName;
	
	public String getParamName() {
		return paramName;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj.getClass().equals(this.getClass());
	}
	
	@Override
	public int hashCode() {
		return paramName.hashCode();
	}

	
}

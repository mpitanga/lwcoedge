package br.edu.ufrj.lwcoedge.core.model.devices.simulation;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class EdgeNodeDevice extends AbstractDevice implements Serializable {

	private static final long serialVersionUID = 5131939590593920208L;

	public EdgeNodeDevice() {}

	@Override
	public Float getEnergy() {
		return this.getWatts();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Util.obj2json(this);
	}

}

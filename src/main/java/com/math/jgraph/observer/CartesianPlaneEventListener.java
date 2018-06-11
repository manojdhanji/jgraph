package com.math.jgraph.observer;
public interface CartesianPlaneEventListener extends Observer {
	//method to update the observer, used by subject
	public void areaCalculationUpdate(double[] areas);
	public void windowClosureUpdate();
}

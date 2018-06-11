package com.math.jgraph.ui;

import com.math.jgraph.function.FunctionForm;

public interface CartesianPlane extends Displayable{
	 void addFunctionForm(FunctionForm functionForm, boolean showDerivative, boolean calculateArea);
}

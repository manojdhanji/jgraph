package com.math.jgraph.observer;

import com.math.jgraph.function.FunctionForm;

public interface ExpressionBuilderEventListener extends Observer {
	//method to update the observer, used by subject
	void expressionListUpdate(FunctionForm functionForm);
}

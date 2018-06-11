package com.math.jgraph.expression;

import com.math.jgraph.constant.Constants;

public class SimpleTerm extends Term {
	
	private static final long serialVersionUID = 327029344418208722L;

	public SimpleTerm(char var){
		super(var);
	}
	public SimpleTerm(){
		this(Constants.VARIABLE_X);
	}
}

package com.math.jgraph.function;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.math.jgraph.constant.Constants;

public enum FunctionFormType{
	Functional(Constants.VARIABLE_X),
	Parametric(Constants.VARIABLE_S),
	Polar(Constants.VARIABLE_THETA);
	private char var;
	private FunctionFormType(char var){
		this.var = var;
	}
	public char getVar(){
		return this.var;
	}
	public static String[] names(){
		return Arrays
			  .stream(FunctionFormType.values())
			  .map(e->e.name())
			  .collect(Collectors.toList())
			  .toArray(new String[FunctionFormType.values().length]);
	}
}

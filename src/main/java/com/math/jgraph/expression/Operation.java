package com.math.jgraph.expression;

import com.math.jgraph.constant.Constants;

public enum Operation implements Stackable {
	
	Add(Constants.ADD),
	Multiply(Constants.MULTIPLY),
	Subtract(Constants.SUBTRACT),
	Divide(Constants.DIVIDE),
	None(Constants.SPACE);
	private char c;
	private Operation(char c){
		this.c=c;
	}
	public char operation(){
		return this.c;
	}
	/*public static void main(String[] args){
		System.out.print(Add.operation());
	}*/
	
}

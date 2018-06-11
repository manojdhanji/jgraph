package com.math.jgraph.function;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.math.jgraph.expression.Expression;

public class FunctionForm implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private double l;
	private double h;
	private Color[] color; 
	private char var;
	private FunctionFormType type;
	public char getVar(){return this.var;}
	private List<List<Expression>> listOfExpressionList = new ArrayList<List<Expression>>();
	public FunctionFormType getType(){
		return this.type;
	}
	public FunctionForm(FunctionFormType type){
		this.type=type;
		this.var=type.getVar();
	}
	public void add(List<Expression> expressionList){
		listOfExpressionList.add(expressionList);
	}
	public 	List<List<Expression>> getListOfExpressionList(){
		return this.listOfExpressionList;
	}
	public double getL(){
		return l;
	}
	public double getH(){
		return h;
	}
	public Color[] getColor(){
		return this.color;
	}
	public void setL(double l){
		this.l = l;
	}
	public void setH(double h){
		this.h = h;
	}
	public void setColor(Color[] color){
		this.color = color;
	}

}

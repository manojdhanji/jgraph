package com.math.jgraph.expression;

import com.math.jgraph.constant.Constants;
import com.math.jgraph.exception.EvaluationException;

/**
 * 
 * @author manoj dhanji
 * @since v1.0
 */
public abstract class Term implements Stackable, Expression{
	
	/**
	 * 
	 */
	protected char var;
	private static final long serialVersionUID = -4413642873210964038L;
	protected double power;
	protected double coefficient;
	public Term(char var){
		super();
		this.var = var;
		this.power=1.0;
		this.coefficient=1.0;
	}
	public void setCoefficient(double coefficient){
		this.coefficient=coefficient;
	}
	public void setPower(double power){
		this.power=power;
	}
	public double getCoefficient(){
		return this.coefficient;
	}
	public double getPower(){
		return this.power;
	}
	public String toString(){
		StringBuilder buff = new StringBuilder();
		
		String c = String.valueOf(coefficient);
		if(Double.compare(power, 0.0)==0){
			buff.append(c);
		}
		else{
			if(Double.compare(Math.abs(coefficient), 1.0)!=0){
				buff.append(c)
					.append(Constants.MULTIPLY);
			}
			else if(Double.compare(coefficient, -1.0)==0){
				buff.append(Operation.Subtract.operation());
			}
			if(Double.compare(power, 1.0)==0)
				buff.append(var);
			else if(Double.compare(power, 1.0)!=0){
				//buff.append("(x)^")
				buff.append(Constants.LEFT_PARENTHESES)
				    .append(var)
				    .append(Constants.RIGHT_PARENTHESES)
				    .append('^')
				    .append(power);
			}
		}
		return buff.toString();
	}
	public void display(){
		System.out.println(this.toString());
	}
	public double evaluate(double x)throws EvaluationException
	{
		return coefficient*Math.pow(x, power);
	}
}

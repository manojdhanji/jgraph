package com.math.jgraph.expression;
import java.text.MessageFormat;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Logger;

import com.math.jgraph.ThreadLocalContext;
import com.math.jgraph.constant.Constants;
import com.math.jgraph.exception.EvaluationException;
import com.math.jgraph.function.FunctionEnum;
public class ComplexTerm extends Term {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7285409640920274008L;
	static final Logger logger;
	static{
		DOMConfigurator.configure("log4j-graph.xml");
		logger = Logger.getLogger(ComplexTerm.class);
	}
	protected final List<Term> multiplier = new ArrayList<Term>();
	protected FunctionEnum function;
	protected final List<Term> input = new ArrayList<Term>();
	protected final List<Term> exponent = new ArrayList<Term>();
	public ComplexTerm(){
		this(Constants.VARIABLE_X);
	}
	public ComplexTerm(char var){
		super(var);
	}
	public void setFunction(FunctionEnum function){
		this.function=function;
	}
	public FunctionEnum getFunction(){
		return this.function;
	}

	public void addMultiplier(List<Term> multiplier){
		if(multiplier!=null && !multiplier.isEmpty())
			this.multiplier.addAll(multiplier);
	}
	public void addMultiplier(Term multiplier){
		if(multiplier!=null)
			this.multiplier.add(multiplier);
	}

	public void addInput(List<Term> input){
		if(input!=null && !input.isEmpty())
			this.input.addAll(input);
	}
	public void addInput(Term input){
		if(input!=null)
			this.input.add(input);
	}
	public void addExponent(List<Term> exponent){
		if(exponent!=null && !exponent.isEmpty())
			this.exponent.addAll(exponent);
	}
	public void addExponent(Term exponent){
		if(exponent!=null)
			this.exponent.add(exponent);
	}
	public List<Term> getExponent(){
		return this.exponent;
	}
	public List<Term> getInput(){
		return this.input;
	}
	public List<Term> getMultiplier(){
		return this.multiplier;
	}

	
	public String toString(){
		StringBuilder buff = new StringBuilder();
		if(Double.compare(Math.abs(coefficient), 1.0)!=0){
			buff.append(coefficient)
				.append(Constants.MULTIPLY);
		}
		else if(Double.compare(coefficient, -1.0)==0){
			buff.append(Operation.Subtract.operation());
		}
		if(!multiplier.isEmpty() ){
			for(Term t:multiplier){	
				buff.append(t)
					.append(Constants.MULTIPLY);
			}
		}
				
		if(Double.compare(power, 1.0)!=0){
			buff.append(Constants.LEFT_PARENTHESES);
		}
		
		if(function==FunctionEnum.pow){
			
			buff.append(ComplexTerm.sumOfTerms(this.input, this.var))
			    .append("^")
				.append(ComplexTerm.sumOfTerms(this.exponent,this.var));
		}
		else if(function==FunctionEnum.exp){
			buff.append("e^")
				.append(ComplexTerm.sumOfTerms(this.input,this.var));
		}
		else if(function==FunctionEnum.expm1){
			buff.append("e^")
				.append(ComplexTerm.sumOfTerms(this.input,this.var))
				.append(" -1 ");
		}
		else{
			buff.append(function.friendlyName())
			    .append(ComplexTerm.sumOfTerms(this.input,this.var));
		}
							
		if(Double.compare(power, 1.0)!=0){
			buff.append(Constants.RIGHT_PARENTHESES)
				.append("^")
				.append(this.power);	
		}
		return buff.toString();
	}
	
	public static String sumOfTerms(List<Term> list, char var){
		StringBuilder buffer = new StringBuilder();
		int i = 0;
		buffer.append(Constants.LEFT_PARENTHESES);
		if(!list.isEmpty()){
			
			for(i=0; i<list.size()-1;i++){
				buffer.append(list.get(i))
					  .append(Constants.SPACE)
					  .append(Operation.Add.operation())
					  .append(Constants.SPACE);
			}
			buffer.append(list.get(i));
			      
		}
		else{
			buffer.append(var);
		}
		buffer.append(Constants.RIGHT_PARENTHESES);
		return buffer.toString();
	}
	public double evaluate(double x)throws EvaluationException{
		
		Method method = function.method();
		try{
			double functionValue = 0.0;
			if(method.getName().equals("pow")){
				if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled())
					logger.debug(MessageFormat.format("ComplexTerm.evaluate x={0}; z={1}",x,ThreadLocalContext.getCurrent().getValue()));
				functionValue = ((Double) method.invoke(Math.class, x, ThreadLocalContext.getCurrent().getValue())).doubleValue();
			}
			else{
				if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled())
					logger.debug(MessageFormat.format("ComplexTerm.evaluate x={0}",x));
				functionValue = ((Double) method.invoke(Math.class, x)).doubleValue();
			}
			return super.evaluate(functionValue);
		}
		catch(Exception exp){
			logger.error(exp.getMessage());
			throw new EvaluationException("Cannot Evaluate - ExpressionImpl not found"); 
		}
	}
}

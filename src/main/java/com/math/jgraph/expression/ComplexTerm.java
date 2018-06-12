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
	protected final List<Term> multiplierList = new ArrayList<Term>();
	protected FunctionEnum function;
	protected final List<Term> argumentList = new ArrayList<Term>();
	protected final List<Term> exponentList = new ArrayList<Term>();
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

	public void addMultiplierList(List<Term> multiplierList){
		if(multiplierList!=null && 
				!multiplierList.isEmpty())
			this.multiplierList.addAll(multiplierList);
	}
	public void addMultiplier(Term multiplier){
		if(multiplier!=null)
			this.multiplierList.add(multiplier);
	}

	public void addArgumentList(List<Term> argumentList){
		if(argumentList!=null && !argumentList.isEmpty())
			this.argumentList.addAll(argumentList);
	}
	public void addArgument(Term argument){
		if(argument!=null)
			this.argumentList.add(argument);
	}
	public void addExponentList(List<Term> exponentList){
		if(exponentList!=null && 
				!exponentList.isEmpty())
			this.exponentList.addAll(exponentList);
	}
	public void addExponent(Term exponent){
		if(exponent!=null)
			this.exponentList.add(exponent);
	}
	public List<Term> getExponentList(){
		return this.exponentList;
	}
	public List<Term> getArgumentList(){
		return this.argumentList;
	}
	public List<Term> getMultiplierList(){
		return this.multiplierList;
	}

	@Override
	public String toString(){
		StringBuilder buff = new StringBuilder();
		if(Double.compare(Math.abs(coefficient), 1.0)!=0){
			buff.append(coefficient)
				.append(Constants.MULTIPLY);
		}
		else if(Double.compare(coefficient, -1.0)==0){
			buff.append(Operation.Subtract.operation());
		}
		if(!multiplierList.isEmpty() ){
			for(Term t:multiplierList){	
				buff.append(t)
					.append(Constants.MULTIPLY);
			}
		}
				
		if(Double.compare(exponent, 1.0)!=0){
			buff.append(Constants.LEFT_PARENTHESES);
		}
		
		if(function==FunctionEnum.pow){
			buff.append(ComplexTerm.sumOfTerms(this.argumentList, this.var))
			    .append("^")
				.append(ComplexTerm.sumOfTerms(this.exponentList,this.var));
		}
		else if(function==FunctionEnum.exp){
			buff.append("e^")
				.append(ComplexTerm.sumOfTerms(this.argumentList,this.var));
		}
		else if(function==FunctionEnum.expm1){
			buff.append("e^")
				.append(ComplexTerm.sumOfTerms(this.argumentList,this.var))
				.append(" -1 ");
		}
		else{
			buff.append(function.friendlyName())
			    .append(ComplexTerm.sumOfTerms(this.argumentList,this.var));
		}
							
		if(Double.compare(exponent, 1.0)!=0){
			buff.append(Constants.RIGHT_PARENTHESES)
				.append("^")
				.append(this.exponent);	
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
	
	@Override
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

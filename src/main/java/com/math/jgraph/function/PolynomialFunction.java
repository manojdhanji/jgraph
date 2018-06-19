package com.math.jgraph.function;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.math.jgraph.constant.Constants;
import com.math.jgraph.exception.EvaluationException;
import com.math.jgraph.expression.Expression;
public class PolynomialFunction implements Function{
	private static final Logger logger = LoggerFactory.getLogger(PolynomialFunction.class);
	protected List<Expression> pExpressions = new ArrayList<Expression>();
	public PolynomialFunction(){
		super();
	}
	
	public void addPExpression(Expression expression){
		pExpressions.add(expression);
	}
	public void setPExpressions(List<Expression> pExpressions){
		this.pExpressions = pExpressions;
	}
	@Override
	public double fx(double x){
		return this.evaluate(x,this.pExpressions);
	}
	protected double evaluate(double x, List<Expression> expressions){
		double y=0.0;

		for(Expression expression:expressions){
			try{
				y+=expression.evaluate(x);
			}
			catch(EvaluationException e){
				final StringBuilder builder = new StringBuilder();
				
				Arrays.stream(e.getStackTrace())
								.forEach(s->builder.append(s));
				logger.error(builder.toString());
			}
		}
        if(logger.isDebugEnabled() && Boolean.getBoolean(Constants.TEST))
			logger.debug(MessageFormat.format("PolynomialFunction: {0}",y));
		return y;
	}
}

package com.math.jgraph.function;
import java.util.ArrayList;
import java.util.List;

import com.math.jgraph.expression.Expression;
public class RationalFunction extends PolynomialFunction{
	
	
	protected List<Expression> qExpressions = new ArrayList<Expression>();
	@Override
	public double fx(double x){
		double y1 = this.evaluate(x, this.pExpressions);
		double y2 = this.evaluate(x, this.qExpressions);
		if(y2!=0){
			return y1/y2;
		}
		else
			throw new ArithmeticException();
	}
}

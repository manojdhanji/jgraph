package com.math.jgraph.expression;
import java.io.Serializable;

import com.math.jgraph.exception.EvaluationException;
public interface Expression extends Serializable
{
	double evaluate(double x)throws EvaluationException;
	void display();
}
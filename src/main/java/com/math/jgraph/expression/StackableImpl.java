package com.math.jgraph.expression;
public class StackableImpl implements Stackable 
{
	
	private static final long serialVersionUID = -8743777257135729048L;
	Marker mBegin;
	Marker mEnd;
	double value;
	Operation operation=Operation.None;
	public String toString(){
		StringBuilder b = new StringBuilder();
		b.append("Begin: ")
	     .append(mBegin!=null?mBegin.toString():"")
		 .append(" End: ")
		 .append(mEnd!=null?mEnd.toString():"")
		 .append(" Value: ")
		 .append(value)
		 .append(" Operation: ")
		 .append(operation.operation());
		return b.toString();

	}
}

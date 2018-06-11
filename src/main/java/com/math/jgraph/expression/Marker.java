package com.math.jgraph.expression;
public class Marker implements Stackable 
{
	
	private static final long serialVersionUID = -8547393990264325148L;
	Delimiter d;
	int a;
	public Marker(Delimiter d, int a){
		this.d=d;
		this.a=a;
	}
	
	public String toString(){
		return d.name()+a;
	}
	/*public static void main(String[] args){
		System.out.print(new Marker(Delimiter.X,1));
	}*/
}

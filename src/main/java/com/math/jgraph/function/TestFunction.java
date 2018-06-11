package com.math.jgraph.function;
public class TestFunction implements Function
{
	@Override
	public double fx(double x){
		//3.0cosh(sinh(2.0cos((x))))
		//return 3.0* Math.cosh(Math.sinh(2.0*Math.cos(x)));
		//return Math.pow( (Math.pow(5.0,2.0) - Math.pow(x, 2.0)), 0.5);
		//return 2*x;
		//return Math.sin(Math.cos(x));
		//ex(2.0cos((x))) + x + 3.0cosh((x))
		//return Math.exp(2.0*Math.cos(x)) + x + 3.0 * Math.cosh(x);
		//sin(cos((x)^2.0 + 2.0*x) + 2.0*x)
		//f(x)= cos((x)^2.0 + 1 + sin(cos((x)^2.0 + 1) + 1))

		return Math.cos(Math.pow(x, 2.0) + 1 + Math.sin(Math.cos(Math.pow(x,2.0)+1) + 1));
	}
	public static void main(String[] args){
		char[] c = new char[]{'\u00DC','\u00FC','\u00DB','\u00FB','\u00D9','\u00F9','\u00D6','\u00F6',
				              '\u00D4','\u00F4','\u00D1','\u00F1','\u00CF','\u00EF','\u00CE','\u00EE',
				              '\u00CB','\u00EB','\u00CA','\u00EA','\u00C8','\u00E8','\u00C9','\u00E9',
				              '\u00C7','\u00E7','\u00C4','\u00E4','\u00C2','\u00E2','\u00C0','\u00E0',
				              '\u00C1','\u00E1'};
		for(char _c:c){
			System.out.println(_c);
		}
	}
}

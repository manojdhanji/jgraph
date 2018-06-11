package com.math.jgraph.function;
import java.awt.geom.Line2D;
import java.math.BigDecimal;

import com.math.jgraph.constant.Constants;
public interface Function{
	abstract double  fx(double x);
	default Line2D.Double image(double x){
		double _y = Constants.SCALE_FACTOR*fx(x);
		if(Double.isNaN(_y) || 
			Double.isInfinite(_y))
			return null;
		
		double y=new BigDecimal(_y ).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
		x = Constants.SCALE_FACTOR * x;
		
	
		Line2D.Double p = new Line2D.Double(
			x+Constants.TRANSFORM_FACTOR,	
				Constants.TRANSFORM_FACTOR-y, 
					x+Constants.TRANSFORM_FACTOR,		
						Constants.TRANSFORM_FACTOR-y);
		return p;
	}
}

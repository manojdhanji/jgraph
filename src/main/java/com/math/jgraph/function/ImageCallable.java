package com.math.jgraph.function;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.awt.geom.Line2D;

import com.math.jgraph.constant.Constants;
public class ImageCallable implements Callable<Line2D.Double> {
	private double x;
	private List<Function> functionList;
	public ImageCallable(List<Function> functionList, double x) {
		this.x=x;
		this.functionList=functionList;
	}
	@Override
	public Line2D.Double call() {
		/*
		Function function = functionList.get(0);
		Line2D.Double _l = function.image(x);//x=f(s)
		if(_l!=null){
			if(functionList.size()==2){
			  Line2D.Double __l = functionList.get(1).image(x); //y=f(s)
			  	if(__l!=null){
					_l.x1=-_l.y1+2*Constants.TRANSFORM_FACTOR;
					_l.x2=-_l.y1+2*Constants.TRANSFORM_FACTOR;
					_l.y1=__l.y1;
					_l.y2=__l.y2;
				}
				else
				_l=__l;
			}
		}
		return _l;
		*/
		Line2D.Double ls1 = null;
		if(functionList!=null){
			Iterator<Function> iterator = functionList.iterator(); 
			Function function1 = iterator.next();
			Function function2 = null;
			if(iterator.hasNext()){
				function2 = iterator.next();
			}
			ls1 = function1.image(x);//x=f(s)
			if(function2!=null){
				Line2D.Double ls2 = function2.image(x);
				if(ls2!=null){
					if(ls1!=null){
						ls1.x1=-ls1.y1+2*Constants.TRANSFORM_FACTOR;
						ls1.x2=-ls1.y2+2*Constants.TRANSFORM_FACTOR;
						ls1.y1=ls2.y1;
						ls1.y2=ls2.y2;
					}
				}
				else
					ls1=ls2;
			}
		}
		return ls1;
	}
} 
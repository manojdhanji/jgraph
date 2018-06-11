package com.math.jgraph;

import com.math.jgraph.expression.Operation;
import com.math.jgraph.expression.Stackable;
import com.math.jgraph.ui.CartesianPlaneEvent;

public class ThreadLocalContext
{
	private double value;
	private Stackable stackable;
	private CartesianPlaneEvent event;
	private ThreadLocalContext(){}
	public void setEvent(CartesianPlaneEvent event){
		this.event = event;
	}
	public CartesianPlaneEvent getEvent(){
		return this.event;
	}
	public void setValue(double value){
		this.value = value;
	}
	public double getValue(){
		return this.value;
	}
	public void setStackable(Stackable stackable){
		this.stackable = stackable;
	}
	public Stackable getStackable(){
		return this.stackable;
	}
	public void reset(){
		this.value=0;
		this.stackable=null;
		this.event = null;
	}
	public void incrementValue(double value){
		updateValue(value, Operation.Add);
	}
	public void updateValue(double value, Operation o){
		switch(o)
		{
			case Add:
				this.value+=value;
				break;
			case Multiply:
				this.value*=value;
			default:

		}
	}
	private static final ThreadLocal<ThreadLocalContext> current = new ThreadLocal<ThreadLocalContext>(){
		@Override
        protected ThreadLocalContext initialValue()
        {
            return new ThreadLocalContext();
        }
    };

	public static ThreadLocalContext getCurrent()
	{
		return ThreadLocalContext.current.get();
	}
	
	/*public static void main(String[] args){
		ThreadLocalContext de = ThreadLocalContext.getCurrent();
		System.out.println("Get value: "+ de.getValue());
		de.setValue(-1.0);
		System.out.println("Get value: "+ de.getValue());
		de.incrementValue(0.00009);
		System.out.println("Get value: "+ de.getValue());
		Thread t = new Thread(){
			public void run(){
				ThreadLocalContext de = ThreadLocalContext.getCurrent();
				System.out.println("Get value: "+ de.getValue());
			}
		};
		t.start();
	}*/
}

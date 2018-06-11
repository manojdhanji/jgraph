package com.math.jgraph.expression;
import java.text.MessageFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Stack;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Logger;

import com.math.jgraph.ThreadLocalContext;
import com.math.jgraph.constant.Constants;
import com.math.jgraph.exception.EvaluationException;
public class ExpressionImpl implements Expression
{
	private static final long serialVersionUID = -5541766297442697847L;
	static final Logger logger;
	static{
		DOMConfigurator.configure("log4j-graph.xml");
		logger = Logger.getLogger(ExpressionImpl.class);
	}	
	private List<Stackable> stackables;
	private Stackable stackable;
	public Term getTerm(){
		return (Term)this.stackable;
	}
	
	public ExpressionImpl(final Stackable s){
		super();
		this.stackable=s;
		this.stackables=this.addToStack(stackable);
	}
	private List<Stackable> addToStack(Stackable s){
		List<Stackable> stackables = new ArrayList<Stackable>();
		addToStack(s,0,stackables);
		return stackables;
	}
	private void addToStack(Stackable s, int a, List<Stackable> stackables){
		if(s instanceof ComplexTerm){
			ComplexTerm c =(ComplexTerm)s;
			List<Term> input = c.getInput();
			List<Term> exponent = c.getExponent();
			if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled()){
				logger.debug(MessageFormat.format("ExpressionImpl.stack Input: {0}",input));
				logger.debug(MessageFormat.format("ExpressionImpl.stack: a: {0}",a));
				logger.debug(MessageFormat.format("ExpressionImpl.stack: ComplexTerm: {0}",c));
				logger.debug(MessageFormat.format("ExpressionImpl.stack Exponent: {0}",exponent));
			}
			//arg1
			++a;
			if(input.size()>0){
				addToStack(new Marker(Delimiter.X,a),a,stackables);
				int i=0;
				for(;i<input.size()-1;i++){
					addToStack((Stackable)input.get(i),a,stackables);
					addToStack(Operation.Add,a,stackables);
				}
				addToStack((Stackable)input.get(i),a,stackables);
				addToStack(new Marker(Delimiter._X,a),a,stackables);
			}
			//arg2
			if(exponent.size()>0){
				addToStack(new Marker(Delimiter.Y,a),a,stackables);
				int i=0;
				for(;i<exponent.size()-1;i++){
					addToStack((Stackable)exponent.get(i),a,stackables);
					addToStack(Operation.Add,a,stackables);
				}
				addToStack((Stackable)exponent.get(i),a,stackables);
				addToStack(new Marker(Delimiter._Y,a),a,stackables);
			}

			stackables.add(s);
			if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled()){
				logger.debug(MessageFormat.format("ExpressionImpl.stack: Added ComplexTerm {0}",s));
			}
		}
		else if(s instanceof Operation){
			stackables.add(s);
			if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled())
				logger.debug(MessageFormat.format("ExpressionImpl.stack: Added Operation {0}",s));
			return;
		}
		else if(s instanceof SimpleTerm){
			stackables.add(s);
			if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled())
				logger.debug(MessageFormat.format("ExpressionImpl.stack: Added SimpleTerm {0}",s));
			return;
		}
		else if(s instanceof Marker){
			stackables.add(s);
			if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled())
				logger.debug(MessageFormat.format("ExpressionImpl.stack: Added Marker {0}",s));
			return;
		}
		if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled())
			logger.debug(stackables);
	}
	@Override
	public double evaluate(double x)throws EvaluationException{
		List<Stackable> stackables=null;
		Stackable stackable = ThreadLocalContext.getCurrent().getStackable();
		if(stackable==null){
			stackables = this.stackables;
		}else{
			stackables = this.addToStack(stackable);
		}
		if(stackables.size()>0){
			double y = 0;
			ThreadLocalContext.getCurrent().reset();//reset for the every iteration
			ThreadLocalContext.getCurrent().setValue(x);
			boolean addOperationFound = false;
			boolean threadLocalSavingRequired=false, threadLocalSavingSupensionRequired=false;
			
			Stack<StackableImpl> s = new Stack<StackableImpl>();
			for(ListIterator<Stackable> iter = stackables.listIterator();iter.hasNext();){
				Stackable i = iter.next();
				if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled()){
					logger.debug(MessageFormat.format("Expression.evaluate: {0}={1}",i.getClass().getSimpleName(),i));
					logger.debug(MessageFormat.format("ExpressionImpl.evaluate: Stack<StackableImpl>={0}",s));
					logger.debug(MessageFormat.format("ExpressionImpl.evaluate: addOperationFound={0}",addOperationFound));
					logger.debug(MessageFormat.format("ExpressionImpl.evaluate: threadLocalSavingRequired={0}",threadLocalSavingRequired));
					logger.debug(MessageFormat.format("ExpressionImpl.evaluate: threadLocalSavingSupensionRequired={0}",threadLocalSavingSupensionRequired));
					logger.debug(MessageFormat.format("ExpressionImpl.evaluate: x={0}",x));
					logger.debug(MessageFormat.format("ExpressionImpl.evaluate: y={0}",y));
					logger.debug(MessageFormat.format("ExpressionImpl.evaluate: z={0}",ThreadLocalContext.getCurrent().getValue()));
				}
				if(i instanceof Operation){
					addOperationFound=true;
				}
				else if(i instanceof Marker){
					Marker mi = (Marker)i;
					if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled())
						logger.debug(MessageFormat.format("ExpressionImpl.evaluate: Marker {0}",mi));
					if(mi.d==Delimiter.Y){
						threadLocalSavingRequired=true;
					}
					if(mi.d==Delimiter.X && threadLocalSavingRequired ){
						threadLocalSavingSupensionRequired=true;
					}
					boolean pushRequired = false;
					if(!s.isEmpty()){
						StackableImpl stackableImpl = s.peek();
						if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled())
							logger.debug(MessageFormat.format("ExpressionImpl.evaluate: Peeked={0}",stackableImpl));
						if(addOperationFound){
							stackableImpl.operation=Operation.Add;
							addOperationFound=false;
						}
						//if end-delimiter
						if((mi.a == stackableImpl.mBegin.a) &&
								(mi.d.name().startsWith("_")) &&
									(mi.d.name().substring(1, mi.d.name().length())
											.equals(stackableImpl.mBegin.d.name()))){
							stackableImpl.mEnd=mi;
							threadLocalSavingSupensionRequired=false;
							if(mi.d==Delimiter._Y){
								threadLocalSavingRequired=false;
								if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled())
									logger.debug(MessageFormat.format("ExpressionImpl.evaluate: Popped={0}",stackableImpl));
								//stackableItem.value=ThreadLocalContext.getCurrent().getValue();
								ThreadLocalContext.getCurrent().setValue(stackableImpl.value);
								s.pop();
							}
						}
						else{
							pushRequired=true;
						}
					}
					if(s.isEmpty() || pushRequired){
						StackableImpl newStackableItem = new StackableImpl();
						newStackableItem.mBegin = mi;
						s.push(newStackableItem);
						if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled())
							logger.debug(MessageFormat.format("ExpressionImpl.evaluate: Pushed={0}",newStackableItem));
					}
				}
				else {
					//peek to see if it could be popped
					//grab the value of the popped item to be used as input
					StackableImpl item = null;
					double input = x;
					if(!s.isEmpty()){
						item = s.peek();
						if(item.mEnd!=null){
							if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled())
								logger.debug(MessageFormat.format("ExpressionImpl.evaluate: StackableImpl Popped={0}",item));
							s.pop();
							switch(item.mEnd.d){
								case _X:
									input=item.value;
								default:
							}
						}
					}
					Term term = (Term)i;
					
					if(threadLocalSavingRequired && !threadLocalSavingSupensionRequired){
						/*
						if(addOperationFound)
							ThreadLocalContext.getCurrent().incrementValue(term.evaluate(input));
						else
						*/
						ThreadLocalContext.getCurrent().setValue(term.evaluate(input));
						if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled())
							logger.debug(MessageFormat.format("ExpressionImpl.evaluate: ThreadLocalContext.getCurrent() {0}: x={1}->z={2}",term,input,ThreadLocalContext.getCurrent().getValue()));
					}
					else{
						y=term.evaluate(input);
						if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled())
							logger.debug(MessageFormat.format("ExpressionImpl.evaluate: {0}: x={1}->y={2}",term,input,y));
					}
					if(term instanceof ComplexTerm){
						ComplexTerm cTerm = (ComplexTerm)term;
						for(Term t: this.getMultiplierList(cTerm, new ArrayList<Term>())){
							ThreadLocalContext.getCurrent().setStackable(t);
							if(threadLocalSavingRequired && !threadLocalSavingSupensionRequired){

								double _d = ThreadLocalContext.getCurrent().getValue();
								double _e = this.evaluate(x);
								ThreadLocalContext.getCurrent().setValue(_d);

								ThreadLocalContext.getCurrent().updateValue(_e, Operation.Multiply);
								if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled())
									logger.debug(MessageFormat.format("ExpressionImpl.evaluate: ThreadLocalContext.getCurrent() {0}: x={1}->z={2}",term,input,ThreadLocalContext.getCurrent().getValue()));
							}
							else{
								y*=this.evaluate(x);
							}
							//ThreadLocalContext.getCurrent().reset();//reset for the second iteration
						}
					}
					if(!s.isEmpty()){
						item = s.peek();
						if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled())
							logger.debug(MessageFormat.format("ExpressionImpl.evaluate: StackableImpl={0}",item));
						if(!addOperationFound)
							addOperationFound=item.operation==Operation.Add;
						if(addOperationFound){
							if(threadLocalSavingRequired && !threadLocalSavingSupensionRequired){
								item.value+=ThreadLocalContext.getCurrent().getValue();
								ThreadLocalContext.getCurrent().setValue(x);
							}
							else
								item.value+=y;
							addOperationFound=false;
						}
						else{
							if(threadLocalSavingRequired && !threadLocalSavingSupensionRequired){
								item.value=ThreadLocalContext.getCurrent().getValue();
								ThreadLocalContext.getCurrent().setValue(x);
							}
							else
								item.value=y;
						}
					}
				}
			}
			if(Boolean.getBoolean(Constants.TEST) && logger.isDebugEnabled())
				logger.debug(MessageFormat.format("ExpressionImpl.evaluate: returning {0}",y));
			return y;
		}
		else
			throw new EvaluationException("Cannot Evaluate - ExpressionImpl not found");
	}
	
	private List<Term> getMultiplierList(Term t, List<Term> list){
		
		if(t instanceof ComplexTerm){
			ComplexTerm c = (ComplexTerm)t;
			for(Term _t:c.getMultiplier()){
				list.add(_t);
				getMultiplierList(_t, list);
			}
		}
		return list;
	}
	public String toString(){
		Iterator<Stackable> iter = this.stackables.iterator();
		Stackable s= null;
		while(iter.hasNext()){
			s= iter.next();
		}
		return s.toString();
	}
	@Override
	public void display(){
		logger.info(MessageFormat.format("ExpressionImpl: {0}",toString()));
	}
}
package com.math.jgraph.ui;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.math.jgraph.ObjectUtil;
import com.math.jgraph.ThreadLocalContext;
import com.math.jgraph.constant.Constants;
import com.math.jgraph.expression.ComplexTerm;
import com.math.jgraph.expression.Expression;
import com.math.jgraph.expression.ExpressionImpl;
import com.math.jgraph.expression.Term;
import com.math.jgraph.function.Function;
import com.math.jgraph.function.FunctionEnum;
import com.math.jgraph.function.FunctionForm;
import com.math.jgraph.function.FunctionFormType;
import com.math.jgraph.function.PolynomialFunction;
import com.math.jgraph.observer.CartesianPlaneEventListener;
import com.math.jgraph.observer.Observer;
import com.math.jgraph.observer.Subject;
public class CartesianPlaneImpl extends JFrame implements Subject, CartesianPlane {
	
	private static final long serialVersionUID = -7361610486148305576L;
	static final Logger logger;
	static{
		DOMConfigurator.configure("log4j-graph.xml");
		logger = Logger.getLogger(CartesianPlaneImpl.class);
	}
	double area1=0.0;
	double area2=0.0;
	private List<Line2D.Double> xLines = new ArrayList<Line2D.Double>();
	private List<Line2D.Double> yLines = new ArrayList<Line2D.Double>();
	private FunctionForm functionForm;
	final private List<Observer> observers = new ArrayList<Observer>();
	private JPanel jPanelGraph;
	private boolean showDerivative;
	private boolean calculateArea;
	
	class MouseMotionListenerImpl implements MouseMotionListener{
	
		public void mouseDragged(MouseEvent e) {
			this.mouseMoved(e);
		}
		public void mouseMoved(MouseEvent e) {
			StringBuilder s = new StringBuilder(Constants.LEFT_PARENTHESES);
			s.append(((e.getX()-Constants.TRANSFORM_FACTOR)/Constants.SCALE_FACTOR))
			 .append(",")
			 .append(((Constants.TRANSFORM_FACTOR-e.getY())/Constants.SCALE_FACTOR))
			 .append(Constants.RIGHT_PARENTHESES);
			jPanelGraph.setToolTipText(s.toString());
		}
	}
	public CartesianPlaneImpl(Observer o){
		this();
		this.register(o);
	}
	public CartesianPlaneImpl(){
		super("Cartesian Plane");
		for(int i = 1;i<=Constants.NO_OF_LINES;i++){
			xLines.add(new Line2D.Double(0, i*Constants.SCALE_FACTOR, 
											Constants.X_LENGTH, i*Constants.SCALE_FACTOR));
		
			yLines.add(new Line2D.Double(i*Constants.SCALE_FACTOR, 0, 
											i*Constants.SCALE_FACTOR, Constants.Y_LENGTH));
		}
		createAndShowGui();
	}

	@Override
	public void addFunctionForm(FunctionForm functionForm, 
									boolean showDerivative, 
										boolean calculateArea){
		this.showDerivative=showDerivative;
		this.calculateArea=calculateArea;
		this.functionForm=functionForm;
		this.update(getGraphics());
	}
	
	@Override
	public void disposeGui(){
		dispose();
		ThreadLocalContext.getCurrent().setEvent(CartesianPlaneEvent.WINDOW_CLOSURE_EVENT);
		notifyObservers();
	}
	
	@Override
	public void createAndShowGui() {
		
		//ImageIcon loading = new ImageIcon("../img/ajax-loader.gif");
		//jLabelLoading = new JLabel("loading... ", loading, JLabel.CENTER);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);


		this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					disposeGui();
				}
			}
		);


		this.setBounds(625,0,625+Constants.X_LENGTH,Constants.Y_LENGTH);
		this.setSize(Constants.X_LENGTH,Constants.Y_LENGTH);

		this.jPanelGraph = new JPanel() {
			private static final long serialVersionUID = -4698439477375463413L;
			
			@SuppressWarnings("unused")
			private void executeSequentially(Graphics2D g2, 
												List<Function> functionList, 
													double h, 
														double l, 
															Color[] color){
				long time1 = new java.util.Date().getTime();
				//Line2D.Double prevL1 = null;
				List<Line2D.Double> lineList = new ArrayList<Line2D.Double>();
				/*				
  				for(double x = l;x<h;x+=Constants.INCREMENTAL_STEP){
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
						if(_l!=null){
							Line2D.Double cloneL1 = (Line2D.Double)_l.clone();
							lineList.add(cloneL1);
							g2.setColor(color[0]);
							g2.draw(_l); //function
							//if(prevL1!=null){
								//_l.setLine(_l.x1,_l.y1, prevL1.x1, prevL1.y1);
								//g2.draw(_l);
							//}
							//prevL1=_l;
							if(calculateArea){
								double y1=_l.y1;
								double y2=Constants.TRANSFORM_FACTOR;
								_l.setLine(_l.x1,y1,_l.x2,y2);
								g2.draw(_l);
								area1+=(Math.abs(y2-y1)/Constants.SCALE_FACTOR)*(Constants.INCREMENTAL_STEP);
							}
						}
					}
				}
				*/
	  				for(double x = l;x<h;x+=Constants.INCREMENTAL_STEP){
	  					
	  					/*Line2D.Double ls1 = function1.image(x);//x=f(s)
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
	  					}*/
	  					Line2D.Double ls = lineSegment(functionList, x);
	  					if(ls!=null){
							Line2D.Double lsClone1 = (Line2D.Double)ls.clone();
							lineList.add(lsClone1);
							g2.setColor(color[0]);
							g2.draw(ls); //function
							if(calculateArea){
								double y1=ls.y1;
								double y2=Constants.TRANSFORM_FACTOR;
								ls.setLine(ls.x1,y1,ls.x2,y2);
								g2.draw(ls);
								area1+=(Math.abs(y2-y1)/Constants.SCALE_FACTOR)*(Constants.INCREMENTAL_STEP);
							}
						}
	  				}					
				if(showDerivative){
					plotDerivative(g2,lineList,color);
				}
				long time2 = new java.util.Date().getTime();
				logger.info("com.math.jgraph.ui.CartesianPlaneImpl$2.executeSequentially: "+(time2-time1)+" secs");
			}
			private void plotDerivative(Graphics2D g2,
											List<Line2D.Double> lineList,
												Color[] color){
				//Line2D.Double prevL2 = null;
				
				for(int z=0;z<lineList.size()-1;z++){
					Line2D.Double l1 = lineList.get(z);
					/*Line2D.Double cloneOfl1 = (Line2D.Double)ObjectUtil.cloneObject(l1);*/
					Line2D.Double l2 = lineList.get(z+1);
					try{	
						
						double tanOfTheta1=BigDecimal.valueOf((l1.y1-l2.y1)/(l1.x1-l2.x1)).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
							l1.y1=Constants.TRANSFORM_FACTOR + Constants.SCALE_FACTOR*(tanOfTheta1);
						double tanOfTheta2=BigDecimal.valueOf((l1.y2-l2.y2)/(l1.x2-l2.x2)).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
							l1.y2=Constants.TRANSFORM_FACTOR + Constants.SCALE_FACTOR*(tanOfTheta2);
						g2.setColor(color[1]);
						g2.draw(l1);
						/*if(prevL2!=null){
							l1.setLine(l1.x1,l1.y1, prevL2.x1, prevL2.y1);
							g2.draw(l1);
						}*/
						/*prevL2=l1;*/
						
						/*
						double $y1 = (cloneOfl1.x2 + (1 * Constants.SCALE_FACTOR) - cloneOfl1.x1)*tanOfTheta1 + cloneOfl1.y1;
						double $y2 = (cloneOfl1.x2 - (1 * Constants.SCALE_FACTOR) - cloneOfl1.x1)*tanOfTheta1 + cloneOfl1.y1;
						Color origColor = g2.getColor();
						
						g2.setColor(color[0].brighter());
						Line2D.Double _slope = new Line2D.Double(
								cloneOfl1.x1,	
									cloneOfl1.y1, 
									cloneOfl1.x2 + (1 * Constants.SCALE_FACTOR),		
										$y1);
										
						
						g2.draw(_slope);
						_slope.setLine(new Line2D.Double(
								cloneOfl1.x1,	
									cloneOfl1.y1, 
									cloneOfl1.x2 - (1 * Constants.SCALE_FACTOR),		
										$y2));
						g2.draw(_slope);
						g2.setColor(origColor);
						*/
						if(calculateArea){
							double y1=l1.y1;
							double y2=Constants.TRANSFORM_FACTOR;
							l1.setLine(l1.x1,y1,l1.x2,y2);
							g2.draw(l1);
							area2+=(Math.abs(y2-y1)/Constants.SCALE_FACTOR)*(Constants.INCREMENTAL_STEP);
						}
					}
					catch(NumberFormatException nfe){
						logger.error(MessageFormat.format("({0},{1})",l1.x1,l1.y1));
						logger.error(MessageFormat.format("({0},{1})",l1.x2,l1.y2));
					}
				}
			}
			
			private void executeConcurrently(Graphics2D g2, 
												List<Function> functionList, 
													double h, 
														double l, 
															Color[] color){
				long time1 = new java.util.Date().getTime();
				//Line2D.Double prevL1 = null;
				
				ExecutorService service = Executors.newFixedThreadPool(Constants.N_THREADS);
				if(logger.isDebugEnabled())
					logger.debug(MessageFormat.format("Threads: {0}",Constants.N_THREADS));
				List<Future<Line2D.Double>> imageFutures = new ArrayList<Future<Line2D.Double>>();
				List<Line2D.Double> lineList = new ArrayList<Line2D.Double>();
				for(double x = l;x<h;x+=Constants.INCREMENTAL_STEP){
					imageFutures.add(service.submit(new CartesianPlaneImpl.ImageCallable(functionList,x)));
				}
				for(Future<Line2D.Double> imageFuture: imageFutures){
					try{
						Line2D.Double ls = imageFuture.get();
						if(ls!=null){
							Line2D.Double cloneL1 = (Line2D.Double)ls.clone();
							lineList.add(cloneL1);
							g2.setColor(color[0]);
							g2.draw(ls); //function
							/*if(prevL1!=null){
								_l.setLine(_l.x1,_l.y1, prevL1.x1, prevL1.y1);
								g2.draw(_l);
							}*/
							//prevL1=_l;
							if(calculateArea){
								double y1=ls.y1;
								double y2=Constants.TRANSFORM_FACTOR;
								ls.setLine(ls.x1,y1,ls.x2,y2);
								g2.draw(ls);
								area1+=(Math.abs(y2-y1)/Constants.SCALE_FACTOR)*(Constants.INCREMENTAL_STEP);
							}
						}
					}
					catch(InterruptedException ie){
						Thread.currentThread().interrupt();
						logger.error(ie.getMessage());
					}
					catch(ExecutionException ee){
						logger.error(ee.getMessage());
					}
				}
				if(showDerivative){
					plotDerivative(g2,lineList,color);
				}
				service.shutdown();
				long time2 = new java.util.Date().getTime();
				logger.info("com.math.jgraph.ui.CartesianPlaneImpl$2.executeConcurrently: "+(time2-time1)+" secs");
			}
			
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
				Graphics2D g2 = (Graphics2D)g;
				g2.setColor(Color.GRAY);
				g2.setBackground(Color.WHITE);
				drawAxes(g2,  xLines);
				drawAxes(g2,  yLines);
				if(CartesianPlaneImpl.this.functionForm!=null){
					List<List<Expression>> listOfExpressionList = CartesianPlaneImpl.this.functionForm.getListOfExpressionList();
					
					
					List<Function> functionList = new ArrayList<Function>();
					double h = CartesianPlaneImpl.this.functionForm.getH();
					double l = CartesianPlaneImpl.this.functionForm.getL();
					Color[] color = CartesianPlaneImpl.this.functionForm.getColor();
					
					for(List<Expression> expressionList: listOfExpressionList){
						if(expressionList!=null){
							//convert polar to parametric form
							if(CartesianPlaneImpl.this.functionForm.getType()==FunctionFormType.Polar){
								PolynomialFunction f1 = new PolynomialFunction();
								PolynomialFunction f2 = new PolynomialFunction();
								Term t1 = null;
								Term t2 = null;
								for(Expression expression: expressionList){
									ComplexTerm cosineTerm = new ComplexTerm(Constants.VARIABLE_THETA);
									t1 = ((ExpressionImpl)expression).getTerm();
									cosineTerm.setFunction(FunctionEnum.cos);
									cosineTerm.addMultiplier(t1);
									cosineTerm.setCoefficient(t1.getCoefficient());
									
									ComplexTerm sineTerm = new ComplexTerm(Constants.VARIABLE_THETA);
									t2 = (Term)ObjectUtil.cloneObject(t1);
									sineTerm.setFunction(FunctionEnum.sin);
									sineTerm.addMultiplier(t2);
									sineTerm.setCoefficient(t2.getCoefficient());
									
									f1.addPExpression(new ExpressionImpl(cosineTerm));
									f2.addPExpression(new ExpressionImpl(sineTerm));
								}
								functionList.add(f1);
								functionList.add(f2);
							}
							else{
							
								PolynomialFunction f = new PolynomialFunction();
								for(Expression expression: expressionList){
									f.addPExpression(expression);
								}
								functionList.add(f);
							}
						}
					}
					area1=area2=0.0;
					//executeSequentially(g2, functionList, h, l, color);
					executeConcurrently(g2,functionList,h,l,color);
					ThreadLocalContext.getCurrent().setEvent(CartesianPlaneEvent.AREA_UPDATE_EVENT);
					notifyObservers();
				}
			}
			private void drawAxes(Graphics2D g2, List<Line2D.Double> lines){
				Stroke stroke = g2.getStroke();
				int i = 1;
				int c = yLines.size();
				for(Line2D.Double lin: lines){ 
					if(i++==c/2)
						g2.setStroke(new BasicStroke(2));
					else
						g2.setStroke(stroke);
					g2.draw(lin);
				}
			}
        };
		//JFrame.setDefaultLookAndFeelDecorated(true);
		this.jPanelGraph.addMouseMotionListener(new MouseMotionListenerImpl());

		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());		
		contentPane.add(this.jPanelGraph,BorderLayout.CENTER);
		this.setVisible(true);
		this.setResizable(false);
	}

	@Override
	public void register(Observer obj){
		if(obj!=null)
			if(!observers.contains(obj)) 
				observers.add(obj); 
	}
	
	@Override
    public void unregister(Observer obj){
		this.observers.remove(obj);
	}
     //method to notify observers of change  
	@Override
    public void notifyObservers(){
		for(Observer obj: observers){
			if(obj instanceof CartesianPlaneEventListener){
				switch(ThreadLocalContext.getCurrent().getEvent()){
					case WINDOW_CLOSURE_EVENT:
						((CartesianPlaneEventListener)obj).windowClosureUpdate();
						break;
					case AREA_UPDATE_EVENT:
						((CartesianPlaneEventListener)obj).areaCalculationUpdate(calculateArea?new double[]{area1,area2}:new double[0]);
						break;
					default:
				}
			}
		}
		ThreadLocalContext.getCurrent().setEvent(null);
	}
	/*public static void main(String[] a) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					CartesianPlaneImpl app = new CartesianPlaneImpl();
				} catch (Throwable e) {
					e.printStackTrace();
				}				
            }
        });
	}*/
	static Line2D.Double lineSegment(List<Function> functionList, double x){
		Line2D.Double ls1 = null;
		if(functionList!=null){
			Function function1 = functionList.get(0);
			Function function2 = null;
			if(functionList.size()>1)
				function2 = functionList.get(1);
			ls1 = function1.image(x);//x=f(s)
			if(function2!=null){
				Line2D.Double ls2 = function2.image(x);//y=f(s)
				if(ls2!=null){
					if(ls1!=null){
						ls1.x1=-ls1.y1+2*Constants.TRANSFORM_FACTOR;
						ls1.x2=-ls1.y1+2*Constants.TRANSFORM_FACTOR;
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
	static class ImageCallable implements Callable<Line2D.Double> {
		private double x;
		private List<Function> functionList;
		public ImageCallable(List<Function> functionList, double x) {
			this.x=x;
			this.functionList=functionList;
		}
		@Override
		public Line2D.Double call() {
			return lineSegment(this.functionList, x);
		}
	} 
}

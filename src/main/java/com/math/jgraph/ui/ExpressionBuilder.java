package com.math.jgraph.ui;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.math.jgraph.ObjectUtil;
import com.math.jgraph.constant.Constants;
import com.math.jgraph.expression.ComplexTerm;
import com.math.jgraph.expression.Expression;
import com.math.jgraph.expression.ExpressionImpl;
import com.math.jgraph.expression.SimpleTerm;
import com.math.jgraph.expression.Term;
import com.math.jgraph.function.FunctionEnum;
import com.math.jgraph.function.FunctionForm;
import com.math.jgraph.function.FunctionFormType;
import com.math.jgraph.observer.ExpressionBuilderEventListener;
import com.math.jgraph.observer.Observer;
import com.math.jgraph.observer.Subject;
public class ExpressionBuilder extends JFrame implements 
	ActionListener,CaretListener, Subject{
	
	private static final long serialVersionUID = 4088840495723968559L;
	static final Logger logger;
	static{
		DOMConfigurator.configure("log4j-graph.xml");
		logger = Logger.getLogger(ExpressionBuilder.class);
	}	
	private static enum TermTypeEnum{
		Simple,
		Complex;
		public static String[] names(){
			return 
				Arrays.stream(TermTypeEnum.values())
				  	.map(t->t.name())
				  	.collect(Collectors.toList())
				  	.toArray(new String[TermTypeEnum.values().length]);
		}
		
	};
	private static enum JButtonTypeEnum{
		NewTerm,
		SetCurrent,
		AddTerm,
		SaveTerm,
		DeleteTerm,
		Reset,
		Done
	};
	private static enum AddTermEnum{
		Multiplier,
		Input,
		Exponent;
	};
	private char var;
	private final DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
	private JComboBox<String> jComboBoxExprTypes;
	private JComboBox<String> jComboBoxMathFunctions;
	private JComboBox<String> jComboBoxAdd;
	
	private JTextField jTextFieldCoefficient;
	private JTextField jTextFieldPower;
	private JTextArea[] jTextArea = new JTextArea[2];
	private JButton[] jButtons;

	private Term t;
	private List<Expression> expressionList = new ArrayList<Expression>();
	final private Map<String, Term> expressionMap = new HashMap<String, Term>();
	private FunctionForm functionForm;
	final private List<Observer> observers = new ArrayList<Observer>();
	private Map<JButtonTypeEnum, JButton> jButtonMap = new HashMap<JButtonTypeEnum, JButton>();
	private int counter=0;
	
	//instance block
	{
		jButtons = new JButton[JButtonTypeEnum.values().length];
		String name="";
		for(int i = 0;i<JButtonTypeEnum.values().length;i++){
			name=JButtonTypeEnum.values()[i].name();
			jButtons[i] = new JButton(name);
			jButtonMap.put(JButtonTypeEnum.values()[i],jButtons[i]); 
			if(name.equals(JButtonTypeEnum.SetCurrent.name()) || 
				name.equals(JButtonTypeEnum.AddTerm.name()) || 
					name.equals(JButtonTypeEnum.SaveTerm.name()))
			jButtons[i].setEnabled(false);
		}
	}

	private static String generateId(Map<String, Term> m, Term t){
		DecimalFormat df = new DecimalFormat("00");
		Set<String> keySet = m.keySet();
		String name = t.getClass().getSimpleName();
		int i=0;
		for(String key:keySet){
			logger.debug("ExpressionBuilder.generateId: key="+ key);
			if(key.matches("^"+name+"[\\d]+$")){
				try{
					int _i=Integer.parseInt(key.substring(name.length()));
					if(_i>i)
						i=_i;
				}
				catch(NumberFormatException e){
					logger.error(e);
					i=m.size();
				}
			}
		}
		String id = name+ df.format(i+1);
		logger.debug("ExpressionBuilder.generateId: returning id: "+ id);
		return id;
	}
	public ExpressionBuilder(FunctionFormType functionFormType){
		super("ExpressionImpl Builder:"+functionFormType.name());
		//UIManager.put("JFrame.activeTitleBackground", Color.RED);
		switch(functionFormType){
			case Functional:
				
			case Polar:
				this.counter=1;
				break;

			case Parametric:
				this.counter=2;
		}
		this.var = functionFormType.getVar();
		functionForm = new FunctionForm(functionFormType);
		createAndShowGui();
	}
	private void createAndShowGui() {
		
		model.addElement(AddTermEnum.Multiplier.name());
		model.addElement(AddTermEnum.Input.name());
		jComboBoxAdd = new JComboBox<>(model);
		
		jComboBoxAdd.setEnabled(false);
		//combo box for expression types
		//create it, select an index and add an ItemChangeListener for it
		jComboBoxExprTypes = new JComboBox<>(TermTypeEnum.names());
		jComboBoxExprTypes.setSelectedIndex(0);
		jComboBoxExprTypes.addItemListener(new ExpressionTypeItemChangeListener());
		
		//combo box for math functions
		//create it, select an index and add an ItemChangeListener for it
		jComboBoxMathFunctions = new JComboBox<>(FunctionEnum.names());
		jComboBoxMathFunctions.setSelectedIndex(0);
		jComboBoxMathFunctions.addItemListener(new FunctionListItemChangeListener());
		jComboBoxMathFunctions.setEnabled(false);


		
		//text field labeled coefficient
		jTextFieldCoefficient = new JTextField(3);
		jTextFieldCoefficient.setText("1.0");
		
		//text field labeled power
		jTextFieldPower = new JTextField(3);
		jTextFieldPower.setText("1.0");
		
		//text areas, create them, set font, set linewrap and make them uneditable
		for(int i = 0;i<jTextArea.length;i++){
			jTextArea[i] = new JTextArea(4, 30);
			jTextArea[i].setFont(new Font("Dialog", Font.PLAIN, 11));

			jTextArea[i].setLineWrap(true);
			jTextArea[i].setWrapStyleWord(true);
			jTextArea[i].setEditable(false);
			
		}
		//register a listener with the left text area
        jTextArea[0].addCaretListener(this);
		JFrame.setDefaultLookAndFeelDecorated(true);
		this.setBounds(0,165, 600, 210);
		
		//set border layout 
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());

		//jpanel for combo boxes and text fields
		JPanel jPanelNorth = new JPanel(new FlowLayout());

		jPanelNorth.add(new JLabel("Term:"));
		jPanelNorth.add(jComboBoxExprTypes);
		
		jPanelNorth.add(new JLabel("Function:"));
		jPanelNorth.add(jComboBoxMathFunctions);
		
		jPanelNorth.add(new JLabel("Coefficient:"));
		jPanelNorth.add(jTextFieldCoefficient);
		
		jPanelNorth.add(new JLabel("Power:"));
		jPanelNorth.add(jTextFieldPower);

		//jpanel for 2 text areas
		JPanel jPanelCenter = new JPanel(new BorderLayout());
		JPanel jPanelCenterC = new JPanel();
		jPanelCenter.add(jPanelCenterC, BorderLayout.CENTER);

		
		
		jPanelCenterC.add(new JScrollPane(jTextArea[0]));
		jPanelCenterC.add(new JScrollPane(jTextArea[1]));

		//jpanel for buttons & radio buttons
		JPanel jPanelSouth = new JPanel();
		for(JButton jButton: jButtons){
			jButton.addActionListener(this);
		}
		JPanel jPanelButtons = new JPanel(new GridLayout(2,8));

		jPanelButtons.add(jButtonMap.get(JButtonTypeEnum.NewTerm));
		jPanelButtons.add(jButtonMap.get(JButtonTypeEnum.SetCurrent));
		jPanelButtons.add(jComboBoxAdd);
		jPanelButtons.add(jButtonMap.get(JButtonTypeEnum.AddTerm));

		
		for(int i=1;i<=4;i++)
			jPanelButtons.add( new JLabel());
		

		jPanelButtons.add(jButtonMap.get(JButtonTypeEnum.DeleteTerm));
		jPanelButtons.add(jButtonMap.get(JButtonTypeEnum.Reset));
		jPanelButtons.add(jButtonMap.get(JButtonTypeEnum.SaveTerm));
		jPanelButtons.add(jButtonMap.get(JButtonTypeEnum.Done));
		
		
		jPanelSouth.add(jPanelButtons);
		contentPane.add(jPanelNorth, BorderLayout.NORTH);
		contentPane.add(jPanelCenter, BorderLayout.CENTER);
		contentPane.add(jPanelSouth, BorderLayout.SOUTH);

		this.setVisible(true);
		this.setResizable(false);
		//this.pack();
	}
	private void reset(){
		expressionList.clear();
		expressionMap.clear();
		t=null;
		jTextArea[0].setText("");
		jTextArea[1].setText("");
		resetComboBoxes();
		resetTextFields();
		enableDisableButtons(JButtonTypeEnum.SetCurrent,false);
		enableDisableButtons(JButtonTypeEnum.AddTerm,false);
		enableDisableButtons(JButtonTypeEnum.SaveTerm,false);
		jComboBoxAdd.setEnabled(false);
	}
	private void resetComboBoxes(){
		jComboBoxExprTypes.setSelectedIndex(0);
		jComboBoxMathFunctions.setSelectedIndex(0);
		jComboBoxMathFunctions.setEnabled(false);
	}
	private void resetTextFields(){
		jTextFieldCoefficient.setText("1.0");
		jTextFieldPower.setText("1.0");	
	}
	private void displayExpression(){
		StringBuilder builder = new StringBuilder();
		Iterator<Map.Entry<String, Term>> it = expressionMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String,Term> pairs = it.next();
			String key = pairs.getKey();
			Term value = pairs.getValue();
			builder.append(key).append(":").append(value).append(Constants.NEW_LINE);
		}
		jTextArea[0].setText(builder.toString());
	}
	private void enableDisableButtons(JButtonTypeEnum button, boolean enabled){
		if(button!=null){
			for(JButton jButton: jButtons){
				if(jButton.getText().equals(button.name()))
					jButton.setEnabled(enabled);	
			}
		}
	}
	
	@Override
	public void caretUpdate(CaretEvent e) {
		String key=jTextArea[0].getSelectedText();
		jTextArea[0].setToolTipText(null);
		if(key!=null && key.matches("^(Complex|Simple)Term[\\d]+$")){
			Term i = expressionMap.get(key);
			if(i!=null)
				jTextArea[0].setToolTipText(key+":"+expressionMap.get(key).toString());
		}
    }


	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {  
		Object source = e.getSource();
		if(source instanceof JButton){
			String cmd = e.getActionCommand(); 
			JButtonTypeEnum jButtonTypeEnum = JButtonTypeEnum.valueOf(cmd);
			String key = "";
			switch(jButtonTypeEnum){
				case Reset:
					reset();
					break;
				case AddTerm:
					key=jTextArea[0].getSelectedText();
					if(logger.isDebugEnabled())
						logger.debug("ExpressionBuilder.actionPerformed: "+key+"->"+expressionMap);

					if(key!=null && key.matches("^(Complex|Simple)Term[\\d]+$")){
						Term i = expressionMap.get(key);
						logger.debug("ExpressionBuilder.actionPerformed: currently saved term : "+t);
						ComplexTerm complexTerm = (ComplexTerm)t;

						if(i!=null){
							String s = (String)jComboBoxAdd.getSelectedItem();
							logger.debug("ExpressionBuilder.actionPerformed: selected type: "+s);
							switch(AddTermEnum.valueOf(s)){
								case Multiplier:
									complexTerm.addMultiplier((Term)ObjectUtil.cloneObject(i));
									break;
								case Input:
									complexTerm.addInput((Term)ObjectUtil.cloneObject(i));
									break;
								case Exponent:
									complexTerm.addExponent((Term)ObjectUtil.cloneObject(i));
							}
						}

						logger.debug("ExpressionBuilder.actionPerformed: term to add: "+i);
						logger.debug("ExpressionBuilder.actionPerformed: currently saved term changes to: "+complexTerm);

						jTextArea[1].setText(t.toString());
						displayExpression();
					}
					else
						JOptionPane.showMessageDialog(this, "To select a term in the (left) TextArea, double click on it!",
												"Error", JOptionPane.ERROR_MESSAGE);
					jTextArea[0].select(0,0);
					key="";
					break;
				case SaveTerm:
					expressionList.add(new ExpressionImpl(t));
					if(logger.isDebugEnabled())
						logger.debug("ExpressionBuilder.actionPerformed: expression list: "+ expressionList);
					jTextArea[0].select(0,0);
					break;
				case SetCurrent:
					key=jTextArea[0].getSelectedText();
					boolean incorrectSelection = false;
					if(key!=null && key.matches("^(Complex|Simple)Term[\\d]+$")){
						Term _t = expressionMap.get(key);
						if(_t!=null){
							this.t=_t;
							enableDisableButtons(JButtonTypeEnum.AddTerm,_t instanceof ComplexTerm);
							enableDisableButtons(JButtonTypeEnum.SaveTerm,_t instanceof Term);
							
							if(_t instanceof ComplexTerm && ((ComplexTerm)_t).getFunction()==FunctionEnum.pow){
								if(model.getIndexOf(AddTermEnum.Exponent.name())<0)
									model.addElement(AddTermEnum.Exponent.name());
							}
							else{
								model.removeElement(AddTermEnum.Exponent.name());
							}
							jComboBoxAdd.setEnabled(_t instanceof ComplexTerm);
							jTextArea[1].setText(this.t.toString());
						}
						else
							incorrectSelection=true;
					}
					else
						incorrectSelection=true;
					if(incorrectSelection)
						JOptionPane.showMessageDialog(this, "To select a term in the (left) TextArea, double click on it!",
												"Error", JOptionPane.ERROR_MESSAGE);
					jTextArea[0].select(0,0);
					key="";
					break;
				case NewTerm:
					int index = jComboBoxExprTypes.getSelectedIndex();
					try{
						double coeff  = Double.parseDouble(jTextFieldCoefficient.getText());
						double power  = Double.parseDouble(jTextFieldPower.getText());
						Term t = null;
						switch(index){
							case 0:
								t = new SimpleTerm(var);
								break;
							case 1:
								t = new ComplexTerm(var);
								((ComplexTerm)t).setFunction( 
									FunctionEnum.getFunctionEnum((String)jComboBoxMathFunctions.getSelectedItem())
												);
								break;
						}
						t.setCoefficient(coeff);
						t.setPower(power);
						String id = ExpressionBuilder.generateId(expressionMap,t);
						expressionMap.put(id, t);
						displayExpression();
						resetComboBoxes();
						resetTextFields();
						enableDisableButtons(JButtonTypeEnum.SetCurrent,true);
						jTextArea[0].select(0,0);
					}
					catch(NumberFormatException ne){
						JOptionPane.showMessageDialog(this, "Coefficient and Power must be numbers",
														"Error", JOptionPane.ERROR_MESSAGE);
					}
					break;
				case DeleteTerm:
					key=jTextArea[0].getSelectedText();
					
					if(key!=null && key.matches("^(Complex|Simple)Term[\\d]+$")){
						if(expressionMap.containsKey(key)){
							expressionMap.remove(key);
							displayExpression();
						}
					}
					key="";
					break;
				case Done:
					this.counter--;
					/*if(this.functionForm.getType()==FunctionFormType.Parametric){
						String[] tokens = this.getTitle().split(String.valueOf(Constants.COLON));
						this.setTitle(tokens[0]+Constants.COLON+"y=f(s)");
					}*/
					if(!this.expressionList.isEmpty())
						this.functionForm.add((List<Expression>)ObjectUtil.cloneObject(this.expressionList));
					reset();
					if(this.counter==0){
						notifyObservers();
						dispose();
						return;
					}
					else{
						
						if(!this.functionForm.getListOfExpressionList().isEmpty()){
							StringBuilder buffer  = new StringBuilder();
							buffer.append("Saved\nx=f(s)=");
							for(List<Expression> listIExpr: this.functionForm.getListOfExpressionList()){
								int i = 0;
								ExpressionImpl expr = null;
								for(;i<listIExpr.size()-1;i++){
									expr = (ExpressionImpl)listIExpr.get(i);
									buffer.append(expr.getTerm()).append(Constants.ADD);
								}
								buffer.append(((ExpressionImpl)listIExpr.get(i)).getTerm()).append(Constants.NEW_LINE);
							}
							buffer.append("Now build parametric function y=f(s)");
							this.jTextArea[1].setText(buffer.toString());
						}
					}
					break;
			}
		}
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
	
	@Override
    //method to notify observers of change  
    public void notifyObservers(){
		for(Observer obj: observers){
			if(obj instanceof ExpressionBuilderEventListener)
				((ExpressionBuilderEventListener)obj).expressionListUpdate(this.functionForm);
		}
	}
	private class FunctionListItemChangeListener implements ItemListener{
		@Override
		public void itemStateChanged(ItemEvent event) {
		   if (event.getStateChange() == ItemEvent.SELECTED) {
			 // Object item = event.getItem();
		   }
		}       
	}
	
	private class ExpressionTypeItemChangeListener implements ItemListener{
		@Override
		public void itemStateChanged(ItemEvent event) {

		   if (event.getStateChange() == ItemEvent.SELECTED) {
			  Object item = event.getItem();
			  if(item.equals(TermTypeEnum.Complex.name())){
					jComboBoxMathFunctions.setEnabled(true);
			  }
			  else
				  jComboBoxMathFunctions.setEnabled(false);
		   }
		}       
	}
	/*public static void main(String[] a) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					ExpressionBuilder eBuilder = new ExpressionBuilder();
				} catch (Throwable e) {
					e.printStackTrace();
				}					
            }
        });
	}*/
}

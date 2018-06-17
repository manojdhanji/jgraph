package com.math.jgraph.ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.math.jgraph.ObjectUtil;
import com.math.jgraph.constant.Constants;
import com.math.jgraph.expression.Expression;
import com.math.jgraph.expression.Operation;
import com.math.jgraph.function.FunctionForm;
import com.math.jgraph.function.FunctionFormType;
import com.math.jgraph.observer.CartesianPlaneEventListener;
import com.math.jgraph.observer.ExpressionBuilderEventListener;
import com.math.jgraph.observer.Subject;
public class JGraphApplication extends JFrame 
	implements ActionListener, ExpressionBuilderEventListener, CartesianPlaneEventListener{
	
	private static final long serialVersionUID = -968107859388491769L;
	static final Logger logger;
	static{
		DOMConfigurator.configure("log4j-graph.xml");
		logger = Logger.getLogger(JGraphApplication.class);
	}
	
	private static enum JButtonTypeEnum{
		BuildExp,
		SetColor,
		DrawAxes,
		Plot,
		Clear,
		Exit,
		ZoomIn,
		ZoomOut,
		LoadExp,
		SaveExp;
	};

	private JButton[] jButtons;
	private Map<JButtonTypeEnum, JButton> jButtonMap = new HashMap<JButtonTypeEnum,JButton>();
	private JTextField jTextFieldLowerLimit, jTextFieldHigherLimit;
	private JCheckBox[] jCheckBox = new JCheckBox[2];
	private JComboBox<String> jComboBoxFormType;
	private JCheckBox jCheckBoxAreaBound, jCheckBoxClearTextArea;
	private JTextArea jTextArea;
	private Color[] color = new Color[2];
	private double[] area;
	
	private FunctionForm functionForm;
	private CartesianPlane plane;

	public JGraphApplication(){
		super("JGraph");
		createAndShowGui();
	}
	private void createAndShowGui() {
		this.color[0]=Color.RED;
		this.color[1]=Color.BLACK;
		jTextArea = new JTextArea(7, 30);
		jTextArea.setFont(new Font("Dialog", Font.PLAIN, 11));
		jTextArea.setLineWrap(true);
		jTextArea.setWrapStyleWord(true);
		jTextArea.setEditable(false);
		jTextArea.addMouseListener(
			new MouseAdapter() {
				@Override 
				public void mouseClicked(MouseEvent evt) {
				    if (evt.getClickCount() >= 2) {
				      
				      String s = jTextArea.getSelectedText();
				      System.out.println("click: "+s);
				      //TODO more work to be done to support saving multiple expressions and loading by choice.
				    }
				}
			});

		jCheckBox[0] = new JCheckBox("f(x)", true);
		jCheckBox[1] = new JCheckBox("f'(x)", false);
		
		jComboBoxFormType = new JComboBox<>(FunctionFormType.names());

		jCheckBoxAreaBound = new JCheckBox(String.valueOf(Constants.INTEGRAL)+"f(x)dx", false);
		jCheckBoxClearTextArea = new JCheckBox("Text Area");
		int counter = 0;
		for(JCheckBox c: this.jCheckBox){
			c.setForeground(Color.WHITE);
			if(counter==0)
				c.setEnabled(false);
			c.setBackground(this.color[counter++]);
		}
		jButtons = new JButton[JButtonTypeEnum.values().length];
		for(int i = 0;i<JButtonTypeEnum.values().length;i++){
			JButtonTypeEnum j = JButtonTypeEnum.values()[i];
			jButtons[i] = new JButton(j.name());
			jButtonMap.put(j, jButtons[i]);
		}
		//JFrame.setDefaultLookAndFeelDecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(0,0,600,160);
		
		for(JButton jButton: jButtons){
			jButton.addActionListener(this);
		}

		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		JPanel jPanelForComponentsWest = new JPanel(new GridLayout(7,2));
		JPanel jPanelForComponentsEast = new JPanel(new FlowLayout());
		
		
		jTextFieldLowerLimit = new JTextField(Boolean.getBoolean(Constants.TEST)?"0.9":"-5.0",2);
		jTextFieldHigherLimit = new JTextField(Boolean.getBoolean(Constants.TEST)?"1.0":"+5.0", 2);
		JPanel[] jPanelForTextFields = new JPanel[2];
		jPanelForTextFields[0] = new JPanel(new GridLayout(1,2));
		jPanelForTextFields[0].add(new JLabel(" Domain Low: ")); //0
		jPanelForTextFields[0].add(jTextFieldLowerLimit); //1
		jPanelForTextFields[1] = new JPanel(new GridLayout(1,2));
		jPanelForTextFields[1].add(new JLabel(" Domain High: "));//2
		jPanelForTextFields[1].add(jTextFieldHigherLimit);//3
		
		
		jPanelForComponentsWest.add(jPanelForTextFields[0]);
		jPanelForComponentsWest.add(jPanelForTextFields[1]);
		
		jPanelForComponentsWest.add(jComboBoxFormType);
		jPanelForComponentsWest.add(jButtonMap.get(JButtonTypeEnum.BuildExp));
		
		JPanel jPanelForCheckBoxes = new JPanel(new GridLayout(1,2));
		jPanelForCheckBoxes.add(jCheckBox[0]);
		jPanelForCheckBoxes.add(jCheckBox[1]);
		jPanelForComponentsWest.add(jPanelForCheckBoxes);
		jPanelForComponentsWest.add(jButtonMap.get(JButtonTypeEnum.SetColor));

		

		JPanel jPanelForZoomInZoomOutButtons = new JPanel(new GridLayout(1,2));
		jPanelForZoomInZoomOutButtons.add(jButtonMap.get(JButtonTypeEnum.ZoomIn));
		jPanelForZoomInZoomOutButtons.add(jButtonMap.get(JButtonTypeEnum.ZoomOut));
		jPanelForComponentsWest.add(jPanelForZoomInZoomOutButtons);

		jPanelForComponentsWest.add(jButtonMap.get(JButtonTypeEnum.DrawAxes)); //draw axes

		
		jPanelForComponentsWest.add(new JPanel()); 
		jPanelForComponentsWest.add(new JPanel()); 
		
		JPanel jPanelPlot = new JPanel(new GridLayout(1,2)); 
		JPanel jPanelLoadSave = new JPanel(new GridLayout(1,2)); 
		jPanelPlot.add(jCheckBoxAreaBound);		
		jPanelPlot.add(jButtonMap.get(JButtonTypeEnum.Plot));
		
		jPanelLoadSave.add(jButtonMap.get(JButtonTypeEnum.SaveExp));		
		jPanelLoadSave.add(jButtonMap.get(JButtonTypeEnum.LoadExp));
		
		jPanelForComponentsWest.add(jPanelPlot);
		jPanelForComponentsWest.add(jPanelLoadSave);
		
		JPanel jpanelClear = new JPanel(new GridLayout(1,2));
		jpanelClear.add(jCheckBoxClearTextArea);
		jpanelClear.add(jButtonMap.get(JButtonTypeEnum.Clear));
		
		jPanelForComponentsWest.add(jpanelClear);
		jPanelForComponentsWest.add(jButtonMap.get(JButtonTypeEnum.Exit));

		jPanelForComponentsEast.add(new JScrollPane(jTextArea));

		JPanel jPanelForComponents = new JPanel(new BorderLayout());
		jPanelForComponents.add(jPanelForComponentsWest, BorderLayout.WEST);
		jPanelForComponents.add(jPanelForComponentsEast, BorderLayout.EAST);
		contentPane.add(jPanelForComponents,BorderLayout.CENTER);
        
		this.setVisible(true);
		this.setResizable(false);
		//this.pack();
	}


	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {  
		Object source = e.getSource();
		if(source instanceof JButton){
			String cmd = e.getActionCommand(); 
			JButtonTypeEnum jButtonTypeEnum = JButtonTypeEnum.valueOf(cmd);
			switch(jButtonTypeEnum){
				case Exit:
					System.exit(0);
					break;
				case Plot:
					this.jButtonMap.get(JButtonTypeEnum.DrawAxes).doClick();
					if(functionForm!=null && functionForm.getListOfExpressionList()!=null){
						double l=-5.0;
						double h=+5.0;
						try{
							 l = Double.parseDouble(jTextFieldLowerLimit.getText());
							 h = Double.parseDouble(jTextFieldHigherLimit.getText());
						}
						catch(NumberFormatException nfe){
							JOptionPane.showMessageDialog(this, 
										"Lower and Upper limits must be double values.\nThe values will be defaulted to -5.0 and +5.0.",
											"Error", JOptionPane.ERROR_MESSAGE);
							jTextFieldLowerLimit.setText("-5.0");
							jTextFieldHigherLimit.setText("+5.0");
						}
						
						functionForm.setH(h);
						functionForm.setL(l);
						functionForm.setColor(color);
						plane.addFunctionForm(functionForm,jCheckBox[1].isSelected(), jCheckBoxAreaBound.isSelected());
					}else
						JOptionPane.showMessageDialog(this, "Please build an expression first!",
																			"Error", JOptionPane.ERROR_MESSAGE);
					break;
				case SetColor:
					int i = 0;
					for(JCheckBox cbox:this.jCheckBox){
						if(cbox.isSelected()){
							Color _c = JColorChooser.showDialog(this, "Pick a color for "+cbox.getLabel(), Color.BLACK);
							if(_c!=null){
								color[i]=_c;
								cbox.setBackground(color[i]);
							}
						}
						i++;
					}
					break;
				case BuildExp:
					Subject s = new ExpressionBuilderFrame(FunctionFormType.valueOf((String)this.jComboBoxFormType.getSelectedItem()));
					s.register((ExpressionBuilderEventListener)this);
					break;
				case Clear:
					if(this.jCheckBoxClearTextArea.isSelected()){
						this.jTextArea.setText("");
						this.functionForm=null;
					}
					if(plane!=null){
						plane.addFunctionForm(null,jCheckBox[1].isSelected(),jCheckBoxAreaBound.isSelected() );
					}
					break;
				case DrawAxes:
					if(plane==null){
						plane = new CartesianPlaneFrame(this);
					}
					break;
				case ZoomIn:
					if(plane!=null){
						if(Constants.SCALE_FACTOR>=10 && Constants.SCALE_FACTOR<80){
							plane.disposeGui();
							Constants.SCALE_FACTOR*=2;
							Constants.NO_OF_LINES/=2;
							plane = new CartesianPlaneFrame((CartesianPlaneEventListener)this);
							if(functionForm!=null && functionForm.getListOfExpressionList()!=null)
								this.jButtonMap.get(JButtonTypeEnum.Plot).doClick();
						}
					}
					break;
				case ZoomOut:
					if(plane!=null){
						if(Constants.SCALE_FACTOR>10 && Constants.SCALE_FACTOR<=80){
							plane.disposeGui();
							Constants.SCALE_FACTOR/=2;
							Constants.NO_OF_LINES*=2;
							plane = new CartesianPlaneFrame((CartesianPlaneEventListener)this);
							if(functionForm!=null && functionForm.getListOfExpressionList()!=null)
								this.jButtonMap.get(JButtonTypeEnum.Plot).doClick();
							
						}
					}
					break;
				case SaveExp:
					if(this.functionForm!=null){
						try (RandomAccessFile raf = new RandomAccessFile("functions.dat", "rw");){
							/*
							List<FunctionForm> functionFormList=null;
							if(raf.length()==0L) {
								functionFormList = new ArrayList<>();
							}
							else {
								byte[] arr = new byte[(int) raf.length()];
								raf.read(arr);
								functionFormList  = (List<FunctionForm>)ObjectUtil.deserialize(arr);
							}
							functionFormList.add(functionForm);
							*/
							//long pointer = raf.getFilePointer();
							raf.seek(0);
							//raf.write(ObjectUtil.serialize(functionFormList));
							raf.write(ObjectUtil.serialize(this.functionForm));
							
						} catch (FileNotFoundException foe) {
							logger.error(foe);
							JOptionPane.showMessageDialog(this, "ExpressionImpl could not be saved!",
									"Error", JOptionPane.ERROR_MESSAGE);
						} catch (IOException ioe) {
							logger.error(ioe);
							JOptionPane.showMessageDialog(this, "ExpressionImpl could not be saved!",
									"Error", JOptionPane.ERROR_MESSAGE);
						} /*catch (ClassNotFoundException cnfe) {
							logger.error(cnfe);
							JOptionPane.showMessageDialog(this, "ExpressionImpl could not be loaded!",
									"Error", JOptionPane.ERROR_MESSAGE);
						}*/
					}
					break;
				case LoadExp:
					try (RandomAccessFile raf = new RandomAccessFile("functions.dat", "r")){
						byte[] arr = new byte[(int) raf.length()];
						raf.read(arr);
						this.functionForm=(FunctionForm)ObjectUtil.deserialize(arr);
						/*
						List<FunctionForm> functionFormList =(List<FunctionForm>)ObjectUtil.deserialize(arr);
						this.jTextArea.setText("");
						int counter=1;
						for(FunctionForm f: functionFormList)
							this.updateTextArea(f, counter++);
						*/
						this.updateTextArea((FunctionForm)ObjectUtil.deserialize(arr), 1);
					} catch (FileNotFoundException foe) {
						logger.error(foe);
						JOptionPane.showMessageDialog(this, "ExpressionImpl could not be loaded!",
								"Error", JOptionPane.ERROR_MESSAGE);
						
					} catch (IOException ioe) {
						logger.error(ioe);
						JOptionPane.showMessageDialog(this, "ExpressionImpl could not be loaded!",
								"Error", JOptionPane.ERROR_MESSAGE);
						
					} catch (ClassNotFoundException cnfe) {
						logger.error(cnfe);
						JOptionPane.showMessageDialog(this, "ExpressionImpl could not be loaded!",
								"Error", JOptionPane.ERROR_MESSAGE);

					}
					break;
			}
		}
	}
	public void areaCalculationUpdate(double[] area){
		if(area!=null){
			this.area=area;
			updateTextArea(this.functionForm, 1);
		}
	}

	@Override
	public void windowClosureUpdate(){
		this.plane=null;
	}
	
	@Override
	public void expressionListUpdate(/*List<Expression> expressions*/FunctionForm functionForm){
		
		if(functionForm!=null){
			//this.functionForm=functionForm;
			updateTextArea(functionForm, 1);
		}
	}
	
	private synchronized void updateTextArea(FunctionForm functionForm, int n){
		if(functionForm!=null){
			if(
				(functionForm.getType()==FunctionFormType.Functional && 
					(functionForm.getListOfExpressionList()==null || functionForm.getListOfExpressionList().size()<1)) ||
				(functionForm.getType()==FunctionFormType.Polar && 
					(functionForm.getListOfExpressionList()==null || functionForm.getListOfExpressionList().size()<1)) 					
				
			  )
			{
				JOptionPane.showMessageDialog(this, "Polar and Funtion form require one function",
						"Information", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			if((functionForm.getType()==FunctionFormType.Parametric && 
					(functionForm.getListOfExpressionList()==null || functionForm.getListOfExpressionList().size()<2)))
			{
				JOptionPane.showMessageDialog(this, "Parametric form requires two functions",
						"Information", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			this.functionForm=functionForm;
			StringBuilder buffer = new StringBuilder("Expression");
			buffer.append(n);
			int counter = 0;
			for(List<Expression> expressions:functionForm.getListOfExpressionList()){
				if(expressions!=null){
					buffer.append(Constants.NEW_LINE);
					switch(functionForm.getType()){
						case Functional:
							buffer.append("y=");
							break;
						case Polar:
							buffer.append("r=");
							break;
						case Parametric:
							if(counter==0){
								buffer.append("x=");
							}
							else{
								buffer.append("y=");
							}
					}
					
					buffer.append('f')
					      .append(Constants.LEFT_PARENTHESES)
					      .append(functionForm.getVar())
					      .append(Constants.RIGHT_PARENTHESES)
					      .append(Constants.EQUAL);
					
					for(int i = 0;i<expressions.size();i++){
						if(expressions.size()-i==1)
							buffer.append(expressions.get(i)).append(Constants.NEW_LINE);
						else
							buffer.append(expressions.get(i)).append(Constants.SPACE).append(Operation.Add.operation()).append(Constants.SPACE);
					}
					if(jCheckBox[1].isSelected()){
						
						switch(functionForm.getType()){
							case Functional:
								buffer.append("dy/d");
								break;
							case Polar:
								buffer.append("dr/d");
								break;
							case Parametric:
								if(counter==0){
									buffer.append("dx/d");
								}
								else{
									buffer.append("dy/d");
								}
						}
						buffer.append(functionForm.getVar()).append(Constants.EQUAL);
						buffer.append('d')
						        .append(Constants.LEFT_PARENTHESES);
						for(int i = 0;i<expressions.size();i++){
							if(expressions.size()-i==1)
								buffer.append(expressions.get(i)).append(Constants.RIGHT_PARENTHESES);
							else
								buffer.append(expressions.get(i)).append(Constants.SPACE).append(Operation.Add.operation()).append(Constants.SPACE);
						}
						buffer.append(Constants.FORWARD_SLASH)
						      .append('d')
						      .append(functionForm.getVar());
						
					}
					counter++;
				}				
			}
			String areaToken=null;
			try{
				if(area!=null){
					areaToken=new java.text.DecimalFormat("#.00 sq units").format(area[0]);
					buffer.append("\nArea bound by f(x): ").append(areaToken).append(Constants.NEW_LINE);
				}
			}
			catch(ArrayIndexOutOfBoundsException e){}
			if(jCheckBox[1].isSelected()){
				if(functionForm.getType()==FunctionFormType.Parametric){
					buffer.append("\ndy/dx=(dy/ds)/(dx/ds)\n");
				}
				try{
					if(area!=null){
						areaToken=new java.text.DecimalFormat("#.00 sq units").format(area[1]);
						buffer.append("Area bound by f'(x): ").append(areaToken).append(Constants.NEW_LINE);
					}
				}
				catch(ArrayIndexOutOfBoundsException e){}
			}
			//jTextArea.append(buffer.toString());
			jTextArea.setText(buffer.toString());
			area=null;
		}
	}
	
	public static void main(String[] str) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					@SuppressWarnings("unused")
					JGraphApplication jGraphApplication = new JGraphApplication();
					
				} catch (Throwable e) {
					final StringBuilder builder = new StringBuilder();
					
					Arrays.stream(e.getStackTrace())
									.forEach(s->builder.append(s));
					logger.error(builder.toString());
				}
            }
        });
	}
}

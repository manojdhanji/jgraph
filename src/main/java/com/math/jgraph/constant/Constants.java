package com.math.jgraph.constant;


public class Constants{
	private Constants(){}
	public static final String TEST = "test";
	
	public static final int N_THREADS = 50;
	public static int SCALE_FACTOR = 20;
	public static int NO_OF_LINES = 40;
	public static final int X_LENGTH = Constants.SCALE_FACTOR*Constants.NO_OF_LINES;
	public static final int Y_LENGTH = Constants.SCALE_FACTOR*Constants.NO_OF_LINES;

	public static final double TRANSFORM_FACTOR = (Constants.SCALE_FACTOR*Constants.NO_OF_LINES)/2;
	public static final double INCREMENTAL_STEP = Boolean.getBoolean(TEST)?0.9:0.0001;
	
	public static final char ADD = '\u002B';
	public static final char SUBTRACT = '\u002D';
	public static final char MULTIPLY = '\u00D7';
	public static final char DIVIDE = '\u2044';
	public static final char SQUARE_ROOT = '\u221A';
	public static final char CUBE_ROOT = '\u221B';
	public static final char INTEGRAL = '\u222B';
	public static final char SPACE = ' ';
	public static final char NEW_LINE = '\n';
	public static final char LEFT_PARENTHESES = '(';
	public static final char RIGHT_PARENTHESES = ')';
	public static final char VARIABLE_X = 'x';
	public static final char VARIABLE_S = 's';
	public static final char VARIABLE_THETA = '\u03F4';
	public static final char EQUAL = '=';
	public static final char FORWARD_SLASH = '/';
	public static final char COLON = ':';
}

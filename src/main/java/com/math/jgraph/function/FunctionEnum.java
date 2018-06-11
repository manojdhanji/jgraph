package com.math.jgraph.function;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.math.jgraph.constant.Constants;
public enum FunctionEnum {

	log10("log"),
	log("ln"),
	sin("sin"),
	cos("cos"),
	tan("tan"),
	asin("arcsin"),
	acos("arccos"),
	atan("arctan"),
	sinh("sinh"),
	cosh("cosh"),
	tanh("tanh"),
	pow("x^x"),
	cbrt(String.valueOf(Constants.CUBE_ROOT)),
    sqrt(String.valueOf(Constants.SQUARE_ROOT)),
	exp("e^x"),
	expm1("e^x-1");
	
	static Map<String, Method> map = new HashMap<String, Method>();
	static String[] names = new String[FunctionEnum.values().length];
	static{
		Arrays
		.stream(Math.class.getMethods())
		.filter(m->Modifier.isStatic(m.getModifiers()))
		.forEach(
			m->
			Arrays.stream(FunctionEnum.values())
			.filter(f->f.name().equals(m.getName()))
			.forEach(
					f->
					map.put(f.name(), m)
			)
		);
		names=
			Arrays.stream(FunctionEnum.values())
			  .map(e->e.function)
			  .collect(Collectors.toList())
			  .toArray(new String[FunctionEnum.values().length]);
	}
	
	private String function;
	private FunctionEnum(String function){
		this.function=function;
	}
	public Method method(){
		return map.get(this.name());
	}
	public static String[] names(){
		return names;
	}
	public String friendlyName(){
		return function;
	}
	public static FunctionEnum  getFunctionEnum(final String arg){
		return 
			Arrays
			.stream(FunctionEnum.values())
			.filter(e->e.function.equals(arg))
			.map(e->e)
			.findFirst()
			.orElse(null);
	}
	
	/*public static void main(String[] args){
		System.out.println(log.name());
		System.out.println(FunctionEnum.getFunctionEnum("ln"));
		System.out.println(FunctionEnum.valueOf("log10"));
		System.out.println(log.method());
		for(String name: names){
			System.out.println(name);
		}
	}*/
}

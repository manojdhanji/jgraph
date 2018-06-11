package com.math.jgraph;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
public class ObjectUtil
{
	static final Logger logger;
	static{
		DOMConfigurator.configure("log4j-graph.xml");
		logger = Logger.getLogger(ObjectUtil.class);
	}
	public static Object cloneObject (Object obj) {
		Object copy = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ois = new ObjectInputStream(bais);
			copy = (Object)ois.readObject();
			
		}
		catch(IOException e){
			logger.error(e);
		}
		catch(ClassNotFoundException e){
			logger.error(e);
		}
		finally{
			if(oos!=null){
				try{
					oos.close();
				}
				catch(IOException ioe){
				}
			}
			if(ois!=null){
				try{
					ois.close();
				}
				catch(IOException ioe){
				}
			}
		}
		return copy;
	}
	/**
	 * 
	 * @param obj <code>java.lang.Object</code>
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] serialize(Object obj)throws IOException {     
		  
		 ObjectOutputStream os = null;
		 try{
			 ByteArrayOutputStream out = new ByteArrayOutputStream() ;     
			 os = new ObjectOutputStream(out);     
			 os.writeObject(obj);     
			 return out.toByteArray();
		}
		finally{
			if(os!=null)
				try{
					os.close();
				}
				catch(IOException ioe){}
		}
	} 
	/**
	 * Uses ObjectInputStream based on a ByteArrayInputStream to read the bytes into an object
	 * @param data <code>byte[]</code>
	 * @return java.lang.Object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException{  
		 ObjectInputStream is = null;
		 try{
			ByteArrayInputStream in = new ByteArrayInputStream(data);     
			is = new ObjectInputStream(in);     
			return is.readObject(); 
		 }
		 finally{
			 if(is!=null)
				try{
					is.close();
				}
				catch(IOException ioe){}
		 }
	} 
}

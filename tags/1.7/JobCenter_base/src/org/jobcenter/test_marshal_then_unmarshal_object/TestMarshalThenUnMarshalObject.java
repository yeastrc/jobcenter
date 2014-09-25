package org.jobcenter.test_marshal_then_unmarshal_object;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.jobcenter.base_exceptions.TestMarshalThenUnMarshalObjectException;


/**
 * Take an object and Marshal and Unmarshal it to test that it parses properly
 *
 */
public class TestMarshalThenUnMarshalObject {

	private static final String XML_ENCODING = "UTF-8";
	
	
	private static TestMarshalThenUnMarshalObject instance = new TestMarshalThenUnMarshalObject();
	
	
	/**
	 * Map of JAXBContext objects by the classes they are built with
	 */
	private Map<Class,JAXBContext> jaxbContextMap = new HashMap<Class, JAXBContext>();
	
	/**
	 * private constructor
	 */
	private TestMarshalThenUnMarshalObject() {}
	
	
	public static TestMarshalThenUnMarshalObject getInstance() {
		
		return instance;
	}
	
	
	 
	/**
	 * @param string1
	 * @throws TestMarshalThenUnMarshalObjectException - if marshal or unmarshal failed 
	 * @throws JAXBException if error creating JAXBContext, Marshaller, or Unmarshaller
	 */
	public void testMarshalThenUnMarshalSingleString( String string1  ) throws JAXBException, TestMarshalThenUnMarshalObjectException  {
		
		TestMarshalThenUnMarshalHolderThreeStrings holder = new TestMarshalThenUnMarshalHolderThreeStrings();
		
		holder.string1 = string1;
		
		testMarshalThenUnMarshalObject( holder, TestMarshalThenUnMarshalHolderThreeStrings.class );
	}
	 
	/**
	 * @param string1
	 * @param string2
	 * @throws TestMarshalThenUnMarshalObjectException - if marshal or unmarshal failed 
	 * @throws JAXBException if error creating JAXBContext, Marshaller, or Unmarshaller
	 */
	public void testMarshalThenUnMarshalTwoString( String string1, String string2 ) 
			throws JAXBException, TestMarshalThenUnMarshalObjectException  {
		
		TestMarshalThenUnMarshalHolderThreeStrings holder = new TestMarshalThenUnMarshalHolderThreeStrings();
		
		holder.string1 = string1;
		holder.string2 = string2;
		
		testMarshalThenUnMarshalObject( holder, TestMarshalThenUnMarshalHolderThreeStrings.class );
	}
	 
	/**
	 * @param string1
	 * @param string2
	 * @param string3
	 * @throws TestMarshalThenUnMarshalObjectException - if marshal or unmarshal failed 
	 * @throws JAXBException if error creating JAXBContext, Marshaller, or Unmarshaller
	 */
	public void testMarshalThenUnMarshalThreeString( String string1, String string2, String string3  ) 
			throws JAXBException, TestMarshalThenUnMarshalObjectException  {
		
		TestMarshalThenUnMarshalHolderThreeStrings holder = new TestMarshalThenUnMarshalHolderThreeStrings();
		
		holder.string1 = string1;
		holder.string2 = string2;
		holder.string3 = string3;
		
		testMarshalThenUnMarshalObject( holder, TestMarshalThenUnMarshalHolderThreeStrings.class );
	}
		
	
	/**
	 * Take an object and Marshal to UTF-8 XML and Unmarshal it to test that it parses properly
	 * 
	 * @param objectToTest - Must have @XmlRootElement on class definition
	 * @param class of provided object
	 * 
	 * @return byte array of marshaled object

	 * @throws TestMarshalThenUnMarshalObjectException - if marshal or unmarshal failed 
	 * @throws JAXBException if error creating JAXBContext, Marshaller, or Unmarshaller
	 */
	public byte[] testMarshalThenUnMarshalObject( Object objectToTest, Class clazz  ) throws JAXBException, TestMarshalThenUnMarshalObjectException  {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream( 1000 );
		
		JAXBContext jaxbContext = getJAXBContext( clazz );

		Marshaller marshaller = jaxbContext.createMarshaller();

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		
		try {
			marshaller.setProperty( Marshaller.JAXB_ENCODING, XML_ENCODING );

			marshaller.marshal( objectToTest, baos ) ;
			
			byte[] marsalledObjectByteArray = baos.toByteArray();

			ByteArrayInputStream bais = new ByteArrayInputStream( marsalledObjectByteArray );

			Object unmarshalledObject = unmarshaller.unmarshal( bais );

			return marsalledObjectByteArray;
			
		} catch ( Exception ex ) {
			
			throw new TestMarshalThenUnMarshalObjectException( ex );
		}
	}
	
	
	/**
	 * @param clazz
	 * @return
	 * @throws JAXBException
	 */
	private synchronized JAXBContext getJAXBContext( Class clazz ) throws JAXBException {
		
		JAXBContext jaxbContext = jaxbContextMap.get( clazz );
		
		if ( jaxbContext == null ) {
		
			jaxbContext = JAXBContext.newInstance( clazz );
			
			jaxbContextMap.put( clazz, jaxbContext );
		}
		
		return jaxbContext;
	}
	
	
	//////////////////////////////////
	
	///   private classes for test marshaling 
	
	@XmlRootElement(name="test")
	public static class TestMarshalThenUnMarshalHolderThreeStrings {

		private String string1;
		private String string2;
		private String string3;
		
		public String getString3() {
			return string3;
		}
		public void setString3(String string3) {
			this.string3 = string3;
		}
		public String getString1() {
			return string1;
		}
		public void setString1(String string1) {
			this.string1 = string1;
		}
		public String getString2() {
			return string2;
		}
		public void setString2(String string2) {
			this.string2 = string2;
		}
	}

}

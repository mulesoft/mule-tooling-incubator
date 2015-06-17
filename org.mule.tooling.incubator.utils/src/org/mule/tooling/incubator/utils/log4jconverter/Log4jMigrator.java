package org.mule.tooling.incubator.utils.log4jconverter;

import java.io.BufferedWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;

import org.mule.tooling.incubator.utils.xml.XMLDomHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Log4jMigrator {
	
	private static final String APPENDER_ROLLING_FILE = "RollingFile";

	private static final String APPENDER_CONSOLE = "Console";

	private static final String APPENDER_DAILY_ROLLING_FILE = "DailyRollingFile";

	private static final String APPENDER_PATTERN = "pattern";

	private static final String APPENDER_TYPE = "type";

	private final Properties originalProperties;
	
	private HashMap<String, HashMap<String, String>> appenders;
	private HashMap<String, List<String>> loggers;
	private String rootLevel;
	private List<String> rootAppenders;
	
	public Log4jMigrator(Properties originalProperties) {
		this.originalProperties = originalProperties;
	}
	
	
	public synchronized void convert(Writer outputWriter) throws Exception {
		
		//initialize instance variables
		appenders = new HashMap<String, HashMap<String, String>>();
		loggers = new HashMap<String, List<String>>();
		rootLevel = null;
		rootAppenders = new LinkedList<String>();
		
		if (originalProperties == null) {
			throw new IllegalStateException("originalProperties must not be null");
		}
		
		parseOriginalFile();
		writeTargetXml(new BufferedWriter(outputWriter));
		
	}


	private void parseOriginalFile() {
		
		for (Object k : originalProperties.keySet()) {
			String key = k.toString();
			//get the value
			String value = originalProperties.getProperty(key);
			
			//check for various conditions:
			if (key.startsWith("log4j.appender")) {
				//this is an appender
				parseAppender(key, value);
				continue;
			}
			
			if (key.startsWith("log4j.logger")) {
				//this is a logger.
				parseLogger(key, value);
				continue;
			}
			
			if (key.startsWith("log4j.rootCategory") || key.startsWith("log4j.rootLogger")) {
				//the root logger configuration
				parseRootLogger(key, value);
				continue;
			}
			
			//TODO - log warning.
			
		}
		
		
		
	}


	private void parseRootLogger(String key, String value) {
		String[] parts = value.split(",");
		
		//level should be the first item
		rootLevel = parts[0];
		
		for (int i = 1 ; i < parts.length ; i++) {
			rootAppenders.add(parts[i]);
		}
		
	}


	private void parseLogger(String key, String value) {
		String loogerName = key.substring("log4j.logger.".length());
		List<String> loggerLoggers = Arrays.asList(value.split(","));
		loggers.put(loogerName, loggerLoggers);
	}


	private void parseAppender(String key, String value) {
		
		String[] keyParts = key.split("\\.");
		String name = safeArrayAccess(keyParts, 2);
		String property = safeArrayAccess(keyParts, 3);
		String extra = safeArrayAccess(keyParts, 4);
		
		//get appender config
		HashMap<String, String> appender = safeGetAppender(name);
		
		if (property == null) {
			if ("org.apache.log4j.ConsoleAppender".equals(value)) {
				appender.put(APPENDER_TYPE, APPENDER_CONSOLE);
			} else if ("org.apache.log4j.DailyRollingFileAppender".equals(value)) {
				appender.put(APPENDER_TYPE, APPENDER_DAILY_ROLLING_FILE);
			} else if ("org.apache.log4j.RollingFileAppender".equals(value)) {
				appender.put(APPENDER_TYPE, APPENDER_ROLLING_FILE);
			} else {
				//TOOD - Log warning
			}
		} else {
			appender.put(property, value);
		}
		
		if (extra != null) {
			appender.put(APPENDER_PATTERN, value);
		}
		
	}


	private HashMap<String, String> safeGetAppender(String name) {
		
		if (!appenders.containsKey(name)) {
			appenders.put(name, new HashMap<String, String>());
		}
		
		return appenders.get(name);
	}


	private String safeArrayAccess(String[] array, int i) {
		
		if (array == null) {
			return null;
		}
		
		if (i >= array.length) {
			return null;
		}
		
		return array[i];
	}


	private void writeTargetXml(BufferedWriter out) throws Exception {
		
		//TODO - MODULARIZE THIS METHOD
		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
		doc.setXmlVersion("1.0");
		doc.setXmlStandalone(true);
		
		//make our lives easier
		XMLDomHelper domHelper = new XMLDomHelper(doc);
		
// TODO - IMPLEMENT THIS		
//		builder.setOmitNullAttributes(true);
		
		
		
		Element rootElement = domHelper.createRootElement("Configuration");
		Element appendersElm = domHelper.createElement("Appenders", rootElement);
		Element loggersElm = domHelper.createElement("Loggers", rootElement);
		
		
		//create the appenders
		for(String appenderName : appenders.keySet()) {
			
			HashMap<String, String> appender = appenders.get(appenderName);
			
			String type = appender.get(APPENDER_TYPE);
			
			Element appenderElm = domHelper.createElement(type, appendersElm); 
			
			//configure the particular appender
			appenderElm.setAttribute("name", appenderName);
			
			
			//configure the layout for this appender, currently only supported PatternLayout
			Element patternLayoutElm = domHelper.createElement("PatternLayout", appenderElm);
			
			//set the pattern, this is backwards compatible
			patternLayoutElm.setAttribute("pattern", appender.get("pattern"));
			
			
			
			
			switch(type) {
				case APPENDER_CONSOLE: {
					appenderElm.setAttribute("target", "SYSTEM_OUT");
				};
				break;
				case APPENDER_DAILY_ROLLING_FILE: {
					appenderElm.setAttribute("fileName", appender.get("File"));
					appenderElm.setAttribute("filePattern", appender.get("File") + "-%d{" + appender.get("DatePattern") + "}" );
					
					domHelper.insertComment("TODO filePattern is autogenerated. Please review.", appenderElm);
					
					Element policiesElm = domHelper.createElement("Policies", appenderElm);
					
					if (appender.containsKey("MaxFileSize")) {
						Element sizeBasedPolicyElm = domHelper.createElement("SizeBasedTriggeringPolicy", policiesElm);
	                    sizeBasedPolicyElm.setAttribute("size", appender.get("MaxFileSize"));
					}
					
					
					domHelper.createElement("TimeBasedTriggeringPolicy", appenderElm);
				};
				break;
				case APPENDER_ROLLING_FILE: {
					appenderElm.setAttribute("fileName", appender.get("File"));
					appenderElm.setAttribute("filePattern", appender.get("File") + ".%i");
					
					Element policiesElm = domHelper.createElement("Policies", appenderElm);
					
					Element sizeBasedPolicyElm = domHelper.createElement("SizeBasedTriggeringPolicy", policiesElm);
                    sizeBasedPolicyElm.setAttribute("size", appender.get("MaxFileSize"));
                    
                    Element rolloverStratElm = domHelper.createElement("DefaultRolloverStrategy", appenderElm);
                    rolloverStratElm.setAttribute("max", appender.get("MaxBackupIndex"));
                    
				};
				break;
			}
			
		}
		
		
		
		//write the loggers
		for(String loggerName : loggers.keySet()) {
			List<String> values = loggers.get(loggerName);
			
			Element asyncLoggerElm = doc.createElement("AsyncLogger");
			loggersElm.appendChild(asyncLoggerElm);
			
			
			asyncLoggerElm.setAttribute("name", loggerName);
			asyncLoggerElm.setAttribute("level", values.get(0));

			for(int i = 1 ; i < values.size(); i++) {
				Element appenderRefElm = doc.createElement("AppenderRef");
				asyncLoggerElm.appendChild(appenderRefElm);				
				appenderRefElm.setAttribute("ref", values.get(i));
			}
		}
		
		Element asyncRootElm = doc.createElement("AsyncRoot");
		loggersElm.appendChild(asyncRootElm);
		asyncRootElm.setAttribute("level", rootLevel);		
		
		
		domHelper.writeToWriter(out, 4);
		
	}

}

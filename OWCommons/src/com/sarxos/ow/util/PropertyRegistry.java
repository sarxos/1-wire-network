package com.sarxos.ow.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * @author Bartosz Firyn (SarXos)
 * @deprecated
 */
public class PropertyRegistry {

	public static Map <String, Properties> propertiesMap = new Hashtable<String, Properties>();
	
	public static Properties getProperties(String filen) {
		Properties p = propertiesMap.get(filen); 
		if(p == null) {
			p = new Properties();
			try {
				p.load(new FileInputStream(filen));
			} catch (FileNotFoundException e) {
				// TODO: zaimplementowac info ze nie ma pliku
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: zaimplementowac info ze plik jest zablokowany
				e.printStackTrace();
			}
			if(p != null) {
				propertiesMap.put(filen, p);
			}
		}
		return p;
	}
}

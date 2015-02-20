package es.inf.uc3m.kr.smartgit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class FileUserLoginIDProperties {
	PropertyResourceBundle pr;

	public FileUserLoginIDProperties(String filename) throws IOException {
		FileInputStream fis = new FileInputStream(new File(filename));
		this.pr = new PropertyResourceBundle(fis);
	}

	public Enumeration<String> getKeys(){
		return this.pr.getKeys();
	}
	public String getString(String key) {
		try {
			return this.pr.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}

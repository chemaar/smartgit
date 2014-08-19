package es.inf.uc3m.kr.smartgit;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class GighubConfigProperties {
	private static final String BUNDLE_NAME = "es.inf.uc3m.kr.smartgit.github"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private GighubConfigProperties() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}

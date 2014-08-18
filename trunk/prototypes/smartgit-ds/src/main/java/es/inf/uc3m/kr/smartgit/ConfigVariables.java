package es.inf.uc3m.kr.smartgit;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ConfigVariables {
	private static final String BUNDLE_NAME = "es.inf.uc3m.kr.smartgit.variables"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private ConfigVariables() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}

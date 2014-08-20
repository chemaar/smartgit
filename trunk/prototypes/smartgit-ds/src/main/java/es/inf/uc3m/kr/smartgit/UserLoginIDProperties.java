package es.inf.uc3m.kr.smartgit;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class UserLoginIDProperties {
	private static final String BUNDLE_NAME = "es.inf.uc3m.kr.smartgit.users-login-id"; //$NON-NLS-1$

	public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private UserLoginIDProperties() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}

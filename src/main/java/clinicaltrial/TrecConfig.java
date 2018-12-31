package clinicaltrial;

import java.util.ResourceBundle;

public final class TrecConfig {	
	private static final ResourceBundle PROPERTIES = ResourceBundle.getBundle("config");

    /* STORAGE - ELASTICSEARCH */

    public static final String ELASTIC_CT_INDEX = getString("ELASTIC_CT_INDEX");
    public static final String ELASTIC_CT_TYPE = getString("ELASTIC_CT_TYPE");
	public static String getString(String key) {
		return PROPERTIES.getString(key);
	}

}

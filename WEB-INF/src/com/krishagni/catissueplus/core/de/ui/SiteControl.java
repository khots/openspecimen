package com.krishagni.catissueplus.core.de.ui;

import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import edu.common.dynamicextensions.domain.nui.AbstractLookupControl;

public class SiteControl extends AbstractLookupControl {

	private static final long serialVersionUID = 1L;
	
	private static final String LU_TABLE = "CATISSUE_SITE";
	
	private static final Properties LU_PV_SOURCE_PROPS = initPvSourceProps();
	

	@Override
	public void getProps(Map<String, Object> props) {
		props.put("type", "siteField");
		props.put("dataType", getDataType());
	}
	
	public void serializeToXml(Writer writer, Properties props) {
		super.serializeToXml("siteField", writer, props);
	}

	@Override
	public Properties getPvSourceProps() {
		return LU_PV_SOURCE_PROPS;
	}

	@Override
	public String getTableName() {
		return LU_TABLE;
	}

	@Override
	public String getAltKeyColumn() {
		return getValueColumn();
	}

	
	private static Properties initPvSourceProps() {
		Properties props = new Properties();
		props.put("apiUrl", "rest/ng/sites/");
		props.put("searchTermName", "name");
		props.put("resultFormat", "{{name}}");
		return props;
	}
}

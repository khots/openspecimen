package com.krishagni.catissueplus.core.de.ui;

import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import edu.common.dynamicextensions.domain.nui.AbstractLookupControl;

public class DistributionProtocolControl extends AbstractLookupControl {
	private static final long serialVersionUID = 1L;
	
	private static final String LU_TABLE = "CATISSUE_DISTRIBUTION_PROTOCOL";
	
	private static final String LU_VALUE_COLUMN = "SHORT_TITLE";
	
	private static final Properties LU_PV_SOURCE_PROPS = initPvSourceProps();

	@Override
	public void getProps(Map<String, Object> props) {
		props.put("type", "distributionProtocolField");
		props.put("dataType", getDataType());
	}
	
	public void serializeToXml(Writer writer, Properties props) {
		super.serializeToXml("distributionProtocolField", writer, props);
	}

	@Override
	public String getTableName() {
		return LU_TABLE;
	}
	
	@Override
	public Properties getPvSourceProps() {
		return LU_PV_SOURCE_PROPS;
	}
	
	@Override
	public String getValueColumn() {
		return LU_VALUE_COLUMN;
	}

	@Override
	public String getAltKeyColumn() {		
		return getValueColumn();
	}
	
	private static Properties initPvSourceProps() {
		Properties props = new Properties();
		props.put("apiUrl", "rest/ng/distribution-protocols/");
		props.put("searchTermName", "shortTitle");
		props.put("resultFormat", "{{shortTitle}}");
		return props;
	}
}

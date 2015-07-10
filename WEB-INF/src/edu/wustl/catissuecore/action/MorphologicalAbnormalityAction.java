
package edu.wustl.catissuecore.action;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.wustl.catissuecore.bizlogic.MorphologicalAbnormalityBizlogic;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.beans.NameValueBean;

public class MorphologicalAbnormalityAction extends BaseAction {

	@Override
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String action = (String) request.getParameter("action");
		String query = request.getParameter("mask") != null ? request.getParameter("mask") : "";
		String pvId = (String) request.getParameter("pvId");

		MorphologicalAbnormalityBizlogic bizLogic = new MorphologicalAbnormalityBizlogic();
		List results = null;
		if (pvId != null) {
			results = bizLogic.getChildsFromParentId(pvId);
			JSONObject obj = new JSONObject();
			Iterator iterator = results.iterator();
			JSONArray outputArray = new JSONArray();
			while (iterator.hasNext()) {
				List row = (List) iterator.next();
				String value = (String) row.get(0) + "(" + row.get(3) + ")";
				row.add(0, value);
				outputArray.put(row);
			}
			obj.put("data", outputArray);
			response.flushBuffer();
			PrintWriter out = response.getWriter();
			out.write(obj.toString());
			return null;

		}
		else if (action != null && action.equals("getRootAbnormalities")) {
			results = bizLogic.getRootNodes();
			String xml = createTreeXml(results).toString();
			response.flushBuffer();
			response.setContentType("text/xml");
			final PrintWriter out = response.getWriter();
			out.write(xml);
			return null;
		}
		else {
			List<NameValueBean> morphoList = new ArrayList<NameValueBean>();
			results = bizLogic.getAllPvs(query);

			List<String> morphoValues = new ArrayList<String>();

			Iterator iterator = results.iterator();

			List<String> values = new ArrayList<String>();

			while (iterator.hasNext()) {
				List row = (List) iterator.next();
				values.add((String) row.get(1));
				String morphValue = (String) row.get(1) + "(" + (String) row.get(3) + ")";
				morphoValues.add(morphValue);
			}
			Set<String> morphoValueSet = new TreeSet<String>(morphoValues);
			List<String> morphoValueList = new ArrayList<String>(morphoValueSet);

			Set<String> valuesSet = new TreeSet<String>(values);
			List<String> valuesList = new ArrayList<String>(valuesSet);

			for (int i = 0; i < morphoValueList.size() && i < valuesList.size(); i++) {
				morphoList.add(new NameValueBean(morphoValueList.get(i), valuesList.get(i)));
			}

			response.flushBuffer();
			response.setContentType("text/xml");
			final PrintWriter out = response.getWriter();
			out.write(createXml(morphoList).toString());
			return null;
		}
	}

	private StringBuffer createTreeXml(List results) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><tree id=\"0\">");
		Iterator iterator = results.iterator();
		while (iterator.hasNext()) {
			List row = (List) iterator.next();
			buffer.append("<item text=\"" + StringEscapeUtils.escapeXml((String) row.get(0) + "(" + row.get(3) + ")")
					+ "\" id=\"" + StringEscapeUtils.escapeXml((String) row.get(1)) + "\">");
			if (row.get(2) != null && !row.get(2).toString().isEmpty()) {
				buffer.append("<item></item>");
			}
			buffer.append("</item>");
		}
		buffer.append("</tree>");
		return buffer;
	}

	private StringBuffer createXml(Collection<NameValueBean> clinicalDiagnosisBean) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\"?>");
		buffer.append("<complete>");
		if (clinicalDiagnosisBean != null) {
			final ListIterator iterator = ((List) clinicalDiagnosisBean).listIterator();
			int count = 0;
			while (iterator.hasNext() && count < 100) {
				final NameValueBean nameValueBean = (NameValueBean) iterator.next();
				if (count == 0) {
					buffer.append("<option value='Not Specified'>Not Specified</option>");
				}
				buffer.append("<option value=\"" + StringEscapeUtils.escapeXml(nameValueBean.getValue()) + "\"><![CDATA["
						+ nameValueBean.getName() + "]]></option>");
				count++;
			}
		}
		buffer.append("</complete>");
		return buffer;
	}
}

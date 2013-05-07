
package edu.wustl.catissuecore.action;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.json.JSONException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;

import edu.wustl.catissuecore.bizlogic.CollectionProtocolBizLogic;
import edu.wustl.catissuecore.bizlogic.ComboDataBizLogic;
import edu.wustl.catissuecore.bizlogic.ConsentTrackingBizLogic;
import edu.wustl.catissuecore.bizlogic.IdentifiedSurgicalPathologyReportBizLogic;
import edu.wustl.catissuecore.bizlogic.SiteBizLogic;
import edu.wustl.catissuecore.bizlogic.SpecimenBizlogic;
import edu.wustl.catissuecore.bizlogic.SpecimenEventParametersBizLogic;
import edu.wustl.catissuecore.bizlogic.StorageContainerBizLogic;
import edu.wustl.catissuecore.bizlogic.StorageContainerForSpArrayBizLogic;
import edu.wustl.catissuecore.bizlogic.StorageContainerForSpecimenBizLogic;
import edu.wustl.catissuecore.bizlogic.SummaryBizLogic;
import edu.wustl.catissuecore.bizlogic.UserBizLogic;
import edu.wustl.catissuecore.cpSync.SyncCPThreadExecuterImpl;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.dto.BiohazardDTO;
import edu.wustl.catissuecore.dto.ConsentResponseDto;
import edu.wustl.catissuecore.dto.ConsentTierDTO;
import edu.wustl.catissuecore.dto.ExternalIdentifierDTO;
import edu.wustl.catissuecore.dto.SpecimenDTO;
import edu.wustl.catissuecore.util.CollectionProtocolUtil;
import edu.wustl.catissuecore.util.PrintUtil;
import edu.wustl.catissuecore.util.global.AppUtility;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.tags.factory.TagBizlogicFactory;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Status;
import edu.wustl.common.util.global.Validator;
import edu.wustl.dao.DAO;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.QueryWhereClause;
import edu.wustl.dao.condition.EqualClause;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.DBTypes;
import edu.wustl.dao.util.NamedQueryParam;

public class CatissueCommonAjaxAction extends DispatchAction
{

	/**
	 * Returns list of all the User of this application.
	 *
	 * @param mapping the mapping
	 * @param form the form
	 * @param request the request
	 * @param response the response
	 * @return the action forward
	 * @throws ApplicationException the application exception
	 * @throws IOException 
	 */
	public ActionForward allUserList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{
		StringBuffer responseString = new StringBuffer(Constants.XML_START);
		List<NameValueBean> userList = userList(request);
		responseString.append(Constants.XML_ROWS);
		for (NameValueBean nvb : userList)
		{
			responseString.append(this.addRowToResponseXML(Long.valueOf(nvb.getValue()), null,
					nvb.getName()));
		}
		responseString.append(Constants.XML_ROWS_END);
		response.setContentType(Constants.CONTENT_TYPE_XML);
		response.getWriter().write(responseString.toString());
		return null;
	}

	/**
	 * This function returns result in xml fromat to populate DHTMLX DropDown combo box.
	 * @param identifier if list is of (String,Long) type pass long value as row id
	 * @param stringValue if list is of (String,String) type pass String value as row id
	 * @param name - this is a display name in DropDown
	 * @return
	 */
	private String addRowToResponseXML(Long identifier, String stringValue, String name)
	{
		StringBuffer responseString = new StringBuffer(Constants.XML_ROW_ID_START);
		responseString.append((identifier == null ? stringValue : identifier))
				.append(Constants.XML_TAG_END).append(Constants.XML_CELL_START)
				.append(Constants.XML_CDATA_START).append(name).append(Constants.XML_CDATA_END)
				.append(Constants.XML_CELL_END).append(Constants.XML_ROW_END);
		return responseString.toString();
	}

	/**
	 * This function returns result in xml format to populate dhtmlxcombo box.
	 * @param identifier
	 * @param stringValue
	 * @param name name of the container
	 * @param populateValueInCombo attribute to set default selected value.
	 * @param selContMatched 
	 * @return
	 */
	private String addRowToResponseXMLForDHTMLXcombo(String stringValue, String name,
			String populateValueInCombo, boolean selContMatched)
	{
		String selected = " selected=\"1\" ";
		StringBuffer responseString = new StringBuffer("<option value=\"" + stringValue + "\"");
		if ("true".equals(populateValueInCombo) && selContMatched)
		{
			responseString.append(selected);
		}
		responseString.append(">" + name);
		responseString.append("</option>");
		return responseString.toString();
	}

	public ActionForward getUserListAsJSon(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{
		AppUtility.writeListAsJSon(userList(request), request, response);
		return null;
	}

	private List<NameValueBean> userList(HttpServletRequest request) throws BizLogicException
	{
		String operation = request.getParameter(Constants.OPERATION);
		if (operation == null)
		{
			operation = (String) request.getAttribute(Constants.OPERATION);
		}
		UserBizLogic userBizLogic = new UserBizLogic();
		final List<NameValueBean> userCollection = userBizLogic.getUsersNameValueList(operation);
		return userCollection;
	}

	/**
	 * This returns list of Clinical Diagnosis which contains specified string as an query.  
	 */
	public ActionForward getClinicalDiagnosisValues(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{
		String query = request.getParameter("query");
		ComboDataBizLogic comboDataBizObj = new ComboDataBizLogic();
		List<NameValueBean> diagnosisList = comboDataBizObj.getClinicalDiagnosisList(query, false);
		AppUtility.writeListAsJSon(diagnosisList, request, response);
		return null;
	}

	public ActionForward getAllSiteList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{
		final String[] siteDisplayField = {"name"};
		final String valueField = "id";

		final String[] activityStatusArray = {Status.ACTIVITY_STATUS_DISABLED.toString(),
				Status.ACTIVITY_STATUS_CLOSED.toString()};

		final SiteBizLogic siteBizlog = new SiteBizLogic();
		final List<NameValueBean> siteResultList = siteBizlog.getAllSiteList(Site.class.getName(),
				siteDisplayField, valueField, activityStatusArray, false);
		StringBuffer responseString = new StringBuffer(Constants.XML_START);
		NameValueBean selectBean = new NameValueBean("-- Select --", Long.valueOf(-1));
		siteResultList.remove(siteResultList.indexOf(selectBean));
		responseString.append(Constants.XML_ROWS);
		for (NameValueBean nvb : siteResultList)
		{
			responseString.append(this.addRowToResponseXML(Long.valueOf(nvb.getValue()), null,
					nvb.getName()));
		}
		responseString.append(Constants.XML_ROWS_END);
		response.setContentType(Constants.CONTENT_TYPE_XML);
		response.getWriter().write(responseString.toString());
		return null;
	}

	public ActionForward getStorageContainerListForDerivative(ActionMapping mapping,
			ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException, IOException
	{
		List<NameValueBean> containerList = new ArrayList<NameValueBean>();
		String contName = request.getParameter(Constants.CONTAINER_NAME);
		final SessionDataBean sessionData = (SessionDataBean) request.getSession().getAttribute(
				Constants.SESSION_DATA);
		DAO dao = AppUtility.openDAOSession(sessionData);
		String cp = request.getParameter(Constants.CAN_HOLD_COLLECTION_PROTOCOL);
		String parentSpecimenLabel = request.getParameter(Constants.PARENT_SPECIMEN_LABEL_KEY);
		String parentSpecimenBarcode = request.getParameter("parentSpecimenBarcode");
		if (cp == null || "".equals(cp))
		{
			if (parentSpecimenLabel != null && !"".equals(parentSpecimenLabel))
			{
				cp = CollectionProtocolUtil.getCPIdFromSpecimenLabel(parentSpecimenLabel,
						sessionData);
			}
			else if (parentSpecimenBarcode != null && !"".equals(parentSpecimenBarcode))
			{
				cp = CollectionProtocolUtil.getCPIdFromSpecimenBarcode(parentSpecimenBarcode,
						sessionData);
			}

		}
		long cpId = 0;
		if (cp != null && !"".equals(cp) && !"null".equals(cp))
		{
			cpId = Long.parseLong(cp);
		}
		String spType = request.getParameter("specimenType");
		String spClass = request.getParameter(Constants.CAN_HOLD_SPECIMEN_CLASS);
		StorageContainerForSpecimenBizLogic bizLogic = new StorageContainerForSpecimenBizLogic();
		LinkedHashMap treeMap = bizLogic.getAutoAllocatedContainerListForSpecimen(
				AppUtility.setparameterList(cpId, spClass, 0, spType), sessionData, dao, contName);
		if (treeMap != null)
		{
			containerList = AppUtility.convertMapToList(treeMap);
		}
		AppUtility.closeDAOSession(dao);
		StringBuffer responseString = new StringBuffer(Constants.XML_START);
		NameValueBean virtualBean = new NameValueBean("Virtual", Long.valueOf(-1));
		responseString.append(Constants.XML_ROWS);
		String tranferEventId = (String) request.getParameter("transferEventParametersId");
		if (tranferEventId == null || "0".equals(tranferEventId))
		{
			for (NameValueBean nvb : containerList)
			{
				responseString.append(this.addRowToResponseXML(Long.valueOf(nvb.getValue()), null,
						nvb.getName()));
			}
			responseString.append(this.addRowToResponseXML(Long.valueOf(virtualBean.getValue()),
					null, virtualBean.getName()));
		}
		responseString.append(Constants.XML_ROWS_END);
		response.setContentType(Constants.CONTENT_TYPE_XML);
		response.getWriter().write(responseString.toString());
		return null;
	}

	public ActionForward getStorageContainerList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{
		List<NameValueBean> containerList = new ArrayList<NameValueBean>();
		String contName = request.getParameter(Constants.CONTAINER_NAME);
		final SessionDataBean sessionData = (SessionDataBean) request.getSession().getAttribute(
				Constants.SESSION_DATA);
		DAO dao = AppUtility.openDAOSession(sessionData);
		long cpId = 0;
		if (!"".equals(request.getParameter(Constants.CAN_HOLD_COLLECTION_PROTOCOL))
				&& !"null".equals(request.getParameter(Constants.CAN_HOLD_COLLECTION_PROTOCOL)))
		{
			cpId = Long.parseLong(request.getParameter(Constants.CAN_HOLD_COLLECTION_PROTOCOL));
		}
		String spType = request.getParameter("specimenType");
		String spClass = request.getParameter(Constants.CAN_HOLD_SPECIMEN_CLASS);
		StorageContainerForSpecimenBizLogic bizLogic = new StorageContainerForSpecimenBizLogic();
		LinkedHashMap treeMap = bizLogic.getAutoAllocatedContainerListForSpecimen(
				AppUtility.setparameterList(cpId, spClass, 0, spType), sessionData, dao, contName);
		if (treeMap != null)
		{
			containerList = AppUtility.convertMapToList(treeMap);
		}
		AppUtility.closeDAOSession(dao);
		StringBuffer responseString = new StringBuffer(Constants.XML_START);
		NameValueBean virtualBean = new NameValueBean("Virtual", Long.valueOf(-1));
		responseString.append(Constants.XML_ROWS);
		String tranferEventId = (String) request.getParameter("transferEventParametersId");
		if (tranferEventId == null || "0".equals(tranferEventId))
		{
			for (NameValueBean nvb : containerList)
			{
				responseString.append(this.addRowToResponseXML(Long.valueOf(nvb.getValue()), null,
						nvb.getName()));
			}
			responseString.append(this.addRowToResponseXML(Long.valueOf(virtualBean.getValue()),
					null, virtualBean.getName()));
		}
		responseString.append(Constants.XML_ROWS_END);
		response.setContentType(Constants.CONTENT_TYPE_XML);
		response.getWriter().write(responseString.toString());
		return null;
	}

	/**
	 * This function returns storage container names list for dhtmlx combo box.
	 */

	public ActionForward getStorageContainerListForDHTMLXcombo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException, IOException
	{
		DAO dao = null;
		try
		{
			List<NameValueBean> containerList = new ArrayList<NameValueBean>();
			String contName = request.getParameter(Constants.CONTAINER_NAME);
			String selectedContName = request.getParameter(Constants.SELECTED_CONTAINER_NAME);
			String populateValueInCombo = request.getParameter("populateValueInCombo");
			final SessionDataBean sessionData = (SessionDataBean) request.getSession()
					.getAttribute(Constants.SESSION_DATA);
			dao = AppUtility.openDAOSession(sessionData);
			long cpId = 0;
			if (!"".equals(request.getParameter(Constants.CAN_HOLD_COLLECTION_PROTOCOL))
					&& !"null".equals(request.getParameter(Constants.CAN_HOLD_COLLECTION_PROTOCOL)))
			{
				cpId = Long.parseLong(request.getParameter(Constants.CAN_HOLD_COLLECTION_PROTOCOL));
			}
			String spType = request.getParameter("specimenType");
			String spClass = request.getParameter(Constants.CAN_HOLD_SPECIMEN_CLASS);
			StorageContainerForSpecimenBizLogic bizLogic = new StorageContainerForSpecimenBizLogic();
			LinkedHashMap treeMap = bizLogic.getAutoAllocatedContainerListForSpecimen(
					AppUtility.setparameterList(cpId, spClass, 0, spType), sessionData, dao,
					contName);
			if (treeMap != null)
			{
				containerList = AppUtility.convertMapToList(treeMap);
			}

			StringBuffer responseString = new StringBuffer(Constants.XML_START);
			responseString.append("<complete>");
			NameValueBean virtualBean = new NameValueBean("Virtual", Long.valueOf(-1));
			String tranferEventId = (String) request.getParameter("transferEventParametersId");
			boolean selContMatched = Boolean.FALSE;
			if (tranferEventId == null || "0".equals(tranferEventId))
			{
				String selectedContId = null;
				for (NameValueBean nvb : containerList)
				{
					if (nvb.getName().equals(selectedContName))
					{
						selContMatched = Boolean.TRUE;
						selectedContId = nvb.getValue();
						break;
					}
				}
				Integer i = 1;
				for (NameValueBean nvb : containerList)
				{
					if (nvb.getName().equals(selectedContName)
							|| Validator.isEmpty(selectedContName))
					{
						responseString.append(this.addRowToResponseXMLForDHTMLXcombo(
								nvb.getValue(), nvb.getName(), populateValueInCombo, true));
					}
					else
					{
						responseString.append(this.addRowToResponseXMLForDHTMLXcombo(
								nvb.getValue(), nvb.getName(), populateValueInCombo, false));
					}
					i = i + 1;
				}
				if (!selContMatched)
				{
					Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
					substParams.put("0", new NamedQueryParam(DBTypes.STRING, selectedContName));

					final List list = ((HibernateDAO) dao).executeNamedQuery(
							"getStorageContainerIdByContainerName", substParams);

					if (!list.isEmpty())
					{
						selectedContId = list.get(0).toString();
						responseString.append(this.addRowToResponseXMLForDHTMLXcombo(
								selectedContId, selectedContName, populateValueInCombo, true));
					}

				}
				//			selContMatched = (selContMatched) ? false : true;
				//			responseString.append(this.addRowToResponseXMLForDHTMLXcombo(i.toString(),
				//					virtualBean.getName(), populateValueInCombo, selContMatched));

			}
			responseString.append("</complete>");
			response.setContentType(Constants.CONTENT_TYPE_XML);
			response.getWriter().write(responseString.toString());
		}
		finally
		{
			AppUtility.closeDAOSession(dao);
		}
		return null;
	}

	public ActionForward getStorageContainerListForRequestShipment(ActionMapping mapping,
			ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException, IOException
	{
		List<NameValueBean> containerList = new ArrayList<NameValueBean>();
		Map containerMap = new TreeMap();
		final StorageContainerForSpecimenBizLogic bizLogic = new StorageContainerForSpecimenBizLogic();
		final SessionDataBean sessionDataBean = (SessionDataBean) request.getSession()
				.getAttribute(Constants.SESSION_DATA);
		final String contName = request.getParameter(Constants.CONTAINER_NAME);
		if (sessionDataBean != null)
		{
			DAO dao = null;
			try
			{
				dao = AppUtility.openDAOSession(sessionDataBean);
				String spClass = null;
				String spType = null;
				String specimenId = request.getParameter("specimenId");
				if (specimenId != null)
				{
					final String sourceObjectName = Specimen.class.getName();
					final String[] selectColumnName = {"specimenClass", "specimenType"};
					final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
					queryWhereClause.addCondition(new EqualClause("id", Long.valueOf(specimenId)));
					final List list = dao.retrieve(sourceObjectName, selectColumnName,
							queryWhereClause);
					if (list.size() != 0)
					{
						final Object[] valArr = (Object[]) list.get(0);
						if (valArr != null)
						{
							spClass = ((String) valArr[0]);
							spType = ((String) valArr[1]);
						}
					}
					String collectionProtocolId = CollectionProtocolUtil.getCPIdFromSpecimen(
							specimenId, dao);
					request.setAttribute(
							edu.wustl.catissuecore.util.global.Constants.COLLECTION_PROTOCOL_ID,
							collectionProtocolId);

					if (!collectionProtocolId.trim().equals(""))
					{
						List<Object> parameterList = AppUtility.setparameterList(
								Long.valueOf(collectionProtocolId).longValue(), spClass, 0, spType);
						containerMap = bizLogic.getAutoAllocatedContainerListForSpecimen(
								parameterList, sessionDataBean, dao, contName);
						if (containerMap != null)
						{
							containerList = AppUtility.convertMapToList(containerMap);
						}

						StringBuffer responseString = new StringBuffer(Constants.XML_START);
						NameValueBean virtualBean = new NameValueBean("Virtual", Long.valueOf(-1));
						//containerList.remove(containerList.indexOf(selectBean));
						responseString.append(Constants.XML_ROWS);
						for (NameValueBean nvb : containerList)
						{
							responseString.append(this.addRowToResponseXML(
									Long.valueOf(nvb.getValue()), null, nvb.getName()));
						}
						responseString.append(this.addRowToResponseXML(
								Long.valueOf(virtualBean.getValue()), null, virtualBean.getName()));
						responseString.append(Constants.XML_ROWS_END);
						response.setContentType(Constants.CONTENT_TYPE_XML);
						response.getWriter().write(responseString.toString());
						return null;
					}
				}
			}
			finally
			{
				if (dao != null)
				{
					dao.closeSession();
				}
			}

		}
		return null;

	}

	public ActionForward getStorageContainerListForSpecimenArray(ActionMapping mapping,
			ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException, IOException
	{
		List<NameValueBean> containerList = new ArrayList<NameValueBean>();
		String contName = request.getParameter(Constants.CONTAINER_NAME);
		String id = (String) request.getParameter(Constants.STORAGE_TYPE_ID);
		if (id != null)
		{
			final SessionDataBean sessionData = (SessionDataBean) request.getSession()
					.getAttribute(Constants.SESSION_DATA);
			DAO dao = AppUtility.openDAOSession(sessionData);
			final StorageContainerForSpArrayBizLogic spbizLogic = new StorageContainerForSpArrayBizLogic();
			TreeMap containerMap = null;/*spbizLogic
										.getAllocatedContainerMapForSpecimenArray(Long.valueOf(id), sessionData,
										dao,contName);*/
			if (containerMap != null)
			{
				containerList = AppUtility.convertMapToList(containerMap);
			}
		}
		StringBuffer responseString = new StringBuffer(Constants.XML_START);
		responseString.append(Constants.XML_ROWS);
		for (NameValueBean nvb : containerList)
		{
			responseString.append(this.addRowToResponseXML(Long.valueOf(nvb.getValue()), null,
					nvb.getName()));
		}
		responseString.append(Constants.XML_ROWS_END);
		response.setContentType(Constants.CONTENT_TYPE_XML);
		response.getWriter().write(responseString.toString());
		return null;
	}

	/**
	 * This function returns list of all active collection protocols
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws ApplicationException
	 * @throws IOException 
	 */
	public ActionForward getAllCPList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{
		CollectionProtocolBizLogic cpBizLogic = new CollectionProtocolBizLogic();
		List<NameValueBean> cpList = cpBizLogic.getAllCPNameValueBeanList();
		StringBuffer responseString = new StringBuffer(Constants.XML_START);
		responseString.append(Constants.XML_ROWS);
		for (NameValueBean nvb : cpList)
		{
			responseString.append(this.addRowToResponseXML(Long.valueOf(nvb.getValue()), null,
					nvb.getName()));
		}
		responseString.append(Constants.XML_ROWS_END);
		response.setContentType(Constants.CONTENT_TYPE_XML);
		response.getWriter().write(responseString.toString());
		return null;
	}

	public ActionForward getClinicalStatusList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{
		List<NameValueBean> csNameValueBeanList = CDEManager.getCDEManager()
				.getPermissibleValueList(Constants.CDE_NAME_CLINICAL_STATUS, null);
		NameValueBean selectBean = new NameValueBean("-- Select --", Long.valueOf(-1));
		csNameValueBeanList.remove(csNameValueBeanList.indexOf(selectBean));
		StringBuffer responseString = new StringBuffer(Constants.XML_START);
		responseString.append(Constants.XML_ROWS);
		for (NameValueBean nvb : csNameValueBeanList)
		{
			responseString.append(this.addRowToResponseXML(null, nvb.getValue(), nvb.getName()));
		}
		responseString.append(Constants.XML_ROWS_END);
		response.setContentType(Constants.CONTENT_TYPE_XML);
		response.getWriter().write(responseString.toString());
		return null;
	}

	public ActionForward startSyncCP(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{
		SyncCPThreadExecuterImpl executerImpl = SyncCPThreadExecuterImpl.getInstance();
		String jobName = request.getParameter("cpTitle");
		executerImpl.startSync(jobName,
				(SessionDataBean) request.getSession().getAttribute(Constants.SESSION_DATA));
		return null;
	}

	public ActionForward swapContainerUsingDrag(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{

		String sId = request.getParameter("sId");
		String tId = request.getParameter("tId");
		String sInd = request.getParameter("sInd");
		String tInd = request.getParameter("tInd");
		String containerName = request.getParameter("containerName");;
		String specimenLabel = request.getParameter("specimenLabel");
		String hasContainer = request.getParameter("hasContainer");
		org.json.JSONObject returnedJObject = new org.json.JSONObject();

		final SessionDataBean sessionData = (SessionDataBean) request.getSession().getAttribute(
				Constants.SESSION_DATA);
		SpecimenEventParametersBizLogic bizLogic = new SpecimenEventParametersBizLogic();
		String msg;
		try
		{
			if (Constants.TRUE.equals(hasContainer))
			{
				StorageContainerBizLogic scBizLogic = new StorageContainerBizLogic();
				msg = scBizLogic.updateStorageContainerPosition(sessionData, specimenLabel,
						containerName, tId, tInd);
			}
			else
			{
				msg = bizLogic.specimenEventTransferFromMobile(sessionData, specimenLabel,
						containerName, tId, tInd);
			}
			msg = "{'success':true,'msg':'" + msg + "'}";
		}
		catch (final ApplicationException exp)
		{
			msg = "{'success':false,'msg':'" + exp.getMessage() + "'}";
		}

		response.getWriter().write(msg);
		return null;

	}

	public ActionForward swapTreeNodeUsingDrag(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{

		String sourceText = request.getParameter("sourceText");
		String targetText = request.getParameter("targetText");
		StorageContainerBizLogic scBizLogic = new StorageContainerBizLogic();
		String responseString = "";
		try
		{
			final SessionDataBean sessionData = (SessionDataBean) request.getSession()
					.getAttribute(Constants.SESSION_DATA);

			String msg = scBizLogic.updateStorageContainerPosition(sessionData, sourceText,
					targetText, "", "");
			responseString = "{'success':true,'msg':'" + msg + "'}";
		}
		catch (final ApplicationException exp)
		{
			responseString = "{'success':false,'msg':'" + exp.getMessage() + "'}";
		}

		response.getWriter().write(responseString.toString());
		return null;

	}

	public ActionForward stopSyncCP(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{
		SyncCPThreadExecuterImpl executerImpl = SyncCPThreadExecuterImpl.getInstance();
		String jobName = request.getParameter("cpTitle");
		executerImpl.stopSync(jobName);
		return null;
	}

	public ActionForward deleteSpecimen(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String specIds = request.getParameter("specId");
		Long tagId = Long.parseLong(request.getParameter("tagId"));

		List<Long> specimenIds = new ArrayList<Long>();
		for (String specIdStr : specIds.split("\\,"))
		{
			specimenIds.add(Long.parseLong(specIdStr));
		}
		SessionDataBean sessionBean = (SessionDataBean) request.getSession().getAttribute(
				Constants.SESSION_DATA);
		Long userId = sessionBean.getUserId();

		TagBizlogicFactory.getBizLogicInstance(Constants.ENTITY_SPECIMEN_TAGITEM)
				.deleteTagItemsFromObjIds(specimenIds, tagId, userId);
		return null;
	}

	public ActionForward deleteSpecimenList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{
		Long specListId = Long.parseLong(request.getParameter("specListId"));
		SessionDataBean sessionBean = (SessionDataBean) request.getSession().getAttribute(
				Constants.SESSION_DATA);
		TagBizlogicFactory.getBizLogicInstance(Constants.ENTITY_SPECIMEN_TAG).deleteTag(
				sessionBean, specListId);
		return null;
	}

	public ActionForward getSpecimenIds(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{
		AddDeleteCartAction action = new AddDeleteCartAction();
		final List<List<String>> dataList = action.getPaginationDataList(request);
		String specIndex = request.getParameter("specIndex");
		HttpSession session = request.getSession();

		int index = 0;
		if (!Validator.isEmpty(specIndex))
		{
			index = Integer.valueOf(specIndex);
		}
		StringBuffer responseString = new StringBuffer();
		for (List<String> list : dataList)
		{
			responseString.append(list.get(index - 1));
			responseString.append(",");
		}
		session.setAttribute("specIds", responseString.toString());
		response.setContentType(Constants.CONTENT_TYPE_XML);
		response.getWriter().write(responseString.toString());
		return null;
	}

	public ActionForward getSummaryCount(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{
		SummaryBizLogic bizLogic = new SummaryBizLogic();
		Map<String, String> map = bizLogic.getSummaryCount();
		org.json.JSONObject returnedJObject = new org.json.JSONObject();

		try
		{
			returnedJObject.put("TissueCount", map.get("TissueCount"));
			returnedJObject.put("CellCount", map.get("CellCount"));
			returnedJObject.put("MoleculeCount", map.get("MoleculeCount"));
			returnedJObject.put("FluidCount", map.get("FluidCount"));
			returnedJObject.put("TotalSpecimenCount", map.get("TotalSpecimenCount"));
			returnedJObject.put(Constants.TOTAL_USER_COUNT, map.get(Constants.TOTAL_USER_COUNT));
			returnedJObject.put(Constants.TOTAL_CP_COUNT, map.get(Constants.TOTAL_CP_COUNT));
			returnedJObject.put(Constants.TOTAL_PART_COUNT, map.get(Constants.TOTAL_PART_COUNT));
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		response.setContentType("application/json");
		response.getWriter().write(returnedJObject.toString());
		return null;
	}

	public ActionForward deleteSPR(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{
		IdentifiedSurgicalPathologyReportBizLogic bizLogic = new IdentifiedSurgicalPathologyReportBizLogic();
		String reportId = request.getParameter("reportId");
		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				Constants.SESSION_DATA);
		try
		{
			bizLogic.deleteReport(Long.parseLong(reportId), sessionDataBean);
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}

		return null;
	}

	public ActionForward getInstituteLogoName(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{
		org.json.JSONObject returnedJObject = new org.json.JSONObject();
		try
		{
			String fileName = ApplicationProperties.getValue("institute.logo.path");

			int idx = fileName.contains("\\")
					? fileName.replaceAll("\\", "/").lastIndexOf("/")
					: fileName.lastIndexOf("/");
			fileName = idx >= 0 ? fileName.substring(idx + 1) : fileName;
			returnedJObject.put("logo", Constants.INSTITUTE_LOGO_WEB_PATH + fileName);
			returnedJObject.put("link", XMLPropertyHandler.getValue(Constants.INSTITUTE_LINK));
			returnedJObject.put("userManual", XMLPropertyHandler.getValue("user.manual"));
			returnedJObject.put("contactNumber", XMLPropertyHandler.getValue("contact.number"));
			returnedJObject.put("whatsNew", XMLPropertyHandler.getValue("whats.new"));
			
			response.setContentType("application/json");
			response.getWriter().write(returnedJObject.toString());
		}
		catch (JSONException e)
		{

			throw new ApplicationException(null, null, e.getMessage());
		}
		return null;
	}

	public ActionForward updateConsentTierStatus(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{
		JsonDeserializer dese = new JsonDeserializer<Date>()
		{

			DateFormat df = new SimpleDateFormat(ApplicationProperties.getValue("date.pattern"));

			@Override
			public Date deserialize(final JsonElement json, final Type typeOfT,
					final JsonDeserializationContext context) throws JsonParseException
			{
				try
				{
					return df.parse(json.getAsString());
				}
				catch (ParseException e)
				{
					return null;
				}
			}
		};

		GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(Date.class, dese);

		Gson gson = gsonBuilder.create();
		String consentTierListJSON = request.getParameter("dataJSON");
		ConsentResponseDto consentsDto = gson.fromJson(request.getParameter("consentDto"),
				ConsentResponseDto.class);
		Type listType = new TypeToken<ArrayList<ConsentTierDTO>>()
		{
		}.getType();
		List<ConsentTierDTO> ConsentTierDtoList = gson.fromJson(consentTierListJSON, listType);
		boolean disposeSpecimen = Constants.TRUE.equals(request.getParameter("disposeSpecimen"))
				? true
				: false;
		ConsentTrackingBizLogic consentTrackingBizLogic = new ConsentTrackingBizLogic();
		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				Constants.SESSION_DATA);
		DAO dao = null;
		dao = AppUtility.openDAOSession(sessionDataBean);
		consentsDto.setConsentTierList(ConsentTierDtoList);
		HashMap<String, String> returnMap = new HashMap<String, String>();
		String msg = "";
		String successString = "success";
		try
		{
			consentsDto.setDisposeSpecimen(disposeSpecimen);
			consentTrackingBizLogic.updateConsentTier(consentsDto, dao, sessionDataBean);
			returnMap.put("msg", "");
			returnMap.put("success", "success");
			dao.commit();
		}
		catch (DAOException ex)
		{
			returnMap.put("msg", ex.getMsgValues());
			returnMap.put("success", "failure");
		}
		catch (ApplicationException ex)
		{
			returnMap.put("msg", ex.getMsgValues());
			returnMap.put("success", "failure");
		}
		catch (Exception ex)
		{
			returnMap.put("msg", "Error occured at server side.");
			returnMap.put("success", "failure");
		}
		finally
		{
			AppUtility.closeDAOSession(dao);
		}
		response.setContentType("application/json");
		response.getWriter().write(gson.toJson(returnMap));

		return null;
	}

	public ActionForward updateSpecimen(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException,
			IOException
	{
		String dataJSON = request.getParameter("dataJSON");
		DAO dao = null;
		Gson gson = new Gson();
		HashMap<String, String> returnMap = new HashMap<String, String>();
		try
		{
			if (dataJSON != null)
			{
				SpecimenDTO specDTO = gson.fromJson(dataJSON, SpecimenDTO.class);

				String extidJSON = request.getParameter("extidJSON");
				java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<Collection<ExternalIdentifierDTO>>()
				{
				}.getType();

				Collection<ExternalIdentifierDTO> externalIdentifierDTOColl = gson.fromJson(
						extidJSON, listType);
				specDTO.setExternalIdentifiers(externalIdentifierDTOColl);

				String biohazardJSON = request.getParameter("biohazardJSON");
				java.lang.reflect.Type listType1 = new com.google.gson.reflect.TypeToken<Collection<BiohazardDTO>>()
				{
				}.getType();

				Collection<BiohazardDTO> biohazardDTOColl = gson.fromJson(biohazardJSON, listType1);
				specDTO.setBioHazards(biohazardDTOColl);

				SpecimenBizlogic bizlogic = new SpecimenBizlogic();
				SessionDataBean sessionDataBean = (SessionDataBean) request.getSession()
						.getAttribute(Constants.SESSION_DATA);

				dao = AppUtility.openDAOSession(sessionDataBean);
				SpecimenDTO updatedSpecimenDTO = bizlogic.updateSpecimen(dao, specDTO,
						sessionDataBean);

				dao.commit();

				returnMap.put("msg", "");
				returnMap.put("success", "success");
				returnMap.put("updatedSpecimenDTO", gson.toJson(updatedSpecimenDTO));

				boolean isToPrintLabel = Boolean.valueOf(request.getParameter("printLabel"));
				if (isToPrintLabel)
				{
					boolean printStatus = PrintUtil.printSpecimenLabel(" ", " ", request,
							specDTO.getId());
					if (printStatus)
					{
						returnMap.put("printLabelSuccess",
								ApplicationProperties.getValue("specimen.label.print.success"));
						returnMap.put("printLabel", "success.");
					}
					else
						returnMap.put("printLabelError",
								ApplicationProperties.getValue("specimen.label.print.fail"));
				}
			}
		}
		catch (Exception ex)
		{
			returnMap.put("msg", ex.getMessage());
			returnMap.put("success", "failure");
		}
		finally
		{
			AppUtility.closeDAOSession(dao);
		}
		response.setContentType("application/json");
		response.getWriter().write(gson.toJson(returnMap));

		return null;
	}

}
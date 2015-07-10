package edu.wustl.catissuecore.bizlogic;

import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;


public class MorphologicalAbnormalityBizlogic {
	
	private static final Logger LOGGER = Logger.getCommonLogger(MorphologicalAbnormalityBizlogic.class);
	
	private static String getAbnormalities = "select identifier, value, parent_identifier, CONCEPT_CODE " +
			"from catissue_permissible_value where public_id='Morphological_Abnormalitiy_PID' and lower(value) like ?";

	private static String getRootAbnormalities = "select p.value ParentName, p.identifier ID, c.TotalCout TotalCout, p.concept_code ConceptCode " +
			"from catissue_permissible_value p " +
			"left join ( " +
			"select parent_identifier, count(*) TotalCout     " +
			"from catissue_permissible_value     " +
			"where parent_identifier != identifier     " +
			"group by parent_identifier ) c on c.parent_identifier=p.identifier " +
			"WHERE p.parent_identifier is NULL and public_id='Morphological_Abnormalitiy_PID' order by p.value";

	private static String getChildAbnormalities = "select p.value ParentName, p.identifier ID, c.TotalCout TotalCout, p.concept_code ConceptCode " +
			"from catissue_permissible_value p " +
			"left join (  " +
			"select parent_identifier, count(*) TotalCout     " +
			"from catissue_permissible_value    " +
			" where parent_identifier != identifier    " +
			" group by parent_identifier ) c on c.parent_identifier=p.identifier " +
			"WHERE p.parent_identifier=? and public_id='Morphological_Abnormalitiy_PID' order by p.value";
	
	public List exeQuery(String sql, List<ColumnValueBean> parameters) {
		List list = null;
		JDBCDAO jdbcDAO = null;
		try {
			String appName = CommonServiceLocator.getInstance().getAppName();
			IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
			jdbcDAO = daofactory.getJDBCDAO();

			jdbcDAO.openSession(null);
			list = jdbcDAO.executeQuery(sql, parameters);
		}
		catch (DAOException daoExp) {
			LOGGER.debug("Could not obtain table object relation. Exception: " + daoExp.getMessage(), daoExp);
		}
		finally {
			try {
				jdbcDAO.closeSession();
			}
			catch (DAOException e) {
				LOGGER.debug(e.getMessage(), e);
			}
		}
		return list;
	}
	
	public List getChildsFromParentId(String pvId){
		List<ColumnValueBean> parameters = new ArrayList<ColumnValueBean>(); 
		parameters = new ArrayList<ColumnValueBean>(); 
		parameters.add(new ColumnValueBean(pvId));
		return exeQuery(getChildAbnormalities, parameters);
	}
	
	public List getRootNodes(){
		return exeQuery(getRootAbnormalities, new ArrayList<ColumnValueBean>());
	}
	
	public List getAllPvs(String query){
		List<ColumnValueBean> parameters = new ArrayList<ColumnValueBean>();
		parameters = new ArrayList<ColumnValueBean>();
		parameters.add(new ColumnValueBean("%" + query.toLowerCase() + "%"));
		return exeQuery(getAbnormalities, parameters);
	}

}

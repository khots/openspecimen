
package com.krishagni.catissueplus.core.administrative.services.impl;

import java.util.ArrayList;
import java.util.List;

import BoxMapAPI.IBoxMapAPI;
import bmclientapp.BoxMapSocketClient;

import com.krishagni.catissueplus.core.administrative.events.ScanContainerSpecimenDetails;
import com.krishagni.catissueplus.core.administrative.events.ScanStorageContainerDetails;
import com.krishagni.catissueplus.core.administrative.events.ScanStorageContainerDetailsEvents;
import com.krishagni.catissueplus.core.administrative.events.StorageContainersEvent;
import com.krishagni.catissueplus.core.administrative.events.ReqStorageContainerEvent;
import com.krishagni.catissueplus.core.administrative.events.ReqStorageContainersEvent;
import com.krishagni.catissueplus.core.administrative.events.StorageContainerEvent;
import com.krishagni.catissueplus.core.administrative.events.StorageContainerSummary;
import com.krishagni.catissueplus.core.administrative.services.StorageContainerService;
import com.krishagni.catissueplus.core.biospecimen.domain.Specimen;
import com.krishagni.catissueplus.core.biospecimen.repository.DaoFactory;
import com.krishagni.catissueplus.core.common.PlusTransactional;

import edu.wustl.catissuecore.domain.StorageContainer;

public class StorageContainerServiceImpl implements StorageContainerService {
	private DaoFactory daoFactory;

	public void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	@Override
	@PlusTransactional
	public StorageContainersEvent getStorageContainers(ReqStorageContainersEvent req) {		
		List<StorageContainer> containers = daoFactory.getStorageContainerDao()
				.getAllStorageContainers(req.getName(), req.getMaxResults());
		
		List<StorageContainerSummary> details = new ArrayList<StorageContainerSummary>();
		for (StorageContainer container : containers) {
			details.add(StorageContainerSummary.fromDomain(container));
		}
		
		return StorageContainersEvent.ok(details);
	}

	@Override
	@PlusTransactional
	public StorageContainerEvent getStorageContainer(ReqStorageContainerEvent req) {
		try {
			StorageContainer storageContainer = daoFactory.getStorageContainerDao()
					.getStorageContainer(req.getId());
			return StorageContainerEvent.ok(StorageContainerSummary.fromDomain(storageContainer));
		} catch (Exception e) {
			return StorageContainerEvent.serverError(e);
		}
	}

  @Override
  @PlusTransactional
  public ScanStorageContainerDetailsEvents validateAndPopulateScanContainerData(ScanStorageContainerDetails details)
  {
    try
    {
      List<StorageContainer> containers = daoFactory.getStorageContainerDao().getAllStorageContainers(
          details.getContainerName(), 1);
      details.setOneDimensionCapacity(containers.get(0).getCapacity().getOneDimensionCapacity());
      details.setTwoDimensionCapacity(containers.get(0).getCapacity().getTwoDimensionCapacity());
      details.setOneDimensionLabellingScheme(containers.get(0).getOneDimensionLabellingScheme());
      details.setTwoDimensionLabellingScheme(containers.get(0).getTwoDimensionLabellingScheme());
      for (ScanContainerSpecimenDetails specDetails : details.getSpecimenList())
      {
        Specimen spec = daoFactory.getSpecimenDao().getSpecimenByBarcode(specDetails.getBarCode());
        if(spec == null){
          specDetails.setConflict(true);
          continue;
        }
        if (spec.getSpecimenPosition() == null
            || spec.getSpecimenPosition().getPositionDimensionOne()!=Integer.parseInt(specDetails.getPosX())
            || spec.getSpecimenPosition().getPositionDimensionTwo()!= Integer.parseInt(specDetails.getPosY())
            || !spec.getSpecimenPosition().getStorageContainer().getName().equals(details.getContainerName()))
        {
          specDetails.setConflict(true);
          specDetails.setActualContainerName(spec.getSpecimenPosition().getStorageContainer().getName());
          specDetails.setActualPosX(spec.getSpecimenPosition().getPositionDimensionOne().toString());
          specDetails.setActualPosY(spec.getSpecimenPosition().getPositionDimensionTwo().toString());
        }else{
          specDetails.setConflict(false);
        }
        specDetails.setSpecimenId(spec.getId());
        specDetails.setSepcimenLable(spec.getBarcode());
      //  specDetails.setPosX(spec.getSpecimenPosition().getPositionDimensionOne().toString());
        //specDetails.setPosY(spec.getSpecimenPosition().getPositionDimensionTwo().toString());
        specDetails.setContainerName(spec.getSpecimenPosition().getStorageContainer().getName());
        specDetails.setTissueSite(spec.getTissueSite());
        specDetails.setType(spec.getSpecimenType());
      }
      return ScanStorageContainerDetailsEvents.ok(details);
    }
    catch (Exception e)
    {
      return ScanStorageContainerDetailsEvents.serverError(e);
    }
  }
  @Override
  @PlusTransactional
  public ScanStorageContainerDetailsEvents getScanContainerData(){
    ScanStorageContainerDetails details = null;
    
    int port;
    String host;
    int ret;
    port = IBoxMapAPI.DEFAULT_PORT;
    host = "LocalHost";
    
    BoxMapSocketClient client = new BoxMapSocketClient();

    ret = client.Connect(host, port);
    if (0 != ret) {
        return null;
    }
    details = client.getStorageContainerDetails();
    client.Close();
    return ScanStorageContainerDetailsEvents.ok(details);
  }
}


package com.krishagni.catissueplus.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.krishagni.catissueplus.core.administrative.events.ReqStorageContainerEvent;
import com.krishagni.catissueplus.core.administrative.events.ReqStorageContainersEvent;
import com.krishagni.catissueplus.core.administrative.events.ScanStorageContainerDetails;
import com.krishagni.catissueplus.core.administrative.events.ScanStorageContainerDetailsEvents;
import com.krishagni.catissueplus.core.administrative.events.StorageContainerEvent;
import com.krishagni.catissueplus.core.administrative.events.StorageContainerSummary;
import com.krishagni.catissueplus.core.administrative.events.StorageContainersEvent;
import com.krishagni.catissueplus.core.administrative.services.StorageContainerService;
import com.krishagni.catissueplus.core.biospecimen.events.SpecimenDetail;
import com.krishagni.catissueplus.core.biospecimen.events.SpecimenUpdatedEvent;
import com.krishagni.catissueplus.core.biospecimen.events.UpdateSpecimenEvent;

@Controller
@RequestMapping("/storage-containers")
public class StorageContainerController {
	@Autowired
	private StorageContainerService storageContainerSvc;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<StorageContainerSummary> getStorageContainers(
			@RequestParam(value = "name", required = false, defaultValue = "") String name,
			@RequestParam(value = "maxResults", required = false, defaultValue = "100") int maxResults) {
		ReqStorageContainersEvent req = new ReqStorageContainersEvent();
		req.setMaxResults(maxResults);
		req.setName(name);
		
		StorageContainersEvent resp = storageContainerSvc.getStorageContainers(req);
		if (!resp.isSuccess()) {
			resp.raiseException();
		}
		
		return resp.getContainers();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public StorageContainerSummary getStorageContainerById(@PathVariable Long id) {
		ReqStorageContainerEvent req = new ReqStorageContainerEvent();
		req.setId(id);
		StorageContainerEvent resp = storageContainerSvc.getStorageContainer(req);
		if (!resp.isSuccess()) {
			resp.raiseException();
		}
		
		return resp.getContainer();
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/validateScanData")
 @ResponseStatus(HttpStatus.OK)
 @ResponseBody
 public ScanStorageContainerDetails validateScan(@RequestBody ScanStorageContainerDetails ScanStorageContainerDetails) {
	  ScanStorageContainerDetailsEvents resp = storageContainerSvc.validateAndPopulateScanContainerData(ScanStorageContainerDetails);
	  if (!resp.isSuccess()) {
	    resp.raiseException();
	   }	   
	   return resp.getContainerDetails();
 }
	
	@RequestMapping(method = RequestMethod.GET, value = "/scanContainer")
 @ResponseStatus(HttpStatus.OK)
 @ResponseBody
 public ScanStorageContainerDetails scanContainerData() {
   ScanStorageContainerDetailsEvents resp = storageContainerSvc.getScanContainerData();
   if (!resp.isSuccess()) {
     resp.raiseException();
    }    
    return resp.getContainerDetails();
 }
}

package com.krishagni.catissueplus.core.administrative.events;

import com.krishagni.catissueplus.core.common.events.EventStatus;
import com.krishagni.catissueplus.core.common.events.ResponseEvent;


public class ScanStorageContainerDetailsEvents extends ResponseEvent 
{
  
  private ScanStorageContainerDetails containerDetails;
  private Long id;
  
  public ScanStorageContainerDetails getContainerDetails()
  {
    return containerDetails;
  }
  
  public void setContainerDetails(ScanStorageContainerDetails containerDetails)
  {
    this.containerDetails = containerDetails;
  }
  
  public Long getId()
  {
    return id;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  public static ScanStorageContainerDetailsEvents ok(ScanStorageContainerDetails details) {
    ScanStorageContainerDetailsEvents event = new ScanStorageContainerDetailsEvents();
    event.setContainerDetails(details);
    event.setStatus(EventStatus.OK);
    return event;
   }

   public static ScanStorageContainerDetailsEvents serverError(Throwable... t) {
    Throwable t1 = t != null && t.length > 0 ? t[0] : null;
    ScanStorageContainerDetailsEvents resp = new ScanStorageContainerDetailsEvents();
    resp.setStatus(EventStatus.INTERNAL_SERVER_ERROR);
    resp.setException(t1);
    resp.setMessage(t1 != null ? t1.getMessage() : null);
    return resp;
   }

   public static ScanStorageContainerDetailsEvents notFound(Long id) {
     ScanStorageContainerDetailsEvents resp = new ScanStorageContainerDetailsEvents();
    resp.setStatus(EventStatus.NOT_FOUND);
    resp.setId(id);
    return resp;
   }

}

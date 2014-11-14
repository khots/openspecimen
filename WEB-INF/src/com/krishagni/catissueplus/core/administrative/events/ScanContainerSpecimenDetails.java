package com.krishagni.catissueplus.core.administrative.events;


public class ScanContainerSpecimenDetails
{
  private String sepcimenLable;
  private Long specimenId;
  private boolean conflict;
  private String containerName;
  private String posX;
  private String posY;
  private String actualContainerName;
  private String actualPosX;
  private String actualPosY;
  private String tissueSite;
  private String type;
  private String barCode;
  
  public String getBarCode()
  {
    return barCode;
  }

  
  public void setBarCode(String barCode)
  {
    this.barCode = barCode;
  }

  public String getTissueSite()
  {
    return tissueSite;
  }
  
  public void setTissueSite(String tissueSite)
  {
    this.tissueSite = tissueSite;
  }
  
  public String getType()
  {
    return type;
  }
  
  public void setType(String type)
  {
    this.type = type;
  }



  public Long getSpecimenId()
  {
    return specimenId;
  }


  
  public void setSpecimenId(Long specimenId)
  {
    this.specimenId = specimenId;
  }


  public String getActualContainerName()
  {
    return actualContainerName;
  }

  
  public void setActualContainerName(String actualContainerName)
  {
    this.actualContainerName = actualContainerName;
  }

  
  public String getActualPosX()
  {
    return actualPosX;
  }

  
  public void setActualPosX(String actualPosX)
  {
    this.actualPosX = actualPosX;
  }

  
  public String getActualPosY()
  {
    return actualPosY;
  }

  
  public void setActualPosY(String actualPosY)
  {
    this.actualPosY = actualPosY;
  }

  public String getSepcimenLable()
  {
    return sepcimenLable;
  }
  
  public void setSepcimenLable(String sepcimenLable)
  {
    this.sepcimenLable = sepcimenLable;
  }
  
  public boolean isConflict()
  {
    return conflict;
  }
  
  public void setConflict(boolean conflict)
  {
    this.conflict = conflict;
  }
  
  public String getContainerName()
  {
    return containerName;
  }
  
  public void setContainerName(String containerName)
  {
    this.containerName = containerName;
  }
  
  public String getPosX()
  {
    return posX;
  }
  
  public void setPosX(String posX)
  {
    this.posX = posX;
  }
  
  public String getPosY()
  {
    return posY;
  }
  
  public void setPosY(String posY)
  {
    this.posY = posY;
  }

}

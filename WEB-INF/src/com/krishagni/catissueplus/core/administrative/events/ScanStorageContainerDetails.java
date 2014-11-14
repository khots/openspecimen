package com.krishagni.catissueplus.core.administrative.events;

import java.util.ArrayList;
import java.util.List;


public class ScanStorageContainerDetails
{
  private String containerName;
  private List<ScanContainerSpecimenDetails> specimenList = new ArrayList<ScanContainerSpecimenDetails>();
  private int oneDimensionCapacity;
  private int twoDimensionCapacity;
  private String twoDimensionLabellingScheme;
  private String oneDimensionLabellingScheme;
  
   
  public String getTwoDimensionLabellingScheme()
  {
    return twoDimensionLabellingScheme;
  }

  
  public void setTwoDimensionLabellingScheme(String twoDimensionLabellingScheme)
  {
    this.twoDimensionLabellingScheme = twoDimensionLabellingScheme;
  }

  
  public String getOneDimensionLabellingScheme()
  {
    return oneDimensionLabellingScheme;
  }

  
  public void setOneDimensionLabellingScheme(String oneDimensionLabellingScheme)
  {
    this.oneDimensionLabellingScheme = oneDimensionLabellingScheme;
  }

  public int getOneDimensionCapacity()
  {
    return oneDimensionCapacity;
  }
  
  public void setOneDimensionCapacity(int oneDimensionCapacity)
  {
    this.oneDimensionCapacity = oneDimensionCapacity;
  }
  
  public int getTwoDimensionCapacity()
  {
    return twoDimensionCapacity;
  }

  
  public void setTwoDimensionCapacity(int twoDimensionCapacity)
  {
    this.twoDimensionCapacity = twoDimensionCapacity;
  }

  public String getContainerName()
  {
    return containerName;
  }
  
  public void setContainerName(String containerName)
  {
    this.containerName = containerName;
  }
  
  
  public List<ScanContainerSpecimenDetails> getSpecimenList()
  {
    return specimenList;
  }
  
  public void setSpecimenList(List<ScanContainerSpecimenDetails> specimenList)
  {
    this.specimenList = specimenList;
  }
  
}

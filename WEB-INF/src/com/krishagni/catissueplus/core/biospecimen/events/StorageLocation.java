
package com.krishagni.catissueplus.core.biospecimen.events;

public class StorageLocation {
	private String containerName;

	private String positionDimensionOne;

	private String positionDimensionTwo;

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getPositionDimensionOne() {
		return positionDimensionOne;
	}

	public void setPositionDimensionOne(String positionDimensionOne) {
		this.positionDimensionOne = positionDimensionOne;
	}

	public String getPositionDimensionTwo() {
		return positionDimensionTwo;
	}

	public void setPositionDimensionTwo(String positionDimensionTwo) {
		this.positionDimensionTwo = positionDimensionTwo;
	}
}

package com.krishagni.catissueplus.core.biospecimen.events;



public class ChildCollectionProtocolSummary implements Comparable<ChildCollectionProtocolSummary>{
	private Long id;

	private String shortTitle;

	private String title;

	private String ppidFormat;

	private String cpType;
	
	private int sequenceNumber;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPpidFormat() {
		return ppidFormat;
	}

	public void setPpidFormat(String ppidFormat) {
		this.ppidFormat = ppidFormat;
	}

	public String getCpType() {
		return cpType;
	}

	public void setCpType(String cpType) {
		this.cpType = cpType;
	}
	
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@Override
	public int compareTo(ChildCollectionProtocolSummary cpSummary) {
		int returnVal=0; 
			Integer seq2 = cpSummary.getSequenceNumber();
			Integer seq1 = this.getSequenceNumber();
			
			if(seq1 != null && seq2 != null)
			{
				returnVal=seq1.compareTo(seq2);
			}
			else if(seq1 == null && seq2 == null)
			{
				returnVal=0;
			}
			else if(seq1 ==null)
			{
				returnVal=1;
			}
			else if(seq2 == null)
			{
				returnVal=-1;
			}			
		return returnVal;
	}

}


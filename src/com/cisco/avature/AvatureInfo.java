package com.cisco.avature;

import java.util.ArrayList;
import java.util.List;

// User information from Avature search results
public class AvatureInfo {
	
	private String fullName;
	private String lastName;
	private String personID;
	private String employer;
	private String candidateType;
	private String reasonForDupe;
	private List<String> websites;
	private List<String> emails;
	
	public AvatureInfo(){
		fullName = "";
		lastName = "";
		personID = "";
		candidateType = "";
		reasonForDupe = "";
		websites  = new ArrayList<String>();
		emails = new ArrayList<String>();
		
	}
	
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public List<String> getWebsites() {
		return websites;
	}


	public void setWebsites(List<String> websites) {
		this.websites = websites;
	}
	
	public void appendWebsites(String site) {
		this.websites.add(site);
	}


	public List<String> getEmails() {
		return emails;
	}


	public void appendEmails(String emails) {
		this.emails.add(emails);
	}


	public String getPersonID() {
		return personID;
	}


	public void setPersonID(String personID) {
		this.personID = personID;
	}


	public String getCandidateType() {
		return candidateType;
	}


	public void setCandidateType(String candidateType) {
		this.candidateType = candidateType;
	}


	public String getEmployer() {
		return employer;
	}


	public void setEmployer(String employer) {
		this.employer = employer;
	}


	public String getReasonForDupe() {
		return reasonForDupe;
	}


	public void setReasonForDupe(String reasonForDupe) {
		this.reasonForDupe = reasonForDupe;
	}

	
	

}

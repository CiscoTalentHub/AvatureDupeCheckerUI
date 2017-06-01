package com.cisco.avature;

//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// User information from Medusa File
public class UserInfo {
	
	private String fullName;
	private String lastName;
	private String employer;
	private String linkedInUserName;
	private String linkedInID;
	private String linkedInProfile;
	private String linkedInRecruiter;
	private String searchKeywordURL;
	private String searchNameEmployerURL;
	//private URI searchURL;
	private List<String> emails;
	private List<String> reasonForDupe;
	private List<String> duplicateNames;
	private List<String> candidateType;
	
	
	public UserInfo() {
		//super(text);
		fullName = "";
		lastName = "";
		employer = "";
		linkedInUserName = "";
		linkedInID = "";
		linkedInProfile = "";
		linkedInRecruiter = "";
		searchKeywordURL = null;
		searchNameEmployerURL = null;
		emails = new ArrayList<String>();
		reasonForDupe = new ArrayList<String>();
		duplicateNames = new ArrayList<String>();
		candidateType = new ArrayList<String>();
		
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
	public String getEmployer() {
		return employer;
	}
	public void setEmployer(String employer) {
		this.employer = employer;
	}
	public String getLinkedInProfile() {
		return linkedInProfile;
	}
	public void setLinkedInProfile(String linkedInProfile) {
		this.linkedInProfile = linkedInProfile;
	}
	public String getLinkedInRecruiter() {
		return linkedInRecruiter;
	}
	public void setLinkedInRecruiter(String linkedInRecruiter) {
		this.linkedInRecruiter = linkedInRecruiter;
	}
	public String getLinkedInUserName() {
		return linkedInUserName;
	}
	public void setLinkedInUserName(String linkedInProfileURL){
		//URI uri = new URI(linkedInProfileURL);
		//String path = uri.getPath();
		this.linkedInUserName = linkedInProfileURL.substring(linkedInProfileURL.lastIndexOf('/') + 1);
	}
	public String getLinkedInID() {
		return linkedInID;
	}
	public void setLinkedInID(String linkedInRecruiterURL){
		//URI uri = new URI(linkedInRecruiterURL);
		//String path = uri.getPath();
		this.linkedInID = linkedInRecruiterURL.substring(linkedInRecruiterURL.indexOf('=') + 1);
	}
	
	public String getSearchKeywordURL() {
		return searchKeywordURL;
	}
	public void setSearchKeywordURL(String searchKeywordURL) {
		this.searchKeywordURL = searchKeywordURL;
	}
	
	
	public String getSearchNameEmployerURL() {
		return searchNameEmployerURL;
	}
	public void setSearchNameEmployerURL(String searchNameEmployerURL) {
		this.searchNameEmployerURL = searchNameEmployerURL;
	}
	public List<String> getEmails() {
		return emails;
	}
	public void appendEmails(String email) {
		this.emails.add(email);
	}
		
	public List<String> getReasonForDupe() {
		return reasonForDupe;
	}
	public void appendReasonForDupe(String reasonForDupe) {
		this.reasonForDupe.add(reasonForDupe);
	}
	
	public List<String> getDuplicateNames() {
		return duplicateNames;
	}
	public void appendDuplicateNames(String duplicateNames) {
		this.duplicateNames.add(duplicateNames);
	}
	
	public List<String> getCandidateType() {
		return candidateType;
	}
	public void appendCandidateType(String candidateType) {
		this.candidateType.add(candidateType);
	}
	//Parse through LinkedIn URL's to get username and user ID
	protected String getToken(String text, String pattern) {
		String token = null;
		Pattern tokSplitter = Pattern.compile(pattern);
		Matcher m = tokSplitter.matcher(text);

		if (m.find()) {
			token = m.group();
			return token;
		}

		return token;
	}
	
	
	

}

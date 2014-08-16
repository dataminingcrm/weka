package weka.salesforce;

import java.util.ArrayList;
import java.util.List;

public class SOQLParser {
	
	public SOQLParser(){}
	
	private String m_Query = null;
	public SOQLParser withQuery(String q){
		this.m_Query = q;
		return this;
	}
	
	public SOQLParser parse(){
		if(this.m_Query == null || this.m_Query.equals("")){
			this.m_IsValid = false;
			Errors.add("Missing Query. Use withQuery(q) method to build parser.");
		}
		return this;
	}
	
	private boolean m_IsValid = true;
	public boolean isValid(){
		return m_IsValid;
	}
	
	public List<String> Errors = new ArrayList<String>();
	public boolean hasErrors(){
		return Errors.size() > 0;
	}
}
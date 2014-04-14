package org.skytrail.bilcdb.views;

import org.skytrail.bilcdb.model.security.DBUser;

import io.dropwizard.views.View;

public class PersonView extends View {
    private final DBUser DBUser;
    public enum Template{
    	FREEMARKER("freemarker/DBUser.ftl"),
    	MUSTACHE("mustache/DBUser.mustache");
    	
    	private String templateName;
    	private Template(String templateName){
    		this.templateName = templateName;
    	}
    	
    	public String getTemplateName(){
    		return templateName;
    	}
    }

    public PersonView(PersonView.Template template, DBUser DBUser) {
        super(template.getTemplateName());
        this.DBUser = DBUser;
    }

    public DBUser getDBUser() {
        return DBUser;
    }
}
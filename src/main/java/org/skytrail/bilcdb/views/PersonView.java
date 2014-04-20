package org.skytrail.bilcdb.views;

import org.skytrail.bilcdb.model.security.DbUser;

import io.dropwizard.views.View;

public class PersonView extends View {
    private final DbUser DBUser;
    public enum Template{
    	FREEMARKER("freemarker/DbUser.ftl"),
    	MUSTACHE("mustache/DbUser.mustache");
    	
    	private String templateName;
    	private Template(String templateName){
    		this.templateName = templateName;
    	}
    	
    	public String getTemplateName(){
    		return templateName;
    	}
    }

    public PersonView(PersonView.Template template, DbUser DBUser) {
        super(template.getTemplateName());
        this.DBUser = DBUser;
    }

    public DbUser getDBUser() {
        return DBUser;
    }
}
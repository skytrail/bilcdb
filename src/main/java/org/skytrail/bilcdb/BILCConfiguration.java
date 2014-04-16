package org.skytrail.bilcdb;

import com.bazaarvoice.dropwizard.assets.AssetsBundleConfiguration;
import com.bazaarvoice.dropwizard.assets.AssetsConfiguration;
import org.skytrail.bilcdb.model.Template;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class BILCConfiguration extends Configuration implements AssetsBundleConfiguration {

    public static String SESSION_TOKEN_NAME = "BILC_SESSION";

    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    private BasicAuthConfiguration basicAuthConfiguration;

    @NotNull
    @Valid
    @JsonProperty
    private final AssetsConfiguration assets = new AssetsConfiguration();

    @JsonProperty
    public BasicAuthConfiguration getBasicAuthConfiguration() {
        return basicAuthConfiguration;
    }

    @JsonProperty
    public void setBasicAuthConfiguration(BasicAuthConfiguration basicAuthConfiguration) {
        this.basicAuthConfiguration = basicAuthConfiguration;
    }

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public Template buildTemplate() {
        return new Template(template, defaultName);
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.database = dataSourceFactory;
    }

    @Override
    public AssetsConfiguration getAssetsConfiguration() {
        return assets;
    }

    public class BasicAuthConfiguration {
        @NotNull
        @Valid
        private String masterKey;

        @JsonProperty
        public String getMasterKey() {
            return masterKey;
        }

        @JsonProperty
        public void setMasterKey(String masterKey) {
            this.masterKey = masterKey;
        }
    }

}

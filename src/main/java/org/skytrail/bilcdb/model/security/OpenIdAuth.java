package org.skytrail.bilcdb.model.security;

import javax.persistence.*;

@Entity
@Table(name = "basic_auth")
@NamedQueries({
        @NamedQuery(
                name = "com.example.helloworld.core.Person.findById",
                query = "SELECT bau FROM basic_auth p WHERE p.key = :id"
        )
})

public class OpenIdAuth implements Auth {
    @Id
    @Column(name = "id", nullable = false)
    private String openIdIdentifier;

    @Column(name = "user", nullable = false)
    private DBUser user;

    @Override
    public String getIdentifier() {
        return getOpenIdIdentifier();
    }

    @Override
    public DBUser getUser() {
        return user;
    }


    public void setUser(DBUser user) {
        this.user = user;
    }

    public String getOpenIdIdentifier() {
        return openIdIdentifier;
    }

    public void setOpenIdIdentifier(String openIdIdentifier) {
        this.openIdIdentifier = openIdIdentifier;
    }
}

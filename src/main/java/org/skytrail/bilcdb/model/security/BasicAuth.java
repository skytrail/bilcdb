package org.skytrail.bilcdb.model.security;

import javax.persistence.*;

/**
 * Created by herndon on 4/13/14.
 */
@Entity
@Table(name = "basic_auth")
@NamedQueries({
        @NamedQuery(
                name = "com.example.helloworld.core.Person.findById",
                query = "SELECT bau FROM basic_auth p WHERE p.key = :id"
        )
})

public class BasicAuth implements Auth {
    @Id
    @Column(name = "user", nullable = false)
    private String key;

    @Column(name = "user", nullable = false)
    private DBUser user;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public void setUser(DBUser user) {
        this.user = user;
    }

    @Override
    public String getIdentifier() {
        return getKey();
    }
    @Override
    public DBUser getUser() {
        return user;
    }

}


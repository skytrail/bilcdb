package org.skytrail.bilcdb.model.security;

import javax.persistence.*;

/**
 * Created by herndon on 4/13/14.
 */
@Entity
@Table(name = "basic_auth")
@NamedQueries({
        @NamedQuery(
                name = "org.skytrail.bilcdb.model.security.BasucAuth.findById",
                query = "SELECT bau FROM basic_auth ba WHERE ba.key = :id"
        )
})

public class BasicAuth implements Auth {
    @Id
    @Column(name = "user", nullable = false)
    private String key;

    @Column(name = "user", nullable = false)
    private DbUser user;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public void setUser(DbUser user) {
        this.user = user;
    }

    @Override
    public DbUser getUser() {
        return user;
    }

}


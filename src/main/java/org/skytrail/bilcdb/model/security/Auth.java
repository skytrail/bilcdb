package org.skytrail.bilcdb.model.security;

public interface Auth {
    String getIdentifier();
    DBUser getUser();
}

package com.techelevator.tenmo.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User {

    private int id;
    private String username;

    //add authorities set
   /* private Set<Authority> authorities = new HashSet<>();

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof User) {
            User otherUser = (User) other;
            return otherUser.getId() == id
                    && otherUser.getUsername().equals(username);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}

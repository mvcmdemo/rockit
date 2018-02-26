package com.rs.mv.rockit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="USERS")
public class User {
    private long id;
    private String login;
    private String password;
    private String name;
    private String email;
    private boolean isAdmin;
    @JsonIgnoreProperties({"machines", "users"})
    private Set<Group> groups = new HashSet<>();
    @JsonIgnoreProperties({"groups", "usedBy"})
    private Set<Machine> usedMachines;

    @Id
    @Column(name="USER_ID")
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name="LOGIN")
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Column(name="PASSWORD")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name="NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="EMAIL")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "USER_GROUPS", joinColumns = { @JoinColumn(name = "USER_ID", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "GROUP_ID", nullable = false, updatable = false) })
    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    @OneToMany(mappedBy = "usedBy", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    public Set<Machine> getUsedMachines() {
        return usedMachines;
    }

    public void setUsedMachines(Set<Machine> usedMachines) {
        this.usedMachines = usedMachines;
    }

    @Column(name="ROLEADMIN")
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}

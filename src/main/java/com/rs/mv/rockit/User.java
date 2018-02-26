package com.rs.mv.rockit;

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
    private Set<Group> groups = new HashSet<>();
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "USER_GROUPS", joinColumns = { @JoinColumn(name = "USER_ID", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "GROUP_ID", nullable = false, updatable = false) })
    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    @OneToMany(mappedBy = "usedBy")
    public Set<Machine> getUsedMachines() {
        return usedMachines;
    }

    public void setUsedMachines(Set<Machine> usedMachines) {
        this.usedMachines = usedMachines;
    }
}

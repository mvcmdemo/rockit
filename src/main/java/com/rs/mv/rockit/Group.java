package com.rs.mv.rockit;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="GROUPS")
public class Group {
    private long id;
    private String name;
    @JsonIgnoreProperties({"groups", "usedBy"})
    private Set<Machine> machines = new HashSet<>();
    @JsonIgnoreProperties({"groups", "usedMachines"})
    private Set<User> users = new HashSet<>();

    @Id
    @Column(name="GROUP_ID")
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name="NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "groups")
    public Set<Machine> getMachines() {
        return machines;
    }

    public void setMachines(Set<Machine> machines) {
        this.machines = machines;
    }

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "groups")
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}

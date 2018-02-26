package com.rs.mv.rockit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="MACHINES")
public class Machine {
    private long id;
    private String host;
    private String name;
    private String user;
    private String password;
    private String description;
    @JsonIgnoreProperties({"machines", "users"})
    private Set<Group> groups = new HashSet<>();
    private volatile MachineStates state = MachineStates.OFFLINE;
    private MachinePlatforms platform = MachinePlatforms.Windows;
    @JsonIgnoreProperties({"groups", "usedMachines"})
    private User usedBy;

    @Id
    @Column(name="MACHINE_ID")
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name="HOST")
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Column(name="USERNAME")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Column(name="PASSWORD")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name="DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "MACHINE_GROUPS", joinColumns = { @JoinColumn(name = "MACHINE_ID", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "GROUP_ID", nullable = false, updatable = false) })
    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    @Transient
    public MachineStates getState() {
        return state;
    }

    public void setState(MachineStates state) {
        this.state = state;
    }

    @Column(name="PLATFORM")
    public MachinePlatforms getPlatform() {
        return platform;
    }

    public void setPlatform(MachinePlatforms platform) {
        this.platform = platform;
    }

    @ManyToOne
    @JoinColumn(name="USEDBY", nullable=true)
    public User getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(User usedBy) {
        this.usedBy = usedBy;
    }
}

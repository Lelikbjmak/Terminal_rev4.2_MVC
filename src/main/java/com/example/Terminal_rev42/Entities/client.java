package com.example.Terminal_rev42.Entities;

import com.example.Terminal_rev42.Model.user;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
public class client implements Serializable {

    public client(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @NonNull
    private long id;

    @NonNull
    @Column(name = "name")
    private String name;

    @NonNull
    @Column(name = "phone")
    private String phone;

    @NonNull
    @Column(name = "passport")
    private String passport;

    @Column(name = "birth")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Minsk")
    @NonNull
    private Date birth;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")  // 1 client has many bills
    private Set<bill> bills = new HashSet();

    @OneToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    private user user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")  // 1 client has many invests
    private Set<investments> investments = new HashSet<>();

    public com.example.Terminal_rev42.Model.user getUser() {
        return user;
    }

    public void setUser(com.example.Terminal_rev42.Model.user user) {
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Set<bill> getBills() {
        return bills;
    }

    public void setBills(Set<bill> bills) {
        this.bills = bills;
    }

    public Set<com.example.Terminal_rev42.Entities.investments> getInvestments() {
        return investments;
    }

    public void setInvestments(Set<com.example.Terminal_rev42.Entities.investments> investments) {
        this.investments = investments;
    }

    @Override
    public String toString(){
        return this.getName() + " ,passport: " + this.getPassport();
    }
}




package com.example.Terminal_rev42.Entities;

import com.example.Terminal_rev42.Model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Client implements Serializable {

    public Client(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    @NotBlank(message = "Name can't be blank.")
    @Length(max = 35, message = "Full name is too long.")
    @Pattern(regexp = "^([A-Z]{1}[a-z]*\\s?){2,3}|([А-Я]{1}[а-я]*\\s?){2,3}$", message = "Not valid format. Should be like Ivanov Ivan Ivanovich.")
    private String name;

    @NotBlank(message = "Phone can't be blank.")
    @Column(name = "phone")
    @Length(min = 7, message = "Phone number is too short.")
    @Length(max = 16, message = "Phone number is too long.")
    @Pattern(regexp = "^(\\+?)\\d{7,16}$", message = "Not valid format.")
    private String phone;

    @Column(name = "passport")
    @NotBlank(message = "Passport can't be blank.")
    @Pattern(regexp = "^[A-Z]{2}\\d{7}$", message = "Not valid format.")
    private String passport;

    @Column(name = "birth")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Minsk")
    @Past(message = "Date should be in the past.")
    private Date birth;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")  // 1 client has many bills
    private Set<Bill> bills = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    @Valid
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")  // 1 client has many invests
    private Set<Investments> investments = new HashSet<>();

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

    public Set<Bill> getBills() {
        return bills;
    }

    public void setBills(Set<Bill> bills) {
        this.bills = bills;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Investments> getInvestments() {
        return investments;
    }

    public void setInvestments(Set<Investments> investments) {
        this.investments = investments;
    }

    @Override
    public String toString(){
        return "Name: " + this.getName() + " ,passport: " + this.getPassport();
    }
}




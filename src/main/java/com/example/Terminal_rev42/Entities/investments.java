package com.example.Terminal_rev42.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class investments implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "client", referencedColumnName = "id", nullable = false)
    private client client;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "percentage", nullable = false)
    @DecimalMin("00.00")
    private BigDecimal percentage;

    @Column(name = "summa", nullable = false)
    @DecimalMin("00.00")
    private BigDecimal contribution;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "begin", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Minsk")
    private LocalDate begin;

    @Column(name = "term_month", nullable = false)
    @Min(6)
    @Max(36)
    private short term;

    @Column(name = "Status", nullable = false)
    private boolean status;

    public investments(){
        this.begin = LocalDate.now();
        this.status = true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public com.example.Terminal_rev42.Entities.client getClient() {
        return client;
    }

    public void setClient(com.example.Terminal_rev42.Entities.client client) {
        this.client = client;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    @NonNull
    public BigDecimal getContribution() {
        return contribution;
    }

    public void setContribution(@NonNull BigDecimal contribution) {
        this.contribution = contribution.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @NonNull
    public LocalDate getBegin() {
        return begin;
    }

    public void setBegin(@NonNull LocalDate begin) {
        this.begin = begin;
    }

    public short getTerm() {
        return term;
    }

    public void setTerm(short term) {
        this.term = term;
    }

    @NonNull
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(@NonNull String currency) {
        this.currency = currency;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Invest#: " + id + " , type: " + type + " , term: " + term + "\n" + "Contribution: " + contribution + " " + currency;
    }
}

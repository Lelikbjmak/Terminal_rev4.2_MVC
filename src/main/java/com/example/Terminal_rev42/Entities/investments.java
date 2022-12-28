package com.example.Terminal_rev42.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.Valid;
import javax.validation.constraints.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class investments implements Serializable {

    public investments(){
        this.begin = LocalDate.now();
        this.status = true;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "client", referencedColumnName = "id")
    @Valid
    private client client;

    @Column(name = "type", nullable = false)
    @NotBlank(message = "Type of investment can't be blank.")
    private String type;

    @Column(name = "percentage", nullable = false)
    @DecimalMin(value = "00.00", message = "Interest must be more than 00.00.")
    @Digits(integer = 1, fraction = 2, message = "Not valid format.")
    private BigDecimal percentage;

    @Column(name = "summa")
    @DecimalMin(value = "00.00", message = "Contribution must be more than 00.00.")
    @Positive
    private BigDecimal contribution;

    @Column(name = "currency", nullable = false)
    @NotBlank(message = "Currency can't be blank.")
    private String currency;

    @Column(name = "begin", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Minsk")
    @PastOrPresent(message = "Begin of investment can't be future date.")
    private LocalDate begin;

    @Column(name = "term_month", nullable = false)
    @Digits(integer = 2, fraction = 0, message = "Term should contain 2 digits.")
    @Min(value = 6, message = "Term can't be less than 6.")
    @Max(value = 36, message = "Term can't be more than 36.")
    private short term;

    @Column(name = "Status", nullable = false)
    private boolean status;

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

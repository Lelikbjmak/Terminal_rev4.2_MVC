package com.example.Terminal_rev42.Entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Entity
public class bill implements Serializable {

    public bill(){
        this.card = Long.toString(ThreadLocalRandom.current().nextLong(1_000_000_000_000_000L, 9_999_999_999_999_999L)).replaceAll("(.{4})", "$1 ").trim();
        this.ledger = new BigDecimal((Math.random()*(3000 - 1000) + 1000)).setScale(2, BigDecimal.ROUND_HALF_UP);
        this.pin = (short) (Math.random()*(9999 - 1000) + 1000);
        this.validity = LocalDate.now().plusYears(3);
        this.active = true;
    }

    @Id
    @NonNull
    private String card;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private client client;

    @NonNull
    private String type;

    @NonNull
    private String currency;

    @Nullable
    private BigDecimal ledger;

    @Size(min = 1000, max = 9999)
    @NonNull
    private  short pin;

    @Column(name = "validity")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Minsk")
    @NonNull
    private LocalDate validity;

    @NonNull
    private boolean active;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "billfrom")
    private Set<receipts> receipts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "billto")
    private Set<receipts> receiptss;

    @Override
    public String toString(){
        return this.getCard() + "\n\n\n\n**********************************************************************\n**********************************************************************\n**********************************************************************\n**********************************************************************\n" +
                "*********************************" +this.getPin() + "*********************************" + "\n**********************************************************************\n**********************************************************************\n**********************************************************************\n**********************************************************************\n";
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public com.example.Terminal_rev42.Entities.client getClient() {
        return client;
    }

    public void setClient(com.example.Terminal_rev42.Entities.client client) {
        this.client = client;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getLedger() {
        return ledger;
    }

    public void setLedger(BigDecimal ledger) {
        this.ledger = ledger;
    }

    public short getPin() {
        return pin;
    }

    public void setPin(short pin) {
        this.pin = pin;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @NonNull
    public LocalDate getValidity() {
        return validity;
    }

}

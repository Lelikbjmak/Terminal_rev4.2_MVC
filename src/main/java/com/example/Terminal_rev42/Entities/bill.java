package com.example.Terminal_rev42.Entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Entity
public class bill implements Serializable {

    public bill(){
        this.card = Long.toString(ThreadLocalRandom.current().nextLong(1_000_000_000_000_000L, 9_999_999_999_999_999L)).replaceAll("(.{4})", "$1 ").trim();
        this.ledger = BigDecimal.valueOf(Math.random() * (3000 - 1000) + 1000).setScale(2, RoundingMode.HALF_UP);
        this.pin = String.valueOf((int)(Math.random()*(9999 - 1000) + 1000));
        this.validity = LocalDate.now().plusYears(3);
        this.active = false;  // become true if we download card -> activate bill
        this.temporalLock = false;
        this.failedAttempts = 0;
    }

    @Transient
    public static final int MAX_FAILED_ATTEMPTS = 3;

    @Transient
    public static final long LOCK_TIME_DURATION =  1; // 24 hours = 1 day

    @Id
    @NotBlank
    @Size(min = 19, max = 19)
    private String card;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private client client;

    @NotBlank
    private String type;

    @Column(updatable = false, nullable = false)
    private String currency;

    @NotBlank
    @DecimalMin("00.00")
    private BigDecimal ledger;

    @NotBlank
    @Size(min = 4, max = 4)
    @Column(length = 4)
    private String pin;

    @Column(name = "validity", updatable = false, nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Minsk")
    private LocalDate validity;

    @NotBlank
    private boolean active;

    @Column(name = "temporalLock", nullable = false)
    private boolean temporalLock;

    @Column(name = "failed_attempts", nullable = false)
    @Min(0)
    @Max(3)
    private int failedAttempts;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "billfrom")
    private Set<receipts> receipts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "billto")
    private Set<receipts> receipts1;

    @Override
    public String toString(){
        return this.getCard() + "  (" + this.getCurrency() + ")" + "\n\n\n\n**********************************************************************\n**********************************************************************\n**********************************************************************\n**********************************************************************\n" +
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

    @NonNull
    public String getPin() {
        return pin;
    }

    public void setPin(@NonNull String pin) {
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

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public LocalDateTime getLockTime() {
        return lockTime;
    }

    public Set<com.example.Terminal_rev42.Entities.receipts> getReceipts() {
        return receipts;
    }

    public Set<com.example.Terminal_rev42.Entities.receipts> getReceiptss() {
        return receipts1;
    }

    public void setValidity(LocalDate validity) {
        this.validity = validity;
    }

    public boolean isTemporalLock() {
        return temporalLock;
    }

    public void setTemporalLock(boolean temporalLock) {
        this.temporalLock = temporalLock;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public void setLockTime(LocalDateTime lockTime) {
        this.lockTime = lockTime;
    }

}

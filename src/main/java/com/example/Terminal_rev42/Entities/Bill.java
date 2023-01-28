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
public class Bill implements Serializable {

    public Bill(){
        this.card = Long.toString(ThreadLocalRandom.current().nextLong(1_000_000_000_000_000L, 9_999_999_999_999_999L)).replaceAll("(.{4})", "$1 ").trim();
        this.ledger = BigDecimal.valueOf(Math.random() * (3000 - 1000) + 1000).setScale(2, RoundingMode.HALF_UP);
        this.pin = String.valueOf((int)(Math.random()*(9999 - 1000) + 1000));
        this.validity = LocalDate.now().plusYears(3);
        this.active = false;  // become true if we download card -> activate Bill
        this.temporalLock = false;
        this.failedAttempts = 0;
    }

    @Transient
    public static final int MAX_FAILED_ATTEMPTS = 3;

    @Transient
    public static final long LOCK_TIME_DURATION =  1; // 24 hours = 1 day

    @Id
    @NotBlank(message = "Card number can't be blank.")
    @Pattern(regexp = "(\\d{4}\\s){3}\\d{4}", message = "Not valid format of card number.")
    @Size(min = 19, max = 19, message = "Length of card number must comprise 19 symbols.")
    private String card;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    @Column(length = 40)
    @NotBlank(message = "Type can't be blank.")
    private String type;

    @Column(updatable = false, nullable = false, length = 5)
    @NotBlank(message = "Currency can't be blank.")
    private String currency;

    @Column(nullable = false)
    @DecimalMin(value = "00.00", message = "Ledger can't be below zero.")
    @Positive( message = "Ledger can't be negative.")
    private BigDecimal ledger;

    @Column(nullable = false)
    @NotBlank(message = "Pin is mandatory.")
    @Size(min = 4, message = "Pin must contain 4 symbols.")
    private String pin;

    @Column(name = "validity", updatable = false, nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Minsk")
    private LocalDate validity;

    @Column(nullable = false)
    private boolean active;        // if Bill is activated

    @Column(name = "temporalLock", nullable = false)      // true if Bill was temporary locked due to 3 failed attempts
    private boolean temporalLock;

    @Column(name = "failed_attempts", nullable = false)
    @Min(value = 0, message = "failedAttempts can't be less than 0.")
    @Max(value = 3, message = "failedAttempts can't be more than 3.")
    private int failedAttempts;  // failed attempts if we input incorrect pin as operation confirmation - (3 failedAttempts -> lock Bill for 24 hours)

    @Column(name = "lock_time")       // lockTime after
    private LocalDateTime lockTime;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "billFrom")   // receipts for this Bill as Sender
    private Set<Receipts> receipts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "billTo")  // receipts for this Bill as Recipient
    private Set<Receipts> receipts1;

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
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

    public Set<Receipts> getReceipts() {
        return receipts;
    }

    public Set<Receipts> getReceiptss() {
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

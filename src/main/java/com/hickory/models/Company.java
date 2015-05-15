package com.hickory.models;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Evgeny Frolov
 */

@Entity
@Table(name = "companies", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Company {
    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "managerId")
    private User manager;

    private String shortcutLegalName;

    private String orgEmail;

    private String orgPhoneNumber;

    private String inn;

    private String kpp;

    private String okpo;

    private String surname;

    private String name;

    private String email;

    private String address;

    private String bik;

    private String kbill;

    private String bill;

    private String bank;

    private String ogrn;

    private String lastBillNumber;

    private String lastActNumber;

    private String lastTorg12Number;

    private String fioCeo;

    private String fioAc;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createDate;


    public Company() {
    }


    @PrePersist
    protected void onCreate() {
        setCreateDate(new Date());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortcutLegalName() {
        return shortcutLegalName;
    }

    public void setShortcutLegalName(String shortcutLegalName) {
        this.shortcutLegalName = shortcutLegalName;
    }

    public String getOrgEmail() {
        return orgEmail;
    }

    public void setOrgEmail(String orgEmail) {
        this.orgEmail = orgEmail;
    }

    public String getOrgPhoneNumber() {
        return orgPhoneNumber;
    }

    public void setOrgPhoneNumber(String orgPhoneNumber) {
        this.orgPhoneNumber = orgPhoneNumber;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getOkpo() {
        return okpo;
    }

    public void setOkpo(String okpo) {
        this.okpo = okpo;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBik() {
        return bik;
    }

    public void setBik(String bik) {
        this.bik = bik;
    }

    public String getKbill() {
        return kbill;
    }

    public void setKbill(String kbill) {
        this.kbill = kbill;
    }

    public String getBill() {
        return bill;
    }

    public void setBill(String bill) {
        this.bill = bill;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    public String getLastTorg12Number() {
        return lastTorg12Number;
    }

    public void setLastTorg12Number(String lastTorg12Number) {
        this.lastTorg12Number = lastTorg12Number;
    }

    public String getLastBillNumber() {
        return lastBillNumber;
    }

    public void setLastBillNumber(String lastBillNumber) {
        this.lastBillNumber = lastBillNumber;
    }

    public String getLastActNumber() {
        return lastActNumber;
    }

    public void setLastActNumber(String lastActNumber) {
        this.lastActNumber = lastActNumber;
    }

    public String getFioCeo() {
        return fioCeo;
    }

    public void setFioCeo(String fioCeo) {
        this.fioCeo = fioCeo;
    }

    public String getFioAc() {
        return fioAc;
    }

    public void setFioAc(String fioAc) {
        this.fioAc = fioAc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Company company = (Company) o;

        if (createDate != null ? !createDate.equals(company.createDate) : company.createDate != null) return false;
        if (id != null ? !id.equals(company.id) : company.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        return result;
    }
}

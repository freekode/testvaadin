package com.hickory.models;

import com.hickory.models.interfaces.ConvertibleEnum;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Evgeny Frolov
 */

@Entity
@Table(name = "suppliers", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Supplier {
    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ClientType clientType;

    @Column(nullable = false)
    private String shortcutLegalName;

    @Column(nullable = false)
    private String orgEmail;

    @Column(nullable = false)
    private String orgPhoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrganizationType organizationType;

    @Column(nullable = false)
    private String inn;

    @Column(nullable = true)
    private String kpp;

    @Column(nullable = true)
    private String okpo;

    private String surname;

    private String name;

    private String patronymic;

    private String phoneNumber;

    private String email;

    private String mobileNumber;

    private String contract;

    private String address;

    private String numberBill;

    private String currency;

    private String bik;

    private String korrBill;

    private String bank;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createDate;


    public Supplier() {
    }

    @PrePersist
    protected void onCreate() {
        setCreateDate(new Date());
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
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

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
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

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getNumberBill() {
        return numberBill;
    }

    public void setNumberBill(String numberBill) {
        this.numberBill = numberBill;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBik() {
        return bik;
    }

    public void setBik(String bik) {
        this.bik = bik;
    }

    public String getKorrBill() {
        return korrBill;
    }

    public void setKorrBill(String korrBill) {
        this.korrBill = korrBill;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Supplier supplier = (Supplier) o;

        if (createDate != null ? !createDate.equals(supplier.createDate) : supplier.createDate != null) return false;
        if (id != null ? !id.equals(supplier.id) : supplier.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        return result;
    }

    public enum ClientType implements ConvertibleEnum {
        COMPANY("Компания"),
        PRIVATE_PERSON("Частное лицо");

        private final String value;

        ClientType(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    public enum OrganizationType implements ConvertibleEnum {
        LEGAL_PERSON("Юр. лицо"),
        LEGAL_PERSON_NOT_RU("Юр. лицо (не ведет деятельность в РФ)"),
        INDIVIDUAL_ENTREPRENEUR("Инд. предпрениматель");

        private final String value;

        OrganizationType(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }
}

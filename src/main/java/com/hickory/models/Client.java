package com.hickory.models;

import com.hickory.models.interfaces.ConvertibleEnum;
import com.hickory.models.interfaces.SerializableToJson;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Evgeny Frolov
 */

@Entity
@Table(name = "clients", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Client implements SerializableToJson {
    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private ClientType clientType;

    @ManyToOne
    @JoinColumn(name = "managerId")
    private User manager;

    private String shortcutLegalName;

    private String orgEmail;

    private String orgPhoneNumber;

    @Enumerated(EnumType.STRING)
    private OrganizationType organizationType;

    private String inn;

    private String kpp;

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

    @OneToMany(mappedBy = "client")
    private Set<Address> addresses = new HashSet<>();

    @OneToMany(mappedBy = "client")
    private Set<Order> orders = new HashSet<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createDate;


    public Client() {
        setPassword(RandomStringUtils.random(8, true, true));
        setClientType(ClientType.COMPANY);
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public Set<Address> getAddresses() {
        return new HashSet<>(addresses);
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    public Set<Order> getOrders() {
        return new HashSet<>(orders);
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
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

        Client client = (Client) o;

        if (createDate != null ? !createDate.equals(client.createDate) : client.createDate != null) return false;
        if (id != null ? !id.equals(client.id) : client.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        return result;
    }

    @Override
    public JSONObject toJson() {
        try {
            JSONArray addressesArr = new JSONArray();
            for (SerializableToJson address : getAddresses()) {
                addressesArr.put(address.toJson());
            }

            JSONArray ordersArr = new JSONArray();
            for (SerializableToJson order : getOrders()) {
                ordersArr.put(order.toJson());
            }

            return new JSONObject()
                    .put("id", getId())
                    .put("login", getLogin())
                    .put("password", getPassword())
                    .put("clientType", getClientType().getValue())
                    .put("shortcutLegalName", getShortcutLegalName())
                    .put("address", getAddress())
                    .put("orgEmail", getOrgEmail())
                    .put("orgPhoneNumber", getOrgPhoneNumber())
                    .put("organizationType", getOrganizationType())
                    .put("addresses", addressesArr)
                    .put("orders", ordersArr)
                    .put("numberBill", getNumberBill())
                    .put("currency", getCurrency())
                    .put("bik", getBik())
                    .put("korrBill", getKorrBill())
                    .put("bank", getBank())
                    .put("inn", getInn())
                    .put("kpp", getKpp())
                    .put("okpo", getOkpo())
                    .put("createDate", getCreateDate());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public String toString() {
        return shortcutLegalName;
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

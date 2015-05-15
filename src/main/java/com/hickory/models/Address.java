package com.hickory.models;

import com.hickory.models.interfaces.SerializableToJson;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Evgeny Frolov
 */

@Entity
@Table(name = "addresses", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Address implements SerializableToJson {
    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String address;

    @ManyToOne
    @JoinColumn(name = "clientId")
    private Client client;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createDate;


    public Address() {
    }


    public Address(String title, String address, Client client) {
        this.title = title;
        this.address = address;
        this.client = client;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Address address = (Address) o;
//
//        if (createDate != null ? !createDate.equals(address.createDate) : address.createDate != null) return false;
//        if (id != null ? !id.equals(address.id) : address.id != null) return false;
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = id != null ? id.hashCode() : 0;
//        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
//        return result;
//    }

    @Override
    public JSONObject toJson() {
        try {
            return new JSONObject()
                    .put("id", getId())
                    .put("title", getTitle())
                    .put("address", getAddress())
                    .put("createDate", getCreateDate());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}

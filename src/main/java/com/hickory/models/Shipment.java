package com.hickory.models;

import com.hickory.models.interfaces.SerializableToJson;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Evgeny Frolov
 */

@Entity
@Table(name = "shipments", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Shipment implements SerializableToJson {
    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date shipmentDate;

    @OneToOne
    @JoinColumn(name = "purchaseId")
    private Purchase purchase;

    @OneToMany(mappedBy = "shipment")
    private Set<Item> items = new HashSet<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createDate;


    public Shipment() {
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

    public Date getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(Date shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public Set<Item> getItems() {
        return new HashSet<>(items);
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shipment shipment = (Shipment) o;

        if (createDate != null ? !createDate.equals(shipment.createDate) : shipment.createDate != null) return false;
        if (id != null ? !id.equals(shipment.id) : shipment.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        if (getPurchase().getSelectDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            str.append(dateFormat.format(getPurchase().getSelectDate()));
        }

        if (getPurchase().getSupplier() != null &&
                getPurchase().getSupplier().getShortcutLegalName() != null) {

            str.append(" ");
            str.append(getPurchase().getSupplier().getShortcutLegalName());
        }


        return str.toString();
    }

    @Override
    public JSONObject toJson() {
        try {
            return new JSONObject()
                    .put("id", getId())
                    .put("shipmentDate", getShipmentDate())
                    .put("createDate", getCreateDate());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}

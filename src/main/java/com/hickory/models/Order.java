package com.hickory.models;

import com.hickory.models.interfaces.ConvertibleEnum;
import com.hickory.models.interfaces.SerializableToJson;
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
@Table(name = "orders", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Order implements SerializableToJson {
    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String number;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fromDate;

    @ManyToOne
    @JoinColumn(name = "clientId")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "managerId")
    private User manager;

    private String contract;

    @Enumerated(EnumType.STRING)
    private StatusType status;

    @Enumerated(EnumType.STRING)
    private PriorityType priority;

    @Enumerated(EnumType.STRING)
    private DeliveryType delivery;

    @Temporal(TemporalType.TIMESTAMP)
    private Date shipmentDate;

    private String commentary;

    @ManyToMany
    @JoinTable(name = "orders_items",
            joinColumns = {@JoinColumn(name = "orderId")},
            inverseJoinColumns = {@JoinColumn(name = "itemId")})
    private Set<Item> items = new HashSet<>();

    @OneToMany(mappedBy = "order")
    private Set<OrderItem> orderItems = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "addressId")
    private Address address;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createDate;


    public Order() {
    }

    @PrePersist
    protected void onCreate() {
        setCreateDate(new Date());
        setFromDate(new Date());
        setStatus(StatusType.NEW);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public PriorityType getPriority() {
        return priority;
    }

    public void setPriority(PriorityType priority) {
        this.priority = priority;
    }

    public DeliveryType getDelivery() {
        return delivery;
    }

    public void setDelivery(DeliveryType delivery) {
        this.delivery = delivery;
    }

    public Date getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(Date shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public Set<Item> getItems() {
        return new HashSet<>(items);
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<OrderItem> getOrderItems() {
        return new HashSet<>(orderItems);
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
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

    public void setCreateDate(Date created) {
        this.createDate = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (createDate != null ? !createDate.equals(order.createDate) : order.createDate != null) return false;
        if (id != null ? !id.equals(order.id) : order.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);

        if (result == 0) {
            result = super.hashCode();
        }

        return result;
    }

    @Override
    public JSONObject toJson() {
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("id", getId())
                    .put("number", getNumber())
                    .put("fromDate", getFromDate())
                    .put("contract", getContract())
                    .put("shipmentDate", getShipmentDate())
                    .put("commentary", getCommentary())
                    .put("createDate", getCreateDate());

            if (getStatus() != null) jsonObject.put("status", getStatus().getValue());
            if (getPriority() != null) jsonObject.put("priority", getPriority().getValue());
            if (getDelivery() != null) jsonObject.put("delivery", getDelivery().getValue());

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum StatusType implements ConvertibleEnum {
        NEW("Новый"),
        COMPUTING("Обработка"),
        PAYMENT("Оплачен"),
        PAYMENT_DELAY("Оплата с отсрочкой"),
        SHIPPED("Отгружен"),
        RETURN("Отменен");

        private final String value;

        StatusType(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    public enum PriorityType implements ConvertibleEnum {
        TEST1("test1"),
        TEST2("test2"),
        TEST3("test3");

        private final String value;

        PriorityType(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    public enum DeliveryType implements ConvertibleEnum {
        PICKUP("Самовывоз"),
        DELIVERY("Доставка");

        private final String value;

        DeliveryType(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }
}

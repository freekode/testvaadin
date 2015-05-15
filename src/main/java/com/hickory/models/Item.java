package com.hickory.models;

import com.hickory.models.interfaces.SerializableToJson;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Evgeny Frolov
 */

// TODO поиск деталей по коду должен происходить 0000-0000/22 == 0000000022
// TODO пробелы по одной штуке внутки наименования -

@Entity
@Table(name = "items", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Item implements SerializableToJson {
    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String printName;

    private String factoryCode;

    private String applicable;

    private String originalCode;

    private String barcode;

    private BigDecimal inPrice;

    private BigDecimal tradePrice;

    private BigDecimal retailPrice;

    private String brand;

    private String country;

    private String gtd;

    private String codeother;

    private Integer amount;

    @Transient
    private Integer reservedAmount;

    @Transient
    private Integer leftAmount;

    @ManyToOne
    @JoinColumn(name = "shipmentId")
    private Shipment shipment;

    @ManyToOne
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

    @ManyToMany(mappedBy = "items")
    private Set<Order> orders = new HashSet<>();

    @OneToMany(mappedBy = "item")
    private Set<OrderItem> itemOrders = new HashSet<>();

    @ManyToMany(mappedBy = "items")
    private Set<StoreRowCell> cells = new HashSet<>();

    @OneToMany(mappedBy = "item")
    private Set<CellItem> itemCells = new HashSet<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createDate;


    public Item() {
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

    public String getPrintName() {
        return printName;
    }

    public void setPrintName(String printName) {
        this.printName = printName;
    }

    public String getFactoryCode() {
        return factoryCode;
    }

    public void setFactoryCode(String factoryCode) {
        this.factoryCode = factoryCode;
    }

    public String getApplicable() {
        return applicable;
    }

    public void setApplicable(String applicable) {
        this.applicable = applicable;
    }

    public String getOriginalCode() {
        return originalCode;
    }

    public void setOriginalCode(String originalCode) {
        this.originalCode = originalCode;
    }

    public Set<OrderItem> getItemOrders() {
        return new HashSet<>(itemOrders);
    }

    public void setItemOrders(Set<OrderItem> orders) {
        this.itemOrders = orders;
    }

    public Set<StoreRowCell> getCells() {
        return new HashSet<>(cells);
    }

    public void setCells(Set<StoreRowCell> cells) {
        this.cells = cells;
    }

    public BigDecimal getInPrice() {
        return inPrice;
    }

    public void setInPrice(BigDecimal price) {
        this.inPrice = price;
    }

    public BigDecimal getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(BigDecimal tradePrice) {
        this.tradePrice = tradePrice;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Set<Order> getOrders() {
        return new HashSet<>(orders);
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Set<CellItem> getItemCells() {
        return new HashSet<>(itemCells);
    }

    public void setItemCells(Set<CellItem> itemCells) {
        this.itemCells = itemCells;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public String getCodeother() {
        return codeother;
    }

    public void setCodeother(String codeother) {
        this.codeother = codeother;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGtd() {
        return gtd;
    }

    public void setGtd(String gtd) {
        this.gtd = gtd;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getReservedAmount() {
        Integer reservedAmount = 0;
        for (OrderItem orderItem : getItemOrders()) {
            Order.StatusType status = orderItem.getOrder().getStatus();
            if ((status != Order.StatusType.SHIPPED) && (status != Order.StatusType.RETURN)) {
                reservedAmount += orderItem.getAmount();
            }
        }

        return reservedAmount;
    }

    public Integer getLeftAmount() {
        return getAmount() - getReservedAmount();
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

        Item item = (Item) o;

        if (createDate != null ? !createDate.equals(item.createDate) : item.createDate != null) return false;
        if (id != null ? !id.equals(item.id) : item.id != null) return false;

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
            return new JSONObject()
                    .put("id", getId())
                    .put("printName", getPrintName())
                    .put("factoryCode", getFactoryCode())
                    .put("applicable", getApplicable())
                    .put("originalCode", getOriginalCode())
                    .put("barcode", getBarcode())
                    .put("inPrice", getInPrice())
                    .put("tradePrice", getTradePrice())
                    .put("retailPrice", getRetailPrice())
                    .put("brand", getBrand())
                    .put("amount", getLeftAmount())
//                    .put("category", getCategory().toJson());
                    .put("createDate", getCreateDate());
        } catch (JSONException e) {
            System.out.println("error");
            e.printStackTrace();
            return null;
        }
    }
}

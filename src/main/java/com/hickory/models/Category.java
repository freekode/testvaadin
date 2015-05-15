package com.hickory.models;

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
@Table(name = "categories", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Category implements SerializableToJson {
    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String description;

    @ManyToOne
    @JoinColumn(name = "parentId")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private Set<Category> childCategories = new HashSet<>();

    @OneToMany(mappedBy = "category")
    private Set<Item> items = new HashSet<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createDate;


    public Category() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public Set<Category> getChildCategories() {
        return new HashSet<>(childCategories);
    }

    public void setChildCategories(Set<Category> childCategories) {
        this.childCategories = childCategories;
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

        Category category = (Category) o;

        if (createDate != null ? !createDate.equals(category.createDate) : category.createDate != null) return false;
        if (id != null ? !id.equals(category.id) : category.id != null) return false;

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
            return new JSONObject()
                    .put("id", getId())
                    .put("title", getTitle())
                    .put("description", getDescription())
                    .put("createDate", getCreateDate());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}

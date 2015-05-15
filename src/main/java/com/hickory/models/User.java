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
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class User implements SerializableToJson {
    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    private String fname;

    private String lname;

    @OneToMany(mappedBy = "manager")
    private Set<Client> clients = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createDate;


    public User() {
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

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public Set<Client> getClients() {
        return new HashSet<>(clients);
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
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

        User user = (User) o;

        if (createDate != null ? !createDate.equals(user.createDate) : user.createDate != null) return false;
        if (id != null ? !id.equals(user.id) : user.id != null) return false;

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
                    .put("login", getLogin())
                    .put("password", getPassword())
                    .put("fname", getFname())
                    .put("lname", getLname())
                    .put("userType", getUserType().getValue())
                    .put("createDate", getCreateDate());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return getFname() + " " + getLname();
    }

    public enum UserType implements ConvertibleEnum {
        ADMINISTRATOR("Администратор"),
        SUPERVISOR("Управляющий"),
        MANAGER("Менеджер"),
        STOREKEEPER("Кладовщик");

        private final String value;

        UserType(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }
}

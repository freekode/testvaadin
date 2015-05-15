package com.hickory.views.forms;

import com.hickory.models.User;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;


/**
 * @author Evgeny Frolov
 */
public class UserForm extends AbstractForm {
    public UserForm(EntityItem item) {
        super(item);

        setTitle("Форма заполнения Пользователя");


        TextField login = new TextField("Логин");
        login.setNullRepresentation("");
        login.setRequired(true);

        PasswordField password = new PasswordField("Пароль");
        password.setNullRepresentation("");
        password.setRequired(true);

        TextField fname = new TextField("Имя");
        fname.setNullRepresentation("");

        TextField lname = new TextField("Фамилия");
        lname.setNullRepresentation("");

        NativeSelect userType = new NativeSelect("Тип пользователя");
        userType.setRequired(true);
        userType.setNullSelectionAllowed(false);
        for (User.UserType value : User.UserType.values()) {
            userType.addItem(value);
            userType.setItemCaption(value, value.getValue());
        }


        addAndBind(login, "login");
        addAndBind(password, "password");
        addAndBind(fname, "fname");
        addAndBind(lname, "lname");
        addAndBind(userType, "userType");
    }
}

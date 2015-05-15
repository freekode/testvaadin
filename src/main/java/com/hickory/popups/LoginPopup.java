package com.hickory.popups;

import com.hickory.models.User;
import com.hickory.services.ContainerBuilder;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;

/**
 * Created by Evgeny Frolov on 02/12/14.
 */
public class LoginPopup extends ModalPopup {
    private Button loginButton;
    private TextField login;
    private PasswordField password;

    public LoginPopup() {
        setWidth("350px");


        login = new TextField("Логин");

        password = new PasswordField("Пароль");

        loginButton = new Button("Войти");
        loginButton.addClickListener(this);
        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        FormLayout formLayout = new FormLayout();
        formLayout.addComponent(login);
        formLayout.addComponent(password);
        formLayout.addComponent(loginButton);


        setContent(formLayout);
        login.focus();
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton() == loginButton) {
            loginButtonClick();
        }
    }

    protected void loginButtonClick() {
        JPAContainer<User> users = ContainerBuilder.getContainer(User.class);

        users.addContainerFilter(new And(
                new Compare.Equal("login", login.getValue()),
                new Compare.Equal("password", password.getValue())
        ));


        if (users.size() == 1) {
            User user = null;
            for (Object itemId : users.getItemIds()) {
                user = users.getItem(itemId).getEntity();
            }

            getSession().setAttribute("user", user);


            fireEvent(loginButton);
            close();
        } else {
            Notification.show("Не правильный пароль и/или пользователь", Notification.Type.ERROR_MESSAGE);
        }
    }
}

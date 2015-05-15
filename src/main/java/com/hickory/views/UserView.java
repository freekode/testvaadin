package com.hickory.views;

import com.hickory.models.User;
import com.hickory.services.ContainerBuilder;
import com.hickory.views.forms.AbstractForm;
import com.hickory.views.forms.UserForm;
import com.hickory.views.services.ConvertibleEnumConverter;
import com.hickory.views.services.FilterTableDecorator;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomTable;
import org.tepi.filtertable.FilterTable;

/**
 * @author Evgeny Frolov
 */
public class UserView extends CommonView implements Button.ClickListener, ItemClickEvent.ItemClickListener {
    private JPAContainer<User> userJpa;
    private FilterTable userTable;
    private Button newButton;


    public UserView() {
        newButton = new Button("Новый", this);

        userJpa = ContainerBuilder.getContainer(User.class);
        userTable = configureUserTable(buildUserTable(userJpa));


        getStandardViewLayout().addTop(newButton);
        getStandardViewLayout().addContent(userTable);

        setContent(getStandardViewLayout());
    }


    public static FilterTable buildUserTable(Container container) {
        FilterTable table = new FilterTable();
        table.setContainerDataSource(container);
        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);
        table.setSelectable(true);
        table.setFilterBarVisible(true);
        table.addStyleName("no-stripes");
        table.addStyleName("borderless");
//        table.setWidth("100%");

        table.setVisibleColumns("login", "password", "fname", "lname", "userType", "createDate");

        table.setColumnHeaders("Пользоватаель", "Пароль", "Имя", "Фамилия", "Роль", "Дата создания");

        table.setConverter("userType", new ConvertibleEnumConverter());

        table.setFilterDecorator(new FilterTableDecorator());


        return table;
    }

    public FilterTable configureUserTable(FilterTable table) {
        table.addItemClickListener(this);
        table.addGeneratedColumn("genRemove", new CustomTable.ColumnGenerator() {
            @Override
            public Object generateCell(CustomTable source, final Object itemId, Object columnId) {
                Button removeBtn = new Button(null, new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        userJpa.removeItem(itemId);
                        commit();
                    }
                });
                removeBtn.setIcon(FontAwesome.TRASH_O);
                removeBtn.setStyleName("borderless");
                return removeBtn;
            }
        });


        String[] columnHeaders = table.getColumnHeaders();
        columnHeaders[columnHeaders.length - 1] = "Удалить";
        table.setColumnHeaders(columnHeaders);


        return table;
    }

    @Override
    public void itemClick(ItemClickEvent event) {
        if (event.getSource() == userTable && event.isDoubleClick()) {
            EntityItem<User> item = userJpa.getItem(event.getItemId());
//            Item item = userTable.getItem(event.getItemId());

            UserForm userForm = new UserForm(item);
            userForm.addSaveListener(new AbstractForm.ClickListener() {
                @Override
                public void saved(AbstractForm.ClickEvent event) {
                    commit();
                    setContent(getStandardViewLayout());
                }

                @Override
                public void cancelled(AbstractForm.ClickEvent event) {
                    setContent(getStandardViewLayout());
                }
            });
            setContent(userForm);
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton() == newButton) {
            newButtonClick();
        }
    }

    private void newButtonClick() {
        final EntityItem<User> newUser = userJpa.createEntityItem(new User());

        UserForm userForm = new UserForm(newUser);
        userForm.addSaveListener(new AbstractForm.ClickListener() {
            @Override
            public void saved(AbstractForm.ClickEvent event) {
                userJpa.addEntity(newUser.getEntity());

                commit();
                setContent(getStandardViewLayout());
            }

            @Override
            public void cancelled(AbstractForm.ClickEvent event) {
                setContent(getStandardViewLayout());
            }
        });
        setContent(userForm);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        userJpa.refresh();
    }

    @Override
    public void commitContainers() {
        userJpa.commit();
    }

    @Override
    public void commitFailed() {
        userJpa.refresh();
    }
}

package com.hickory.views;

import com.hickory.models.Address;
import com.hickory.models.Client;
import com.hickory.models.User;
import com.hickory.services.ContainerBuilder;
import com.hickory.views.forms.AbstractForm;
import com.hickory.views.forms.ClientForm;
import com.hickory.views.services.ConvertibleEnumConverter;
import com.hickory.views.services.FilterTableDecorator;
import com.hickory.views.services.ManagerConverter;
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
public class ClientView extends CommonView implements Button.ClickListener, ItemClickEvent.ItemClickListener {
    private JPAContainer<Client> clientJpa;
    private JPAContainer<User> managerJpa;
    private JPAContainer<Address> addressJpa;
    private FilterTable clientTable;
    private Button newButton;


    public ClientView() {
        clientJpa = ContainerBuilder.getContainer(Client.class);
        managerJpa = ContainerBuilder.getContainer(User.class);
        addressJpa = ContainerBuilder.getContainer(Address.class);

        newButton = new Button("Новый", this);

        clientTable = configureClientTable(buildClientTable(clientJpa));


        getStandardViewLayout().addTop(newButton);
        getStandardViewLayout().addContent(clientTable);

        setContent(getStandardViewLayout());
    }


    public static FilterTable buildClientTable(Container container) {
        FilterTable table = new FilterTable();
        table.setContainerDataSource(container);
        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);
        table.setSelectable(true);
        table.setFilterBarVisible(true);
        table.addStyleName("no-stripes");
        table.addStyleName("borderless");
//        table.setWidth("100%");


        table.setVisibleColumns("shortcutLegalName",
                "organizationType", "inn", "login", "password", "contract");

        table.setColumnHeaders("Название организации",
                "Тип организации", "ИНН", "Логин", "Пароль", "Договор");

        table.setConverter("manager", new ManagerConverter());
        table.setConverter("clientType", new ConvertibleEnumConverter());
        table.setConverter("organizationType", new ConvertibleEnumConverter());

        table.setFilterDecorator(new FilterTableDecorator());


        return table;
    }

    public FilterTable configureClientTable(FilterTable table) {
        table.addItemClickListener(this);
        table.addGeneratedColumn("genRemove", new CustomTable.ColumnGenerator() {
            @Override
            public Object generateCell(CustomTable source, final Object itemId, Object columnId) {
                Button removeBtn = new Button(null, new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        clientJpa.removeItem(itemId);
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
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton() == newButton) {
            newButtonClick();
        }
    }

    @Override
    public void itemClick(ItemClickEvent event) {
        if (event.getSource() == clientTable && event.isDoubleClick()) {
            EntityItem<Client> clientEntityItem = clientJpa.getItem(event.getItemId());
//            Item item = clientTable.getItem(event.getItemId());

            ClientForm clientForm = new ClientForm(clientEntityItem, managerJpa, addressJpa);
            clientForm.addSaveListener(new AbstractForm.ClickListener() {
                @Override
                public void saved(AbstractForm.ClickEvent event) {
                    commit();
                    setContent(getStandardViewLayout());
                }

                @Override
                public void cancelled(AbstractForm.ClickEvent event) {
                    addressJpa.discard();
                    setContent(getStandardViewLayout());
                }
            });
            setContent(clientForm);
        }
    }

    private void newButtonClick() {
        final EntityItem<Client> newClient = clientJpa.createEntityItem(new Client());

        ClientForm clientForm = new ClientForm(newClient, managerJpa, addressJpa);
        clientForm.addSaveListener(new AbstractForm.ClickListener() {
            @Override
            public void saved(AbstractForm.ClickEvent event) {
                clientJpa.addEntity(newClient.getEntity());

                commit();
                setContent(getStandardViewLayout());
            }

            @Override
            public void cancelled(AbstractForm.ClickEvent event) {
                setContent(getStandardViewLayout());
            }
        });
        setContent(clientForm);
    }

    @Override
    public void commitContainers() {
        clientJpa.commit();
        managerJpa.commit();
        addressJpa.commit();
    }

    @Override
    public void commitFailed() {
        clientJpa.refresh();
        managerJpa.refresh();
        addressJpa.refresh();
    }
}

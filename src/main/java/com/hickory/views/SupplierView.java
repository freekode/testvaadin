package com.hickory.views;

import com.hickory.models.Supplier;
import com.hickory.services.ContainerBuilder;
import com.hickory.views.forms.AbstractForm;
import com.hickory.views.forms.SupplierForm;
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
public class SupplierView extends CommonView implements Button.ClickListener, ItemClickEvent.ItemClickListener {
    private JPAContainer<Supplier> supplierJpa;
    private FilterTable supplierTable;
    private Button newBtn;


    public SupplierView() {
        newBtn = new Button("Новый", this);

        supplierJpa = ContainerBuilder.getContainer(Supplier.class);
        supplierTable = configureSupplierTable(buildSupplierTable(supplierJpa));


        getStandardViewLayout().addTop(newBtn);
        getStandardViewLayout().addContent(supplierTable);

        setContent(getStandardViewLayout());
    }


    public static FilterTable buildSupplierTable(Container container) {
        FilterTable table = new FilterTable();
        table.setContainerDataSource(container);

        table.setVisibleColumns("shortcutLegalName",
                "organizationType", "inn", "kpp", "okpo", "surname", "name", "patronymic", "phoneNumber",
                "email", "mobileNumber", "contract");
        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);
        table.setSelectable(true);
        table.setFilterBarVisible(true);
        table.addStyleName("no-stripes");
        table.addStyleName("borderless");

        table.setColumnHeaders("Наименование",
                "Тип организации", "ИНН", "КПП", "ОКПО", "Фамилия", "Имя", "Отчество", "Тел.",
                "Email", "Моб. тел.", "Договор");

        table.setConverter("clientType", new ConvertibleEnumConverter());
        table.setConverter("organizationType", new ConvertibleEnumConverter());

        table.setFilterDecorator(new FilterTableDecorator());

        return table;
    }

    public FilterTable configureSupplierTable(FilterTable table) {
        table.addItemClickListener(this);
        table.addGeneratedColumn("genRemove", new CustomTable.ColumnGenerator() {
            @Override
            public Object generateCell(CustomTable source, final Object itemId, Object columnId) {
                Button removeBtn = new Button(null, new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        supplierJpa.removeItem(itemId);
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
        if (event.getSource() == supplierTable && event.isDoubleClick()) {
            EntityItem<Supplier> item = supplierJpa.getItem(event.getItemId());
//            Item item = supplierTable.getItem(event.getItemId());

            SupplierForm supplierForm = new SupplierForm(item);
            supplierForm.addSaveListener(new AbstractForm.ClickListener() {
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
            setContent(supplierForm);
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton() == newBtn) {
            newButtonClick();
        }
    }

    private void newButtonClick() {
        final EntityItem<Supplier> newSupplier = supplierJpa.createEntityItem(new Supplier());

        SupplierForm supplierForm = new SupplierForm(newSupplier);
        supplierForm.addSaveListener(new AbstractForm.ClickListener() {
            @Override
            public void saved(AbstractForm.ClickEvent event) {
                supplierJpa.addEntity(newSupplier.getEntity());

                commit();
                setContent(getStandardViewLayout());
            }

            @Override
            public void cancelled(AbstractForm.ClickEvent event) {
                setContent(getStandardViewLayout());
            }
        });
        setContent(supplierForm);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        supplierJpa.refresh();
    }

    @Override
    public void commitContainers() {
        supplierJpa.commit();
    }

    @Override
    public void commitFailed() {
        supplierJpa.refresh();
    }
}

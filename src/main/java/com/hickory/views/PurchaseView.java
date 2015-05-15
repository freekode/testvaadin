package com.hickory.views;

import com.hickory.models.Category;
import com.hickory.models.Purchase;
import com.hickory.models.StoreRowCell;
import com.hickory.models.Supplier;
import com.hickory.services.ContainerBuilder;
import com.hickory.views.forms.AbstractForm;
import com.hickory.views.forms.PurchaseForm;
import com.hickory.views.services.CategoryConverter;
import com.hickory.views.services.FilterTableDecorator;
import com.hickory.views.services.SupplierConverter;
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
public class PurchaseView extends CommonView implements Button.ClickListener, ItemClickEvent.ItemClickListener {
    private JPAContainer<Purchase> purchaseJpa;
    private JPAContainer<Supplier> supplierJpa;
    private JPAContainer<Category> categoryJpa;
    private JPAContainer<StoreRowCell> cellJpa;
    private FilterTable purchaseTable;
    private Button newBtn;


    public PurchaseView() {
        newBtn = new Button("Новый", this);

        purchaseJpa = ContainerBuilder.getContainer(Purchase.class);
        supplierJpa = ContainerBuilder.getContainer(Supplier.class);
        categoryJpa = ContainerBuilder.getContainer(Category.class);
        cellJpa = ContainerBuilder.getContainer(StoreRowCell.class);

        purchaseTable = configureProcurementTable(buildProcurementTable(purchaseJpa));


        getStandardViewLayout().addTop(newBtn);
        getStandardViewLayout().addContent(purchaseTable);

        setContent(getStandardViewLayout());
    }


    public static FilterTable buildProcurementTable(Container container) {
        FilterTable table = new FilterTable();
        table.setContainerDataSource(container);
        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);
        table.setSelectable(true);
        table.setFilterBarVisible(true);
        table.addStyleName("no-stripes");
        table.addStyleName("borderless");

        table.setVisibleColumns("supplier", "selectDate", "file0", "file1", "tax", "category");

        table.setColumnHeaders("Поставщик", "Дата", "Файл 1", "Файл 2", "НДС", "Категория");

        table.setConverter("supplier", new SupplierConverter());
        table.setConverter("category", new CategoryConverter());

        table.setFilterDecorator(new FilterTableDecorator());


        return table;
    }

    public FilterTable configureProcurementTable(FilterTable table) {
        table.addItemClickListener(this);
        table.addGeneratedColumn("genRemove", new CustomTable.ColumnGenerator() {
            @Override
            public Object generateCell(CustomTable source, final Object itemId, Object columnId) {
                Button removeBtn = new Button(null, new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        purchaseJpa.removeItem(itemId);

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
        if (event.getSource() == purchaseTable && event.isDoubleClick()) {
            EntityItem<Purchase> item = purchaseJpa.getItem(event.getItemId());

            PurchaseForm purchaseForm = new PurchaseForm(item, supplierJpa, categoryJpa, cellJpa);
            purchaseForm.addSaveListener(new AbstractForm.ClickListener() {
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
            setContent(purchaseForm);
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton() == newBtn) {
            newButtonClick();
        }
    }

    private void newButtonClick() {
        final EntityItem<Purchase> newProcurement = purchaseJpa.createEntityItem(new Purchase());

        PurchaseForm purchaseForm = new PurchaseForm(newProcurement, supplierJpa, categoryJpa, cellJpa);
        purchaseForm.addSaveListener(new AbstractForm.ClickListener() {
            @Override
            public void saved(AbstractForm.ClickEvent event) {
                purchaseJpa.addEntity(newProcurement.getEntity());

                commit();
                setContent(getStandardViewLayout());
            }

            @Override
            public void cancelled(AbstractForm.ClickEvent event) {
                setContent(getStandardViewLayout());
            }
        });

        setContent(purchaseForm);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        purchaseJpa.refresh();
        supplierJpa.refresh();
        categoryJpa.refresh();
        cellJpa.refresh();
    }

    @Override
    public void commitContainers() {
        purchaseJpa.commit();
    }

    @Override
    public void commitFailed() {
        purchaseJpa.refresh();
    }
}

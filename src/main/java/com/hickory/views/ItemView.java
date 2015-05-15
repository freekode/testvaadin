package com.hickory.views;

import com.hickory.models.*;
import com.hickory.popups.ItemAmountPopup;
import com.hickory.popups.ModalPopup;
import com.hickory.services.ContainerBuilder;
import com.hickory.views.forms.AbstractEditForm;
import com.hickory.views.forms.AbstractForm;
import com.hickory.views.forms.CategoryEditor;
import com.hickory.views.forms.ItemForm;
import com.hickory.views.services.CategoryConverter;
import com.hickory.views.services.FilterTableDecorator;
import com.hickory.views.services.OrdersConverter;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import org.tepi.filtertable.FilterTable;
import org.vaadin.peter.contextmenu.ContextMenu;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Evgeny Frolov
 */
public class ItemView extends CommonView implements Button.ClickListener, ItemClickEvent.ItemClickListener {
    private JPAContainer<Item> itemJpa;
    private JPAContainer<Shipment> shipmentJpa;
    private JPAContainer<Category> categoryJpa;
    private JPAContainer<StoreRowCell> cellJpa;
    private JPAContainer<CellItem> cellItemJpa;
    private FilterTable itemTable;
    private Button newButton;
    private Tree categoryTree;
    private ContextMenu.ContextMenuItem newMenuItem;
    private ContextMenu.ContextMenuItem editMenuItem;
    private ContextMenu.ContextMenuItem deleteMenuItem;
    private Set<OrderItem> cartItemSet;
    private Container.Filter shipmentItemFilter;


    public ItemView() {
        itemJpa = ContainerBuilder.getContainer(Item.class);
        shipmentJpa = ContainerBuilder.getContainer(Shipment.class);
        categoryJpa = ContainerBuilder.getContainer(Category.class);
        cellJpa = ContainerBuilder.getContainer(StoreRowCell.class);
        cellItemJpa = ContainerBuilder.getContainer(CellItem.class);




        cartItemSet = (Set<OrderItem>) VaadinSession.getCurrent().getAttribute("cart");
        if (cartItemSet == null) cartItemSet = new HashSet<>();


//        Label label = new Label("<h2>Категории</h2>", ContentMode.HTML);
//        label.setWidth("200px");

        newButton = new Button("Новый", this);


        ComboBox shipmentComboBox = new ComboBox();
        shipmentComboBox.setContainerDataSource(shipmentJpa);
        shipmentComboBox.setConverter(new SingleSelectConverter<Shipment>(shipmentComboBox));
        shipmentComboBox.setFilteringMode(FilteringMode.CONTAINS);
        shipmentComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ITEM);
        shipmentComboBox.setNullSelectionAllowed(true);

        shipmentComboBox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (shipmentItemFilter != null) {
                    itemJpa.removeContainerFilter(shipmentItemFilter);
                }

                if (event.getProperty().getValue() == null) {
                    return;
                }

                shipmentItemFilter = new Compare.Equal("shipment", shipmentJpa.getItem(event.getProperty().getValue()).getEntity());
                itemJpa.addContainerFilter(shipmentItemFilter);
            }
        });


//        itemJpa.addContainerFilter(new Compare.Equal("category", categoryJpa.getItem(new Object()).getEntity()));
        itemTable = configureItemTable(buildItemTable(itemJpa));


        categoryJpa = ContainerBuilder.getContainer(Category.class);
        categoryJpa.setParentProperty("parentCategory");


        ItemClickEvent.ItemClickListener itemClickListener = new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.getSource() == categoryTree) {
                    if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
                        categoryTree.select(event.getItemId());
                    }

                    itemJpa.removeAllContainerFilters();
                    itemJpa.addContainerFilter(new Compare.Equal("category", categoryJpa.getItem(event.getItemId()).getEntity()));
                }
            }
        };
        categoryTree = buildCategoryTree(categoryJpa, itemClickListener);
        buildContextMenu(categoryTree);


        getStandardViewLayout().addTop(newButton);
        getStandardViewLayout().addTop(shipmentComboBox);
        getStandardViewLayout().addContent(itemTable);

        setContent(getStandardViewLayout());
    }

    public static FilterTable buildItemTable(Container container) {
        FilterTable table = new FilterTable();

        table.setContainerDataSource(container);
        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);
        table.setSelectable(true);
        table.setFilterBarVisible(true);
        table.addStyleName("no-stripes");
        table.addStyleName("borderless");
        table.setRowHeaderMode(CustomTable.RowHeaderMode.INDEX);

        table.setVisibleColumns("factoryCode", "originalCode", "printName", "brand",
                "inPrice", "tradePrice", "retailPrice", "applicable", "country", "gtd");

        table.setColumnHeaders("Код произв.", "Оригинальный код", "Наименование", "Бренд",
                "Цена", "Оптовая", "Розничная", "Применение", "Страна", "ГТД");

        table.setConverter("orders", new OrdersConverter());
        table.setConverter("category", new CategoryConverter());
//        table.setConverter("cells", new CellConverter());

        table.setFilterDecorator(new FilterTableDecorator());
//        table.setFilterGenerator(new FilterTableGenerator());

        table.setWidth("1250px");
        return table;
    }

    public static Tree buildCategoryTree(Container container, ItemClickEvent.ItemClickListener listener) {
        Tree tree = new Tree();
        tree.setWidth("200px");
        tree.setContainerDataSource(container);
        tree.setItemCaptionPropertyId("title");
        tree.addItemClickListener(listener);
        tree.setSelectable(true);

        return tree;
    }

    public FilterTable configureItemTable(FilterTable table) {
        table.addItemClickListener(this);

        table.addGeneratedColumn("genCart", new CustomTable.ColumnGenerator() {
            @Override
            public Object generateCell(CustomTable source, final Object itemId, Object columnId) {
                Button addButton = new Button(null, new Button.ClickListener() {
                    @Override
                    public void buttonClick(final Button.ClickEvent clickEvent) {
                        ItemAmountPopup amountPopup = new ItemAmountPopup(1);
                        amountPopup.addPopupListener(new ModalPopup.ClickListener() {
                            @Override
                            public void clicked(ModalPopup.ClickEvent event) {
                                ItemAmountPopup.SaveEvent e = (ItemAmountPopup.SaveEvent) event;
                                OrderItem orderItem = new OrderItem();
                                orderItem.setItem(itemJpa.getItem(itemId).getEntity());

                                if (!orderItem.setAmount(e.getAmount())) {
                                    Notification.show("Ошибка выставления количества", Notification.Type.ERROR_MESSAGE);
                                    return;
                                }


                                cartItemSet.add(orderItem);
                                VaadinSession.getCurrent().setAttribute("cart", cartItemSet);

                                ((Button) clickEvent.getSource()).setEnabled(false);
                            }
                        });

                        UI.getCurrent().addWindow(amountPopup);
                    }
                });

                addButton.setStyleName("borderless");
                addButton.setIcon(FontAwesome.SHOPPING_CART);

                if (itemJpa.getItem(itemId).getEntity().getLeftAmount() == 0) {
                    addButton.setEnabled(false);
                } else {
                    for (OrderItem orderItem : cartItemSet) {
                        if (orderItem.getItem().equals(itemJpa.getItem(itemId).getEntity())) {
                            addButton.setEnabled(false);
                        }
                    }
                }


                return addButton;
            }
        });

        table.addGeneratedColumn("genRemove", new CustomTable.ColumnGenerator() {
            @Override
            public Object generateCell(CustomTable source, final Object itemId, Object columnId) {
                Button del = new Button(null, new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        itemJpa.removeItem(itemId);
                        commit();
                    }
                });
                del.setIcon(FontAwesome.TRASH_O);
                del.setStyleName("borderless");
                return del;
            }
        });


        table.setVisibleColumns("genCart", "factoryCode", "originalCode", "printName", "brand", "reservedAmount",
                "leftAmount", "inPrice", "tradePrice", "retailPrice", "applicable", "genRemove");

        table.setColumnHeaders("Корзина", "Код произв.", "Оригинальный код", "Наименование", "Бренд", "Зарез.",
                "Остаток", "Цена", "Оптовая", "Розничная", "Применение", "Удалить");

        return table;
    }

    @Override
    public void itemClick(ItemClickEvent event) {
        if (event.getSource() == itemTable && event.isDoubleClick()) {
            EntityItem<Item> item = itemJpa.getItem(event.getItemId());

            ItemForm itemForm = new ItemForm(item, categoryJpa, cellJpa, cellItemJpa);
            itemForm.addSaveListener(new AbstractForm.ClickListener() {
                @Override
                public void saved(AbstractForm.ClickEvent event) {
                    commit();
                    setContent(getStandardViewLayout());
                }

                @Override
                public void cancelled(AbstractForm.ClickEvent event) {
                    cellItemJpa.discard();
                    setContent(getStandardViewLayout());
                }
            });
            setContent(itemForm);
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton() == newButton) {
            newButtonClick();
        }
    }

    private ContextMenu buildContextMenu(AbstractClientConnector component) {
        ContextMenu contextMenu = new ContextMenu();
        CategoryMenu categoryMenu = new CategoryMenu();
        newMenuItem = contextMenu.addItem("Добавить");
        newMenuItem.addItemClickListener(categoryMenu);

        editMenuItem = contextMenu.addItem("Редактировать");
        editMenuItem.addItemClickListener(categoryMenu);

        deleteMenuItem = contextMenu.addItem("Удалить");
        deleteMenuItem.addItemClickListener(categoryMenu);
        contextMenu.setAsContextMenuOf(component);

        return contextMenu;
    }

    private void newButtonClick() {
        final EntityItem<Item> newItem = itemJpa.createEntityItem(new Item());

        ItemForm itemForm = new ItemForm(newItem, categoryJpa, cellJpa, cellItemJpa);
        itemForm.addSaveListener(new AbstractForm.ClickListener() {
            @Override
            public void saved(AbstractForm.ClickEvent event) {
                itemJpa.addEntity(newItem.getEntity());

                commit();
                setContent(getStandardViewLayout());
            }

            @Override
            public void cancelled(AbstractForm.ClickEvent event) {
                cellItemJpa.discard();
                setContent(getStandardViewLayout());
            }
        });
        setContent(itemForm);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        itemJpa.refresh();
        shipmentJpa.refresh();
        categoryJpa.refresh();
    }

    @Override
    public void commitContainers() {
        itemJpa.commit();
        cellItemJpa.commit();
    }

    @Override
    public void commitFailed() {
        itemJpa.refresh();
        cellItemJpa.refresh();
    }

    private class CategoryMenu implements ItemClickEvent.ItemClickListener,
            ContextMenu.ContextMenuItemClickListener {

        @Override
        public void itemClick(ItemClickEvent event) {
            if (event.getSource() == categoryTree) {
                if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
                    categoryTree.select(event.getItemId());
                }
            }
        }

        @Override
        public void contextMenuItemClicked(ContextMenu.ContextMenuItemClickEvent event) {
            if (event.getSource() == newMenuItem) {
                newClick();
            } else if (event.getSource() == editMenuItem) {
                editClick();
            } else if (event.getSource() == deleteMenuItem) {
                deleteClick();
            }
        }

        private void newClick() {
            final BeanItem<com.hickory.models.Category> newCategoryItem = new BeanItem<>(new com.hickory.models.Category());

            final CategoryEditor categoryEditor = new CategoryEditor(newCategoryItem, categoryJpa);
            categoryEditor.addSaveListener(new AbstractEditForm.SaveListener() {
                @Override
                public void editorSaved(AbstractEditForm.SaveEvent event) {
                    categoryJpa.addEntity(newCategoryItem.getBean());
                    commitContainers();
                }
            });

            UI.getCurrent().addWindow(categoryEditor);
        }

        private void editClick() {
            com.vaadin.data.Item item = categoryTree.getItem(categoryTree.getValue());

            if (item == null) {
                Notification.show("Please select item");
                return;
            }

            final CategoryEditor categoryEditor = new CategoryEditor(item, categoryJpa);
            categoryEditor.addSaveListener(new AbstractEditForm.SaveListener() {
                @Override
                public void editorSaved(AbstractEditForm.SaveEvent event) {
                    commit();
                }
            });

            UI.getCurrent().addWindow(categoryEditor);
        }

        private void deleteClick() {
            if (categoryTree.getItem(categoryTree.getValue()) == null) {
                Notification.show("Please select item");
                return;
            }

            categoryJpa.removeItem(categoryTree.getValue());
            commitContainers();
        }

        private void commitContainers() {
            categoryJpa.commit();
        }
    }
}

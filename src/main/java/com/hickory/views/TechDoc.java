package com.hickory.views;

import com.hickory.models.Category;
import com.hickory.models.Item;
import com.hickory.models.StoreRowCell;
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
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.*;
import org.tepi.filtertable.FilterTable;
import org.vaadin.peter.contextmenu.ContextMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TechDoc extends CommonView implements Button.ClickListener, ItemClickEvent.ItemClickListener {
    private JPAContainer<Item> itemJpa;
    private JPAContainer<Category> categoryJpa;
    private JPAContainer<StoreRowCell> cellJpa;
    private FilterTable itemTable;
    private Button newButton;
    private Tree categoryTree;
    private ContextMenu.ContextMenuItem newMenuItem;
    private ContextMenu.ContextMenuItem editMenuItem;
    private ContextMenu.ContextMenuItem deleteMenuItem;
    private HashMap<Item, Integer> cartItems;

    public TechDoc() {
        cellJpa = ContainerBuilder.getContainer(StoreRowCell.class);


        cartItems = (HashMap<Item, Integer>) VaadinSession.getCurrent().getAttribute("cart");
        if (cartItems == null) cartItems = new HashMap<>();


//        Label label = new Label("<h2>Категории</h2>", ContentMode.HTML);
//        label.setWidth("200px");

        newButton = new Button("Новый", this);

        itemJpa = ContainerBuilder.getContainer(Item.class);
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

        table.setVisibleColumns("otherCode", "originalCode", "printName", "brand",
                "inPrice", "tradePrice", "retailPrice", "applicable", "barcode");

        table.setColumnHeaders("Код произв.", "Оригинальный код", "Наименование", "Бренд",
                "Цена", "Оптовая", "Розничная", "Применение", "Штрихкод");

        table.setConverter("orders", new OrdersConverter());
        table.setConverter("category", new CategoryConverter());
//        table.setConverter("cells", new CellConverter());

        table.setFilterDecorator(new FilterTableDecorator());
//        table.setFilterGenerator(new FilterTableGenerator());

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

    @Override
    public void itemClick(ItemClickEvent event) {
        if (event.getSource() == itemTable && event.isDoubleClick()) {
            EntityItem<Item> item = itemJpa.getItem(event.getItemId());

//            ItemForm itemForm = new ItemForm(item, categoryJpa, cellJpa);
//            itemForm.addSaveListener(new AbstractForm.ClickListener() {
//                @Override
//                public void saved(AbstractForm.ClickEvent event) {
//                    commit();
//                    setContent(getStandardViewLayout());
//                }
//
//                @Override
//                public void cancelled(AbstractForm.ClickEvent event) {
//                    setContent(getStandardViewLayout());
//                }
//            });
//            setContent(itemForm);
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton() == newButton) {
            newButtonClick();
        }
    }

    public FilterTable configureItemTable(FilterTable table) {
        table.addItemClickListener(this);

        table.addGeneratedColumn("genCart", new CustomTable.ColumnGenerator() {
            @Override
            public Object generateCell(CustomTable source, final Object itemId, Object columnId) {
                Button addButton = new Button(null, new Button.ClickListener() {
                    @Override
                    public void buttonClick(final Button.ClickEvent clickEvent) {
                        ItemAmountPopup itemAmountPopup = new ItemAmountPopup();
                        itemAmountPopup.addSaveListener(new ItemAmountPopup.SaveListener() {
                            @Override
                            public void editorSaved(ItemAmountPopup.SaveEvent event) {
                                cartItems.put(itemJpa.getItem(itemId).getEntity(), event.getAmount());
                                VaadinSession.getCurrent().setAttribute("cart", cartItems);

                                ((Button) clickEvent.getSource()).setEnabled(false);
                            }
                        });

                        UI.getCurrent().addWindow(itemAmountPopup);
                    }
                });

                addButton.setStyleName("borderless");
                addButton.setIcon(FontAwesome.SHOPPING_CART);

                if (cartItems.containsKey(itemJpa.getItem(itemId))) addButton.setEnabled(false);

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


        table.setVisibleColumns("genCart", "otherCode", "originalCode", "printName", "brand", "allAmount",
                "inPrice", "tradePrice", "retailPrice", "applicable", "barcode", "genRemove");

        table.setColumnHeaders("Корзина", "Код произв.", "Оригинальный код", "Наименование", "Бренд", "Кол-во",
                "Цена", "Оптовая", "Розничная", "Применение", "Штрихкод", "Удалить");

        return table;
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

//        ItemForm itemForm = new ItemForm(newItem, categoryJpa, cellJpa);
//        itemForm.addSaveListener(new AbstractForm.ClickListener() {
//            @Override
//            public void saved(AbstractForm.ClickEvent event) {
//                itemJpa.addEntity(newItem.getEntity());
//
//                commit();
//                setContent(getStandardViewLayout());
//            }
//
//            @Override
//            public void cancelled(AbstractForm.ClickEvent event) {
//                setContent(getStandardViewLayout());
//            }
//        });
//        setContent(itemForm);
    }

    @Override
    public void commitContainers() {
        itemJpa.commit();
    }

    @Override
    public void commitFailed() {
        itemJpa.refresh();
    }

    public static class ItemAmountPopup extends Window implements Button.ClickListener {
        private List<SaveListener> saveListeners;
        private TextField amountField;
        private Button saveBtn;


        public ItemAmountPopup() {
            setModal(true);
            setWidth("350px");
            saveListeners = new ArrayList<>();

            amountField = new TextField("Количество", "1");

            saveBtn = new Button("Дальше", this);

            FormLayout mainLayout = new FormLayout();
            mainLayout.addComponent(amountField);
            mainLayout.addComponent(saveBtn);


            setContent(mainLayout);
        }

        public void addSaveListener(SaveListener listener) {
            saveListeners.add(listener);
        }

        @Override
        public void buttonClick(Button.ClickEvent event) {
            if (event.getButton() == saveBtn) {
                saveBtnClick();
            }
        }

        public void saveBtnClick() {
            fireSaveEvent();
            close();
        }

        public void fireSaveEvent() {
            SaveEvent e = new SaveEvent(saveBtn, Integer.parseInt(amountField.getValue()));
            for (SaveListener listener : saveListeners) {
                listener.editorSaved(e);
            }
        }

        public interface SaveListener {
            public void editorSaved(SaveEvent event);
        }

        public static class SaveEvent extends Event {
            private Integer amount;

            public SaveEvent(Component source, Integer amount) {
                super(source);

                this.amount = amount;
            }

            public Integer getAmount() {
                return amount;
            }
        }
    }

    private class CategoryMenu implements ItemClickEvent.ItemClickListener, ContextMenu.ContextMenuItemClickListener {

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
            final BeanItem<Category> newCategoryItem = new BeanItem<>(new com.hickory.models.Category());

            final CategoryEditor categoryEditor = new CategoryEditor(newCategoryItem, categoryJpa);
            categoryEditor.addSaveListener(new AbstractEditForm.SaveListener() {
                @Override
                public void editorSaved(AbstractEditForm.SaveEvent event) {
                    categoryJpa.addEntity(newCategoryItem.getBean());
                    commit();
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
            commit();
        }

        private void commitContainers() {
            categoryJpa.commit();
        }
    }
}

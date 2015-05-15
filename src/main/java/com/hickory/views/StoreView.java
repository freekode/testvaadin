package com.hickory.views;

import com.hickory.models.*;
import com.hickory.services.ContainerBuilder;
import com.hickory.views.forms.*;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.filter.JoinFilter;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.ui.*;
import org.tepi.filtertable.FilterTable;
import org.vaadin.peter.contextmenu.ContextMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evgeny Frolov
 */
public class StoreView extends CommonView implements ContextMenu.ContextMenuOpenedListener.TreeListener,
        ItemClickEvent.ItemClickListener, ContextMenu.ContextMenuItemClickListener {
    private JPAContainer<Store> storeJpa;
    private JPAContainer<StoreRow> storeRowJpa;
    private JPAContainer<StoreRowCell> storeRowCellJpa;
    private JPAContainer<Item> itemJpa;
    private HierarchicalContainer hierarchicalContainer;
    private Tree storeTree;
    private FilterTable itemTable;

    private ContextMenu.ContextMenuItem newStoreMenuItem;
    private ContextMenu.ContextMenuItem newStoreRowMenuItem;
    private ContextMenu.ContextMenuItem newStoreRowCellMenuItem;
    private ContextMenu.ContextMenuItem editMenuItem;
    private ContextMenu.ContextMenuItem deleteMenuItem;
    private Button newItemBtn;


    public StoreView() {
        newItemBtn = new Button("Добавить товар", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                editStoreRowCell();
            }
        });
        newItemBtn.setEnabled(false);

        storeJpa = ContainerBuilder.getContainer(Store.class);
        storeRowJpa = ContainerBuilder.getContainer(StoreRow.class);
        storeRowCellJpa = ContainerBuilder.getContainer(StoreRowCell.class);
        itemJpa = ContainerBuilder.getContainer(Item.class);
        itemJpa.addContainerFilter(new Compare.Equal("id", 0));

        hierarchicalContainer = new HierarchicalContainer();
        hierarchicalContainer.addContainerProperty("itemId", Integer.class, null);
        hierarchicalContainer.addContainerProperty("title", String.class, null);
        buildHierarchy(hierarchicalContainer);

        storeTree = buildStoreTree(hierarchicalContainer, this);
        buildContextMenu(storeTree);

        itemTable = ItemView.buildItemTable(itemJpa);


        HorizontalLayout horizontalLayout = new HorizontalLayout(storeTree, itemTable);
        horizontalLayout.setSpacing(true);


        getStandardViewLayout().addTop(newItemBtn);
        getStandardViewLayout().addContent(horizontalLayout);

        setContent(getStandardViewLayout());
    }

    public static Tree buildStoreTree(Container container, ItemClickEvent.ItemClickListener listener) {
        Tree tree = new Tree();
        tree.setContainerDataSource(container);
        tree.setItemCaptionPropertyId("title");
        tree.addItemClickListener(listener);
        tree.setSelectable(true);

        return tree;
    }

    private HierarchicalContainer buildHierarchy(HierarchicalContainer container) {
        for (Object itemId : storeJpa.getItemIds()) {
            Store store = storeJpa.getItem(itemId).getEntity();
            container.addItem(store);
            container.getItem(store).getItemProperty("itemId").setValue(itemId);
            container.getItem(store).getItemProperty("title").setValue(store.getTitle());
        }

        for (Object itemId : storeRowJpa.getItemIds()) {
            StoreRow storeRow = storeRowJpa.getItem(itemId).getEntity();
            container.addItem(storeRow);
            container.setParent(storeRow, storeRow.getStore());
            container.getItem(storeRow).getItemProperty("itemId").setValue(itemId);
            container.getItem(storeRow).getItemProperty("title").setValue(storeRow.getBarcode());
        }

        for (Object itemId : storeRowCellJpa.getItemIds()) {
            StoreRowCell storeRowCell = storeRowCellJpa.getItem(itemId).getEntity();
            container.addItem(storeRowCell);
            container.setParent(storeRowCell, storeRowCell.getRow());
            container.getItem(storeRowCell).getItemProperty("itemId").setValue(itemId);
            container.getItem(storeRowCell).getItemProperty("title").setValue(storeRowCell.getBarcode().toString());
        }

        return container;
    }

    private ContextMenu buildContextMenu(AbstractClientConnector component) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.addContextMenuTreeListener(this);

        newStoreMenuItem = contextMenu.addItem("Добавить склад");
        newStoreMenuItem.addItemClickListener(this);

        newStoreRowMenuItem = contextMenu.addItem("Добавить ряд");
        newStoreRowMenuItem.addItemClickListener(this);

        newStoreRowCellMenuItem = contextMenu.addItem("Добавить ячейку");
        newStoreRowCellMenuItem.addItemClickListener(this);

        editMenuItem = contextMenu.addItem("Редактировать");
        editMenuItem.addItemClickListener(this);

        deleteMenuItem = contextMenu.addItem("Удалить");
        deleteMenuItem.addItemClickListener(this);

        contextMenu.setAsContextMenuOf(component);

        return contextMenu;
    }

    @Override
    public void itemClick(ItemClickEvent event) {
        if (event.getSource() == storeTree) {
            storeTree.select(event.getItemId());

            if (storeTree.getValue() instanceof StoreRowCell) {
                newItemBtn.setEnabled(true);
                com.vaadin.data.Item item = storeTree.getItem(storeTree.getValue());
                EntityItem<StoreRowCell> cellItem = storeRowCellJpa.getItem(item.getItemProperty("itemId").getValue());

                itemJpa.removeAllContainerFilters();
                itemJpa.addContainerFilter(new JoinFilter("cells", new Compare.Equal("id", cellItem.getItemId())));
            } else {
                newItemBtn.setEnabled(false);
            }
        } else {
            newItemBtn.setEnabled(false);
        }
    }

    @Override
    public void contextMenuItemClicked(ContextMenu.ContextMenuItemClickEvent event) {
        if (event.getSource() == newStoreMenuItem) {
            newStore();
        } else if (event.getSource() == newStoreRowMenuItem) {
            newStoreRow();
        } else if (event.getSource() == newStoreRowCellMenuItem) {
            newStoreRowCell();
        } else if (event.getSource() == editMenuItem) {
            editClick();
        } else if (event.getSource() == deleteMenuItem) {
            deleteClick();
        }
    }

    @Override
    public void onContextMenuOpenFromTreeItem(ContextMenu.ContextMenuOpenedOnTreeItemEvent event) {
        if (storeTree.getValue() instanceof Store) {
            newStoreRowCellMenuItem.setEnabled(false);
        } else if (storeTree.getValue() instanceof StoreRow) {
            newStoreRowCellMenuItem.setEnabled(true);
        } else if (storeTree.getValue() instanceof StoreRowCell) {
            newStoreRowCellMenuItem.setEnabled(true);
        }
    }


    protected void newStore() {
        final BeanItem<Store> newStoreItem = new BeanItem<>(new Store());

        final StoreEditor storeEditor = new StoreEditor(newStoreItem);
        storeEditor.addSaveListener(new AbstractEditForm.SaveListener() {
            @Override
            public void editorSaved(AbstractEditForm.SaveEvent event) {
                storeJpa.addEntity(newStoreItem.getBean());
                commit();
                buildHierarchy(hierarchicalContainer);
            }
        });

        UI.getCurrent().addWindow(storeEditor);
    }

    protected void newStoreRow() {
        StoreRow newStoreRow = new StoreRow();

        if (storeTree.getValue() instanceof Store) {
            newStoreRow.setStore((Store) storeTree.getValue());
        } else if (storeTree.getValue() instanceof StoreRow) {
            StoreRow storeRow = (StoreRow) storeTree.getValue();
            newStoreRow.setStore(storeRow.getStore());
        } else if (storeTree.getValue() instanceof StoreRowCell) {
            StoreRowCell storeRowCell = (StoreRowCell) storeTree.getValue();
            newStoreRow.setStore(storeRowCell.getRow().getStore());
        }

        storeRowJpa.addEntity(newStoreRow);
        commit();
        buildHierarchy(hierarchicalContainer);
    }

    protected void newStoreRowCell() {
        StoreRowCell newStoreRowCell = new StoreRowCell();

        if (storeTree.getValue() instanceof StoreRow) {
            newStoreRowCell.setRow((StoreRow) storeTree.getValue());
        } else if (storeTree.getValue() instanceof StoreRowCell) {
            StoreRowCell storeRowCell = (StoreRowCell) storeTree.getValue();
            newStoreRowCell.setRow(storeRowCell.getRow());

        }

        storeRowCellJpa.addEntity(newStoreRowCell);
        commit();
        buildHierarchy(hierarchicalContainer);
    }


    protected void editClick() {
        if (storeTree.getValue() instanceof Store) {
            editStore();
        } else if (storeTree.getValue() instanceof StoreRow) {
            editStoreRow();
        } else if (storeTree.getValue() instanceof StoreRowCell) {
            editStoreRowCell();
        } else {
            editStoreRowCell();
        }
    }

    protected void editStore() {
        com.vaadin.data.Item item = storeTree.getItem(storeTree.getValue());
        com.vaadin.data.Item storeItem = storeJpa.getItem(item.getItemProperty("itemId").getValue());

        StoreEditor storeEditor = new StoreEditor(storeItem);
        storeEditor.addSaveListener(new AbstractEditForm.SaveListener() {
            @Override
            public void editorSaved(AbstractEditForm.SaveEvent event) {
                commit();
                buildHierarchy(hierarchicalContainer);
            }
        });

        UI.getCurrent().addWindow(storeEditor);
    }

    protected void editStoreRow() {
        com.vaadin.data.Item item = storeTree.getItem(storeTree.getValue());
        com.vaadin.data.Item rowItem = storeRowJpa.getItem(item.getItemProperty("itemId").getValue());

        StoreRowEditor storeRowEditor = new StoreRowEditor(rowItem);
        storeRowEditor.addSaveListener(new AbstractEditForm.SaveListener() {
            @Override
            public void editorSaved(AbstractEditForm.SaveEvent event) {
                commit();
                buildHierarchy(hierarchicalContainer);
            }
        });

        UI.getCurrent().addWindow(storeRowEditor);
    }

    protected void editStoreRowCell() {
        newItemBtn.setEnabled(true);
        com.vaadin.data.Item item = storeTree.getItem(storeTree.getValue());
        EntityItem<StoreRowCell> cellItem = storeRowCellJpa.getItem(item.getItemProperty("itemId").getValue());

        final List<Container.Filter> itemsFilters = new ArrayList<>(itemJpa.getFilters());
        itemJpa.removeAllContainerFilters();

        StoreRowCellForm storeRowCellForm = new StoreRowCellForm(cellItem, itemJpa);
        storeRowCellForm.addSaveListener(new AbstractForm.ClickListener() {
            @Override
            public void saved(AbstractForm.ClickEvent event) {
                commit();
                itemJpa.refresh();
                storeRowCellJpa.refresh();
                buildHierarchy(hierarchicalContainer);

                for (Container.Filter filter : itemsFilters) {
                    itemJpa.addContainerFilter(filter);
                }

                setContent(getStandardViewLayout());
            }

            @Override
            public void cancelled(AbstractForm.ClickEvent event) {
                setContent(getStandardViewLayout());
            }
        });
        setContent(storeRowCellForm);
    }

    protected void deleteClick() {
        if (itemTable.getItem(itemTable.getValue()) == null) {
            Notification.show("Please select item");
            return;
        }

        storeJpa.removeItem(itemTable.getValue());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        storeJpa.refresh();
        storeRowJpa.refresh();
        storeRowCellJpa.refresh();
        itemJpa.refresh();
    }

    @Override
    public void commitContainers() {
        itemJpa.commit();
        storeJpa.commit();
        storeRowJpa.commit();
        storeRowCellJpa.commit();
    }

    @Override
    public void commitFailed() {
        itemJpa.refresh();
        storeJpa.refresh();
        storeRowJpa.refresh();
        storeRowCellJpa.refresh();
    }
}

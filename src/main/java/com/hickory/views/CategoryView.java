package com.hickory.views;

import com.hickory.models.Category;
import com.hickory.services.ContainerBuilder;
import com.hickory.views.forms.AbstractEditForm;
import com.hickory.views.forms.CategoryEditor;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import org.vaadin.peter.contextmenu.ContextMenu;

/**
 * @author Evgeny Frolov
 */
public class CategoryView extends CommonView implements ItemClickEvent.ItemClickListener,
        ContextMenu.ContextMenuItemClickListener {

    private JPAContainer<Category> categoryJpa;
    private Tree categoryTree;
    private ContextMenu.ContextMenuItem newMenuItem;
    private ContextMenu.ContextMenuItem editMenuItem;
    private ContextMenu.ContextMenuItem deleteMenuItem;


    public CategoryView() {
        categoryJpa = ContainerBuilder.getContainer(Category.class);
        categoryJpa.setParentProperty("parentCategory");

        categoryTree = buildCategoryTree(categoryJpa, this);
        buildContextMenu(categoryTree);


        getStandardViewLayout().addContent(categoryTree);
        setContent(getStandardViewLayout());
    }


    public static Tree buildCategoryTree(Container container, ItemClickEvent.ItemClickListener listener) {
        Tree tree = new Tree();
        tree.setContainerDataSource(container);
        tree.setItemCaptionPropertyId("title");
        tree.addItemClickListener(listener);
        tree.setSelectable(true);

        return tree;
    }

    private ContextMenu buildContextMenu(AbstractClientConnector component) {
        ContextMenu contextMenu = new ContextMenu();
        newMenuItem = contextMenu.addItem("Добавить");
        newMenuItem.addItemClickListener(this);

        editMenuItem = contextMenu.addItem("Редактировать");
        editMenuItem.addItemClickListener(this);

        deleteMenuItem = contextMenu.addItem("Удалить");
        deleteMenuItem.addItemClickListener(this);
        contextMenu.setAsContextMenuOf(component);


        return contextMenu;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
    }

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
        final BeanItem<Category> newCategoryItem = new BeanItem<>(new Category());

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
        Item item = categoryTree.getItem(categoryTree.getValue());

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

    @Override
    public void commitContainers() {
        categoryJpa.commit();
    }

    @Override
    public void commitFailed() {
        categoryJpa.refresh();
    }
}

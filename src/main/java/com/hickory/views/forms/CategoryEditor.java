package com.hickory.views.forms;

import com.hickory.models.Category;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;


/**
 * @author Evgeny Frolov
 */
public class CategoryEditor extends AbstractEditForm {
    public CategoryEditor(Item item, Container category) {
        super(item);

        center();
        setWidth("500px");
        setResizable(false);
        setCaption("Форма заполнения Категории");


        TextField title = new TextField("Название");
        title.setNullRepresentation("");
        title.setRequired(true);

        TextField description = new TextField("Описание");
        description.setNullRepresentation("");

        ComboBox parentCategory = new ComboBox("Родитель");
        parentCategory.setContainerDataSource(category);
        parentCategory.setConverter(new SingleSelectConverter<Category>(parentCategory));
        parentCategory.setItemCaptionPropertyId("title");


        addAndBind(title, "title");
        addAndBind(description, "description");
        addAndBind(parentCategory, "parentCategory");
    }
}

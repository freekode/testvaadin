package com.hickory.views.forms;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evgeny Frolov
 */
public abstract class AbstractEditForm extends Window implements Button.ClickListener {
    private final Item item;
    private List<SaveListener> saveListeners;
    private FieldGroup fieldGroup;
    private FormLayout formLayout;
    private Button saveButton;
    private Button cancelButton;
    private ProgressBar progressBar;


    public AbstractEditForm(Item item) {
        setModal(true);
        this.item = item;
        saveListeners = new ArrayList<>();


        fieldGroup = new FieldGroup(item);
        fieldGroup.setBuffered(true);


        saveButton = new Button("Сохранить", this);
        cancelButton = new Button("Отменить", this);

        progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setSizeUndefined();


        formLayout = new FormLayout();
        formLayout.setImmediate(true);


        HorizontalLayout footerLayout = new HorizontalLayout();
        footerLayout.setSpacing(true);
        footerLayout.addComponent(saveButton);
        footerLayout.addComponent(cancelButton);
        footerLayout.addComponent(progressBar);
        footerLayout.setComponentAlignment(progressBar, Alignment.MIDDLE_LEFT);

        FormLayout mainLayout = new FormLayout();
        mainLayout.addComponent(formLayout);
        mainLayout.addComponent(footerLayout);


        setContent(mainLayout);
    }

    public void addSaveListener(SaveListener listener) {
        saveListeners.add(listener);
    }

    public void bind(Field<?> field, Object propertyId) {
        getFieldGroup().bind(field, propertyId);
    }

    public void add(Component... component) {
        for (Component unit : component) {
            getFormLayout().addComponent(unit);
        }
    }

    public void addAndBind(Field<?> field, Object propertyId) {
        bind(field, propertyId);
        add(field);
    }

    public void commit() {
        try {
            getFieldGroup().commit();
        } catch (FieldGroup.CommitException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton() == saveButton) {
            savingSequence();
        } else if (event.getButton() == cancelButton) {
            cancelSequence();
        }
    }

    public void savingSequence() {
        try {
            setProcessing(true);
            saveButtonClick();

            fieldGroup.commit();
            fireSaveEvent();
            close();
        } catch (FieldGroup.CommitException e) {
            setProcessing(false);
            e.printStackTrace();
        }
    }

    public void saveButtonClick() {
    }

    public void fireSaveEvent() {
        SaveEvent e = new SaveEvent(saveButton);
        for (SaveListener listener : saveListeners) {
            listener.editorSaved(e);
        }
    }

    public void cancelSequence() {
        cancelButtonClick();

        setProcessing(false);
        fieldGroup.discard();
        close();
    }

    public void cancelButtonClick() {
    }

    private FormLayout getFormLayout() {
        return formLayout;
    }

    private FieldGroup getFieldGroup() {
        return fieldGroup;
    }

    public Item getLocalItem() {
        return item;
    }

    public void setProcessing(boolean visible) {
        progressBar.setVisible(visible);
        saveButton.setEnabled(!visible);
    }

    public interface SaveListener {
        public void editorSaved(SaveEvent event);
    }

    public static class SaveEvent extends Event {
        public SaveEvent(Component source) {
            super(source);
        }
    }
}

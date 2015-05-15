package com.hickory.views.forms;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evgeny Frolov
 */
public abstract class AbstractForm extends VerticalLayout implements Button.ClickListener {
    private List<ClickListener> clickListeners;
    private EntityItem item;
    private FieldGroup fieldGroup;
    private FormLayout formLayout;
    private Label titleLabel;
    private Button saveButton;
    private Button cancelButton;
    private ProgressBar progressBar;


    public AbstractForm(EntityItem item) {
        setSpacing(true);
        setMargin(true);

        this.item = item;
        clickListeners = new ArrayList<>();


        fieldGroup = new FieldGroup(item);
        fieldGroup.setBuffered(true);

        formLayout = new FormLayout();
        formLayout.setImmediate(true);
        formLayout.setMargin(true);

        saveButton = new Button("Сохранить", this);
        cancelButton = new Button("Отменить", this);

        progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setSizeUndefined();


        titleLabel = new Label("Редактирование данных");
        titleLabel.addStyleName("h2");


        HorizontalLayout footerLayout = new HorizontalLayout();
        footerLayout.setSpacing(true);
        footerLayout.setMargin(new MarginInfo(true, false, true, false));
        footerLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        footerLayout.addComponent(saveButton);
        footerLayout.addComponent(cancelButton);
        footerLayout.addComponent(progressBar);

        addComponent(titleLabel);
        addComponent(formLayout);
        addComponent(footerLayout);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton() == saveButton) {
            savingSequence();
        } else if (event.getButton() == cancelButton) {
            cancelSequence();
        }
    }

    public void addSaveListener(ClickListener listener) {
        clickListeners.add(listener);
    }

    public void bind(Field<?> field, Object propertyId) {
        getFieldGroup().bind(field, propertyId);
    }

    public void addToForm(Component... component) {
        for (Component unit : component) {
            getFormLayout().addComponent(unit);
        }
    }

    public void addAndBind(Field<?> field, Object propertyId) {
        bind(field, propertyId);
        addToForm(field);
    }

    public void commit() throws FieldGroup.CommitException {
        getFieldGroup().commit();
    }

    public void discard() {
        getFieldGroup().discard();
    }

    public void savingSequence() {
        try {
            setProcessing(true);
            saveButtonClick();

            commit();
            fireSaveClickEvent();
        } catch (Exception e) {
            setProcessing(false);
            Notification.show("Ошибка сохранения", Notification.Type.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void saveButtonClick() {
    }

    public void fireSaveClickEvent() {
        ClickEvent e = new ClickEvent(saveButton);
        for (ClickListener listener : clickListeners) {
            listener.saved(e);
        }
    }

    public void cancelSequence() {
        setProcessing(false);
        cancelButtonClick();

        discard();
        fireCancelClickEvent();
    }

    public void cancelButtonClick() {
    }

    public void fireCancelClickEvent() {
        ClickEvent e = new ClickEvent(saveButton);
        for (ClickListener listener : clickListeners) {
            listener.cancelled(e);
        }
    }

    private FormLayout getFormLayout() {
        return formLayout;
    }

    public FieldGroup getFieldGroup() {
        return fieldGroup;
    }

    public EntityItem getLocalItem() {
        return item;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public void setTitle(String title) {
        titleLabel.setValue(title);
    }

    public void setProcessing(boolean visible) {
        progressBar.setVisible(visible);
        saveButton.setEnabled(!visible);
    }

    public interface ClickListener {
        public void saved(ClickEvent event);

        public void cancelled(ClickEvent event);
    }

    public static class ClickEvent extends Event {
        public ClickEvent(Component source) {
            super(source);
        }
    }
}

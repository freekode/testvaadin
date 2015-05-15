package com.hickory.popups;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

/**
 * Created by freekode on 04/12/14.
 */
public class ItemAmountPopup extends ModalPopup {
    private TextField amountField;
    private Button saveBtn;


    public ItemAmountPopup(Integer amount) {
        setModal(true);
        setWidth("350px");

        amountField = new TextField("Количество", amount.toString());

        saveBtn = new Button("Дальше", this);

        FormLayout mainLayout = new FormLayout();
        mainLayout.addComponent(amountField);
        mainLayout.addComponent(saveBtn);


        setContent(mainLayout);
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
        for (ClickListener listener : getClickListeners()) {
            listener.clicked(e);
        }
    }

    public static class SaveEvent extends ClickEvent {
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

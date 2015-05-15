package com.hickory.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Notification;

/**
 * Created by Evgeny Frolov on 13/12/14.
 */
public abstract class CommonView extends CommonPanel implements View {
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
    }

    public void commitContainers() {
    }

    public void commitFailed() {
    }

    public void commit() {
        try {
            commitContainers();
        } catch (Exception e) {
            Notification.show("Ошибка при выполнении", Notification.Type.ERROR_MESSAGE);
            e.printStackTrace();
            commitFailed();
        }
    }
}

package com.hickory.popups;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Frolov on 02/12/14.
 */
public abstract class ModalPopup extends Window implements Button.ClickListener {
    private List<ClickListener> clickListeners;


    public ModalPopup() {
        setModal(true);
        setWidth("800px");

        clickListeners = new ArrayList<>();
    }

    public void addPopupListener(ClickListener listener) {
        clickListeners.add(listener);
    }

    public List<ClickListener> getClickListeners() {
        return clickListeners;
    }

    public void fireEvent(Component source) {
        ClickEvent e = new ClickEvent(source);
        for (ClickListener listener : clickListeners) {
            listener.clicked(e);
        }
    }

    public interface ClickListener {
        public void clicked(ClickEvent event);
    }

    public static class ClickEvent extends Event {
        public ClickEvent(Component source) {
            super(source);
        }
    }
}

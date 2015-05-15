package com.hickory.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 */
public class ErrorView extends VerticalLayout implements View {
    public ErrorView() {
        addComponent(new Label("Ooops..."));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}

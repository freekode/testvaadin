package com.hickory.views;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;

/**
 * @author Evgeny Frolov
 */
public abstract class CommonPanel extends Panel {
    private StandardViewLayout standardViewLayout;


    public CommonPanel() {
        setSizeFull();
        addStyleName("borderless");

        standardViewLayout = new StandardViewLayout();

        setContent(standardViewLayout);
    }

    public StandardViewLayout getStandardViewLayout() {
        return standardViewLayout;
    }

    public static class StandardViewLayout extends VerticalLayout {
        private HorizontalLayout topLayout;
        private CssLayout contentLayout;


        public StandardViewLayout() {
            setWidth("100%");
            setSpacing(true);
            setMargin(new MarginInfo(true, true, true, true));

            topLayout = new HorizontalLayout();
            topLayout.setSpacing(true);

            contentLayout = new CssLayout();

            addComponent(topLayout);
            addComponent(contentLayout);
        }

        public void addTop(Component... component) {
            for (Component unit : component) {
                topLayout.addComponent(unit);
            }
        }

        public void addContent(Component... component) {
            for (Component unit : component) {
                contentLayout.addComponent(unit);
            }
        }
    }
}

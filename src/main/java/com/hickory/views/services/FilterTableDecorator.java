package com.hickory.views.services;

import com.hickory.models.interfaces.ConvertibleEnum;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.datefield.Resolution;
import org.tepi.filtertable.FilterDecorator;
import org.tepi.filtertable.numberfilter.NumberFilterPopupConfig;

import java.util.Locale;

/**
 * @author Evgeny Frolov
 */
public class FilterTableDecorator implements FilterDecorator {

    @Override
    public String getEnumFilterDisplayName(Object propertyId, Object value) {
        if (value instanceof ConvertibleEnum) {
            return ((ConvertibleEnum) value).getValue();
        }

        return null;
    }

    @Override
    public Resource getEnumFilterIcon(Object propertyId, Object value) {
        return null;
    }

    @Override
    public String getBooleanFilterDisplayName(Object propertyId, boolean value) {
        return null;
    }

    @Override
    public Resource getBooleanFilterIcon(Object propertyId, boolean value) {
        return null;
    }

    @Override
    public boolean isTextFilterImmediate(Object propertyId) {
        return true;
    }

    @Override
    public int getTextChangeTimeout(Object propertyId) {
        return 500;
    }

    @Override
    public String getFromCaption() {
        return "From";
    }

    @Override
    public String getToCaption() {
        return "To";
    }

    @Override
    public String getSetCaption() {
        return null;
    }

    @Override
    public String getClearCaption() {
        return null;
    }

    @Override
    public Resolution getDateFieldResolution(Object propertyId) {
        return Resolution.SECOND;
    }

    @Override
    public String getDateFormatPattern(Object propertyId) {
//            return "dd.MM.yyyy hh:mm:ss";
        return null;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public String getAllItemsVisibleString() {
        return "All";
    }

    @Override
    public NumberFilterPopupConfig getNumberFilterPopupConfig() {
        return null;
    }

    @Override
    public boolean usePopupForNumericProperty(Object propertyId) {
        return true;
    }
}

package com.hickory.views.services;

import com.hickory.models.Supplier;
import com.vaadin.data.util.converter.Converter;

import java.util.Locale;

/**
 * @author Evgeny Frolov
 */
public class SupplierConverter implements Converter<String, Supplier> {
    @Override
    public Supplier convertToModel(String s, Class<? extends Supplier> aClass, Locale locale) throws ConversionException {
        return null;
    }

    @Override
    public String convertToPresentation(Supplier value, Class<? extends String> aClass, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        }

        return value.getShortcutLegalName();
    }

    @Override
    public Class<Supplier> getModelType() {
        return Supplier.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return null;
    }
}

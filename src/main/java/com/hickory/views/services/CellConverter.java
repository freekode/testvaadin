package com.hickory.views.services;

import com.hickory.models.StoreRowCell;
import com.vaadin.data.util.converter.Converter;

import java.util.Locale;

/**
 * @author Evgeny Frolov
 */
public class CellConverter implements Converter<String, StoreRowCell> {
    @Override
    public StoreRowCell convertToModel(String value, Class<? extends StoreRowCell> targetType, Locale locale) throws ConversionException {
        return null;
    }

    @Override
    public String convertToPresentation(StoreRowCell value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        }

        return value.getBarcode();
    }

    @Override
    public Class<StoreRowCell> getModelType() {
        return null;
    }

    @Override
    public Class<String> getPresentationType() {
        return null;
    }
}

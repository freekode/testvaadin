package com.hickory.views.services;

import com.hickory.models.Client;
import com.vaadin.data.util.converter.Converter;

import java.util.Locale;

/**
 * @author Evgeny Frolov
 */
public class ClientConverter implements Converter<String, Client> {
    @Override
    public Client convertToModel(String value, Class<? extends Client> targetType, Locale locale) throws ConversionException {
        return null;
    }

    @Override
    public String convertToPresentation(Client value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        }

        return value.getShortcutLegalName();
    }

    @Override
    public Class<Client> getModelType() {
        return null;
    }

    @Override
    public Class<String> getPresentationType() {
        return null;
    }
}

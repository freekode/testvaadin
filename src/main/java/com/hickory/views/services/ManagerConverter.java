package com.hickory.views.services;

import com.hickory.models.User;
import com.vaadin.data.util.converter.Converter;

import java.util.Locale;

/**
 * @author Evgeny Frolov
 */
public class ManagerConverter implements Converter<String, User> {
    @Override
    public User convertToModel(String value, Class<? extends User> targetType, Locale locale) throws ConversionException {
        System.out.println("value = " + value);
        return null;
    }

    @Override
    public String convertToPresentation(User value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        }

        return value.getFname() + " " + value.getLname();
    }

    @Override
    public Class<User> getModelType() {
        return User.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}

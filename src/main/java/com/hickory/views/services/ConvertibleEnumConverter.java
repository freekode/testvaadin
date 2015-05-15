package com.hickory.views.services;

import com.hickory.models.interfaces.ConvertibleEnum;
import com.vaadin.data.util.converter.Converter;

import java.util.Locale;

/**
 * @author Evgeny Frolov
 */
public class ConvertibleEnumConverter implements Converter<String, ConvertibleEnum> {
    @Override
    public ConvertibleEnum convertToModel(String value, Class<? extends ConvertibleEnum> targetType, Locale locale) throws ConversionException {
        return null;
    }

    @Override
    public String convertToPresentation(ConvertibleEnum value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        }

        return value.getValue();
    }

    @Override
    public Class<ConvertibleEnum> getModelType() {
        return ConvertibleEnum.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}

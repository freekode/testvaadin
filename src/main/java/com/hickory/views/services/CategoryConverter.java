package com.hickory.views.services;

import com.hickory.models.Category;
import com.vaadin.data.util.converter.Converter;

import java.util.Locale;

/**
 * @author Evgeny Frolov
 */
public class CategoryConverter implements Converter<String, Category> {

    @Override
    public Category convertToModel(String s, Class<? extends Category> aClass, Locale locale) throws ConversionException {
        return null;
    }

    @Override
    public String convertToPresentation(Category category, Class<? extends String> aClass, Locale locale) throws ConversionException {
        if (category == null) {
            return null;
        }

        return category.getTitle();
    }

    @Override
    public Class<Category> getModelType() {
        return null;
    }

    @Override
    public Class<String> getPresentationType() {
        return null;
    }
}

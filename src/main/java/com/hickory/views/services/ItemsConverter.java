package com.hickory.views.services;

import com.hickory.models.Item;
import com.vaadin.data.util.converter.Converter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author Evgeny Frolov
 */
public class ItemsConverter implements Converter<String, Set> {

    @Override
    public Set convertToModel(String value, Class<? extends Set> targetType, Locale locale) throws ConversionException {
        return null;
    }

    @Override
    public String convertToPresentation(Set value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        }

        List<String> nameArray = new ArrayList<>();
        for (Object entry : value) {
            Item item = (Item) entry;
            nameArray.add(item.getPrintName());
        }

        return StringUtils.join(nameArray, ", ");
    }

    @Override
    public Class<Set> getModelType() {
        return null;
    }

    @Override
    public Class<String> getPresentationType() {
        return null;
    }
}

package com.hickory.services;

import com.hickory.MyVaadinUI;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;

/**
 * @author Evgeny Frolov
 */
public class ContainerBuilder {
    public static <T> JPAContainer<T> getContainer(Class<T> requestedClass) {
        JPAContainer<T> newContainer = JPAContainerFactory.makeBatchable(requestedClass, MyVaadinUI.PERSISTENCE_UNIT);
        newContainer.setAutoCommit(false);
        newContainer.setBuffered(true);

        newContainer.getEntityProvider().getEntityManager().getEntityManagerFactory().getCache().evict(requestedClass);

        return newContainer;
    }
}

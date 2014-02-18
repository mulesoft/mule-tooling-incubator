package org.mule.tooling.ui.contribution.munit.common;

/**
 * <p>
 * Common filter interface.
 * </p>
 * 
 * @param <T>
 *            The type of the element to be filtered
 */
public interface Filter<T> {

    boolean accept(T element);
}

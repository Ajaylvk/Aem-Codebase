package com.softwareag.core.util;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Helper class for functional programing in AEM.
 */
public final class FunctionalUtil {

    private FunctionalUtil() {
        // Class is not meant to be instantiated.
    }

    /**
     * Converts an iterator object into a stream to
     * use it with functional features.
     *
     * @param iterator
     *     to convert to stream
     * @param <T>
     *     type of iterator
     * @return stream from iterator
     */
    public static <T> Stream<T> asStream(final Iterator<T> iterator) {
        return asStream(asIterable(iterator));
    }

    /**
     * Converts and iterable to a stream
     *
     * @param iterable
     *     to convert to stream
     * @param <T>
     *     type of iterable
     * @return stream from iterable
     */
    public static <T> Stream<T> asStream(final Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private static <T> Iterable<T> asIterable(final Iterator<T> iterator) {
        return () -> iterator;
    }

}

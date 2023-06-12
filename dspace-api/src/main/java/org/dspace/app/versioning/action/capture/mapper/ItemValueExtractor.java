/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import java.util.function.Function;

import org.dspace.content.Item;

/**
 * Functional interfaces that implements a generic {@link ItemValueExtractor}
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 * @param <R>
 */
@FunctionalInterface
public interface ItemValueExtractor<R> extends Function<Item, R> {

}

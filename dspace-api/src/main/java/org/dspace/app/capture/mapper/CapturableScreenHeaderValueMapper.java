/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture.mapper;

import java.util.function.Function;

import org.dspace.app.capture.model.CapturableScreen;
import org.dspace.content.Item;
import org.dspace.core.Context;

/**
 * Mapper interface that given the {@link Context} and the {@link Item} that
 * will contain the {@link CapturableScreen} taken, computes a custom header
 * value.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
@FunctionalInterface
public interface CapturableScreenHeaderValueMapper extends Function<Context, String> {

}

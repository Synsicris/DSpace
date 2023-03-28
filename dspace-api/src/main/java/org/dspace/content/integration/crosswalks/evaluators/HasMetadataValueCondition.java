/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.integration.crosswalks.evaluators;

import java.util.List;
import java.util.Objects;

import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.core.Context;

/**
 *
 * Condition that given a {@code metadataField} checks if the templating
 * item has any metadata with the value defined in the template condition.
 * <br/>
 * Example:
 * <br/>
 * <blockquote><pre>{@code
 *
 *  @if.hasEntityType.process.start@
 *      // display section
 *  @if.hasEntityType.process.end@
 *
 *  }</pre></blockquote>
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class HasMetadataValueCondition extends HasMetadataCondition {

    private String metadataField;

    public String getMetadataField() {
        return metadataField;
    }

    public void setMetadataField(String metadataField) {
        this.metadataField = metadataField;
    }

    @Override
    protected boolean doTest(Context context, Item item, String condition) {
        return this.hasSameValue(
            super.getMetadataValues(item, metadataField),
            super.extractConditionQualifier(condition)
        );
    }

    protected boolean hasSameValue(List<MetadataValue> metadataValues, String conditionValue) {
        return metadataValues
                .stream()
                .filter(Objects::nonNull)
                .anyMatch(mv -> mv.getValue().equals(conditionValue));
    }

}

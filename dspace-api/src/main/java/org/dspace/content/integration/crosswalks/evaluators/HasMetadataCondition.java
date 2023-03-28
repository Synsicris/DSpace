/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.integration.crosswalks.evaluators;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of {@link ConditionEvaluator} to evaluate
 * if the given item has the specified metadata.
 *
 * @author Luca Giamminonni (luca.giamminonni at 4science.it)
 *
 */
public class HasMetadataCondition extends ConditionEvaluator {

    @Autowired
    protected ItemService itemService;

    @Override
    protected boolean doTest(Context context, Item item, String condition) {
        return CollectionUtils.isNotEmpty(
            getMetadataValues(item, extractMetadataField(condition))
        );
    }

    protected List<MetadataValue> getMetadataValues(Item item, String metadataField) {
        return itemService.getMetadataByMetadataString(item, metadataField);
    }

    protected String extractMetadataField(String condition) {
        return extractConditionQualifier(condition).replaceAll("-", ".");
    }

    protected String extractConditionQualifier(String condition) {
        String[] conditionSections = condition.split("\\.");
        if (conditionSections.length != 2) {
            throw new IllegalArgumentException("Invalid has metadata condition: " + condition);
        }
        return conditionSections[1];
    }

}

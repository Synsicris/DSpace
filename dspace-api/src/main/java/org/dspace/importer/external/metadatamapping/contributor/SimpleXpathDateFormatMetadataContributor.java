/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.importer.external.metadatamapping.contributor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.dspace.importer.external.metadatamapping.MetadataFieldConfig;
import org.dspace.importer.external.metadatamapping.MetadatumDTO;
import org.jaxen.JaxenException;


public class SimpleXpathDateFormatMetadataContributor extends SimpleXpathMetadatumContributor {


    private DateFormat dateFormatFrom;
    private DateFormat dateFormatTo;

    public void setDateFormatFrom(String dateFormatFrom) {
        this.dateFormatFrom = new SimpleDateFormat(dateFormatFrom);
    }

    public void setDateFormatTo(String dateFormatTo) {
        this.dateFormatTo = new SimpleDateFormat(dateFormatTo);
    }

    @Override
    public Collection<MetadatumDTO> contributeMetadata(OMElement t) {
        List<MetadatumDTO> values = new LinkedList<>();
        try {
            AXIOMXPath xpath = new AXIOMXPath(query);
            for (String ns : prefixToNamespaceMapping.keySet()) {
                xpath.addNamespace(prefixToNamespaceMapping.get(ns), ns);
            }
            List<Object> nodes = xpath.selectNodes(t);
            for (Object el : nodes) {
                if (el instanceof OMElement) {
                    values.add(getMetadatum(field, ((OMElement) el).getText()));
                } else if (el instanceof OMAttribute) {
                    values.add(getMetadatum(field, ((OMAttribute) el).getAttributeValue()));
                } else if (el instanceof String) {
                    values.add(getMetadatum(field, (String) el));
                } else if (el instanceof OMText) {
                    values.add(metadataFieldMapping.toDCValue(field, ((OMText) el).getText()));
                } else {
                    System.err.println("node of type: " + el.getClass());
                }
            }
            return values;
        } catch (JaxenException e) {
            System.err.println(query);
            throw new RuntimeException(e);
        }
    }

    private MetadatumDTO getMetadatum(MetadataFieldConfig field, String value) {
        MetadatumDTO dcValue = new MetadatumDTO();
        if (field == null) {
            return null;
        }
        try {
            dcValue.setValue(dateFormatTo.format(dateFormatFrom.parse(value)));
        } catch (ParseException e) {
            dcValue.setValue(value);
        }
        dcValue.setElement(field.getElement());
        dcValue.setQualifier(field.getQualifier());
        dcValue.setSchema(field.getSchema());
        return dcValue;
    }
}

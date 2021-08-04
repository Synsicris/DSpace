/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;
import org.dspace.app.easyliveimport.EasyOnlineImport;
import org.dspace.app.rest.model.EasyOnlineImportRest;
import org.dspace.app.rest.projection.Projection;
import org.springframework.stereotype.Component;

/**
 * This is the converter from/to the EasyOnlineImport in the DSpace API data model
 * and the REST data model
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
@Component
public class EasyOnlineImportRestConverter implements DSpaceConverter<EasyOnlineImport, EasyOnlineImportRest> {

    @Override
    public EasyOnlineImportRest convert(EasyOnlineImport modelObject, Projection projection) {
        EasyOnlineImportRest rest = new EasyOnlineImportRest();
        rest.setId(modelObject.getId());
        rest.setCreated(modelObject.getCreated());
        rest.setModified(modelObject.getModified());
        rest.setType(EasyOnlineImportRest.TYPE);
        return rest;
    }

    @Override
    public Class<EasyOnlineImport> getModelClass() {
        return EasyOnlineImport.class;
    }

}
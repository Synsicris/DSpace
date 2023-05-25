/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.scripts.patents.service;

import static org.dspace.content.Item.ANY;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.content.dto.MetadataValueDTO;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.external.model.ExternalDataObject;
import org.dspace.external.provider.impl.LiveImportDataProvider;
import org.dspace.importer.external.metadatamapping.MetadataFieldConfig;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.com)
 */
public class UpdatePatentServiceImpl implements UpdatePatentService {

    private final static Logger log = LogManager.getLogger();

    @Autowired
    private ItemService itemService;

    @Override
    public boolean updatePatent(Context context, Item item, LiveImportDataProvider liveImportDataProvider)
            throws SQLException {
        String patentNo = itemService.getMetadataFirstValue(item, "dc", "identifier", "patentno", ANY);
        if (StringUtils.isBlank(patentNo)) {
            return false;
        }
        Optional<ExternalDataObject> externalPatent = liveImportDataProvider.getExternalDataObject(patentNo);
        if (externalPatent.isEmpty()) {
            //TODO: manage case is the Patent was deleted!
            return false;
        }

        List<MetadataFieldConfig> supportedMetadataFields = liveImportDataProvider.getQuerySource()
                                                                                 .getSupportedMetadataFields();
        return isMoreUpToDateThanCurrentPatent(context, item, externalPatent.get()) ?
                 updateCurrentPatentWithNewOne(context, item, supportedMetadataFields, externalPatent.get()) : false;
    }

    private boolean isMoreUpToDateThanCurrentPatent(Context context, Item currentPatent,
            ExternalDataObject externalPatent) {
        var publishedDateOfCurrentPatent = itemService.getMetadataFirstValue(currentPatent, "dc", "date", "issued",ANY);
        LocalDate publicationDateOfLocalPatent = LocalDate.parse(publishedDateOfCurrentPatent);
        LocalDate publicationDateOfExternalPatent = getPublicationDateOfExternalPatent(externalPatent);
        return publicationDateOfExternalPatent.isAfter(publicationDateOfLocalPatent);
    }

    private LocalDate getPublicationDateOfExternalPatent(ExternalDataObject externalPatent) {
        Optional<MetadataValueDTO> publicationDateOfExternalPatent = externalPatent.getMetadata()
                                                 .stream()
                                                 .filter(mv -> StringUtils.equals(mv.getSchema(), "dc") &&
                                                               StringUtils.equals(mv.getElement(), "date") &&
                                                               StringUtils.equals(mv.getQualifier(), "issued"))
                                                 .findFirst();
        return publicationDateOfExternalPatent.isPresent() ?
               LocalDate.parse(publicationDateOfExternalPatent.get().getValue()) : LocalDate.MIN;
    }

    private boolean updateCurrentPatentWithNewOne(Context context, Item localPatent,
            List<MetadataFieldConfig> supportedMetadataFields, ExternalDataObject externalPatent) {
        try {
            localPatent = clearMetadataOfLocalPatent(context, localPatent, supportedMetadataFields);
            for (MetadataValueDTO mv : externalPatent.getMetadata()) {
                itemService.addMetadata(context, localPatent, mv.getSchema(), mv.getElement(), mv.getQualifier(), null,
                                        mv.getValue());
            }
        } catch (SQLException | AuthorizeException e) {
            log.error("The Patent with uuid " + localPatent.getID() + " was not updated by the fallowing cause:"
                                              + e.getCause(), e.getMessage());
            return false;
        }
        return true;
    }

    private Item clearMetadataOfLocalPatent(Context context, Item localPatent,
            List<MetadataFieldConfig> supportedMetadataFields) throws SQLException, AuthorizeException {
        for (MetadataFieldConfig mfc : supportedMetadataFields) {
            itemService.clearMetadata(context, localPatent, mfc.getSchema(), mfc.getElement(), mfc.getQualifier(),null);
        }
        itemService.update(context, localPatent);
        return context.reloadEntity(localPatent);
    }

}
/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.repository;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.easyliveimport.EasyOnlineImport;
import org.dspace.app.rest.exception.RepositoryMethodNotImplementedException;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.model.EasyOnlineImportRest;
import org.dspace.app.rest.utils.Utils;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.EasyonlineimportServiceImpl;
import org.dspace.content.Item;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.service.CollectionService;
import org.dspace.content.service.InstallItemService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.WorkspaceItemService;
import org.dspace.core.Context;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.DiscoverResultIterator;
import org.dspace.discovery.SearchServiceException;
import org.dspace.discovery.indexobject.IndexableItem;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
@Component(EasyOnlineImportRest.CATEGORY + "." + EasyOnlineImportRest.NAME)
public class EasyonlineimportRestRepository extends DSpaceRestRepository<EasyOnlineImportRest, UUID> {

    @Autowired
    private ItemService itemService;

    @Autowired
    private InstallItemService installItemService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private EasyonlineimportServiceImpl easyOnlineImportService;

    @Autowired
    private ProjectConsumerService projectConsumerService;

    @Autowired
    private WorkspaceItemService workspaceItemService;

    @Override
    public Page<EasyOnlineImportRest> findAll(Context context, Pageable pageable) {
        throw new RepositoryMethodNotImplementedException(EasyOnlineImportRest.NAME, "findAll");
    }

    @Override
    public EasyOnlineImportRest findOne(Context context, UUID id) {
        throw new RepositoryMethodNotImplementedException(EasyOnlineImportRest.NAME, "findOne");
    }

    @Override
    public EasyOnlineImportRest upload(HttpServletRequest request,String apiCategory, String model,
            UUID id, MultipartFile multipartFile)
            throws SQLException, IOException, AuthorizeException {
        EasyOnlineImport easyOnlineImportDTO = new EasyOnlineImport();
        easyOnlineImportDTO.setId(id);
        List<UUID> modified = new ArrayList<UUID>();

        Context context = obtainContext();
        Item item = itemService.find(context, id);
        if (Objects.isNull(item)) {
            throw new UnprocessableEntityException("");
        }
        String entityType = itemService.getMetadataFirstValue(item, "dspace", "entity", "type", Item.ANY);
        if (!StringUtils.equals("Project", entityType)) {
            throw new UnprocessableEntityException("");
        }
        Document document = null;
        try {
            document = Utils.extractDocument(multipartFile);
        } catch (ParserConfigurationException | SAXException e) {
            //TODO
        }

        // import Project item
        easyOnlineImportService.importFile(context, item, document, entityType);
        modified.add(item.getID());

        // import project partner item
        Collection projectPartnerCollection = getProjectPartnerCollection(context, item);
        Item projectPartnerItem = getProjectPartnerItem(context, projectPartnerCollection);
        if (Objects.nonNull(projectPartnerItem)) {
            easyOnlineImportService.importFile(context, projectPartnerItem, document, "projectpartner");
            modified.add(projectPartnerItem.getID());
        } else {
            WorkspaceItem workspaceItem = workspaceItemService.create(context, projectPartnerCollection, true);
            Item newItem = installItemService.installItem(context, workspaceItem);
            easyOnlineImportService.importFile(context, newItem, document, "projectpartner");
            collectionService.addItem(context, projectPartnerCollection, newItem);
            newItem = context.reloadEntity(newItem);
            easyOnlineImportDTO.setCreated(Collections.singletonList(newItem.getID()));
        }
        easyOnlineImportDTO.setModified(modified);
        return converter.toRest(easyOnlineImportDTO, utils.obtainProjection());
    }

    private Item getProjectPartnerItem(Context context, Collection collection) throws SQLException {
        Iterator<Item> items = null;
        try {
            items = findItems(context, collection);
        } catch (SearchServiceException e) {
            e.printStackTrace();
        }
        if (items.hasNext()) {
            return items.next();
        }
        return null;
    }

    private Collection getProjectPartnerCollection(Context context, Item item) throws SQLException {
        Community parentCommunity = projectConsumerService.getParentCommunityByProjectItem(context, item);
        if (Objects.nonNull(parentCommunity)) {
            return collectionService.retriveCollectionByEntityType(context, parentCommunity, "projectpartner");
        }
        return null;
    }

    private Iterator<Item> findItems(Context context, Collection collection)
            throws SQLException, SearchServiceException {
        DiscoverQuery discoverQuery = new DiscoverQuery();
        discoverQuery.setDSpaceObjectFilter(IndexableItem.TYPE);
        discoverQuery.addFilterQueries("dspace.entity.type:projectpartner");
        discoverQuery.addFilterQueries("synsicris.type.easy-import:yes");
        discoverQuery.addFilterQueries("location.coll:" + collection.getID().toString());
        return new DiscoverResultIterator<Item, UUID>(context, discoverQuery);
    }

    @Override
    public Class<EasyOnlineImportRest> getDomainClass() {
        return EasyOnlineImportRest.class;
    }

}
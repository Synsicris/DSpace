/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.security;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.dspace.app.rest.repository.EditItemRestRepository.OPERATION_PATH_SECTIONS;
import static org.dspace.project.util.ProjectConstants.MD_UNIQUE_ID;
import static org.dspace.project.util.ProjectConstants.PROJECT_ENTITY;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.dspace.app.rest.model.patch.Operation;
import org.dspace.app.rest.model.patch.Patch;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Item;
import org.dspace.content.edit.EditItem;
import org.dspace.content.edit.service.EditItemService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.services.ConfigurationService;
import org.dspace.services.RequestService;
import org.dspace.services.model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class EditProjectItemRestPermissionEvaluatorPlugin extends RestObjectPermissionEvaluatorPlugin {

    private static final Logger log = LoggerFactory.getLogger(EditProjectItemRestPermissionEvaluatorPlugin.class);

    @Autowired
    private RequestService requestService;

    @Autowired
    private EditItemService editItemService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public boolean hasDSpacePermission(
        Authentication authentication, Serializable targetId, String targetType, DSpaceRestPermission permission
    ) {

        return false;
    }

    @Override
    public boolean hasPatchPermission(
        Authentication authentication, Serializable targetId, String targetType, Patch patch
    ) {

        boolean hasPermission = super.hasPatchPermission(authentication, targetId, targetType, patch);

        if (!hasPermission) {
            return false;
        }

        AtomicReference<Request> requestRef = new AtomicReference<>();
        AtomicReference<Context> contextRef = new AtomicReference<>();
        AtomicReference<Item> itemRef = new AtomicReference<>();

        String uuidWithMode = targetId.toString();
        initializeAtomicReferences(uuidWithMode, requestRef, contextRef, itemRef);

        String modeName = null;
        String[] values = uuidWithMode.split(":");
        if (values != null && values.length > 0) {
            modeName = values[1];
        } else {
            return false;
        }

        Context context = contextRef.get();
        Item item = itemRef.get();
        EditItem source = null;
        try {
            source = editItemService.find(context, item, modeName);
        } catch (SQLException | AuthorizeException e) {
            log.error(
                "Error while retrieving the edit-item permissions for item {} with mode {}: {}", item.getID(), modeName,
                e.getMessage()
            );
        } finally {
            if (source == null || source.getMode() == null) {
                return false;
            }
        }

        boolean isAdmin = false;
        try {
            isAdmin = authorizeService.isAdmin(context);
        } catch (SQLException e) {
            log.error(
                "Error while checking admin permissions: {}", e.getMessage()
            );
        }

        Optional<Boolean> isAuthorized = Optional.ofNullable(isAdmin)
            .filter(Boolean::valueOf)
            .or(() -> Optional.ofNullable(isNotVersionItem(item)));

        if (isAuthorized.get()) {
            return true;
        }

        boolean isProject = itemService.getEntityType(item).equals(PROJECT_ENTITY);
        if (!isProject) {
            return false;
        }

        List<Operation> operations = patch.getOperations();
        String[] metadatas = null;
        if (operations != null && !operations.isEmpty()) {
            metadatas = configurationService.getArrayProperty("project.version.metadata-to-patch");
        }
        for (Operation op : operations) {
            // the value in the position 0 is a null value
            String[] path = op.getPath().substring(1).split("/", 3);
            String sections = path[0];
            if (OPERATION_PATH_SECTIONS.equals(sections) && path.length > 2 ) {
                String patchedMetadata =
                    Optional.ofNullable(path[2])
                        .filter(m -> m.contains("/"))
                        .map(m -> m.split("/")[0])
                        .orElse(path[2]);
                if (!isMetadataAllowed(metadatas, patchedMetadata)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isMetadataAllowed(String[] metadatas, String metadataField) {
        return Arrays.stream(metadatas).anyMatch(metadataField::equals);
    }

    private boolean isNotVersionItem(Item item) {
        return isEmpty(
            itemService.getMetadataFirstValue(
                item, MD_UNIQUE_ID.schema, MD_UNIQUE_ID.element, MD_UNIQUE_ID.qualifier, Item.ANY
            )
        );
    }

    private void initializeAtomicReferences(
        String uuid,
        AtomicReference<Request> request,
        AtomicReference<Context> context,
        AtomicReference<Item> item
    ) {
        request.set(requestService.getCurrentRequest());
        context.set(ContextUtil.obtainContext(request.get().getHttpServletRequest()));

        try {
            if (uuid.contains(":")) {
                uuid = uuid.split(":")[0];
            }
            item.set(itemService.find(context.get(), UUID.fromString(uuid)));
        } catch (SQLException e) {
            log.error("Error while retrieving the item ({}): {}", uuid, e.getMessage());
        }
    }

}

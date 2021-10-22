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
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.Parameter;
import org.dspace.app.rest.SearchRestMethod;
import org.dspace.app.rest.exception.DSpaceBadRequestException;
import org.dspace.app.rest.exception.RepositoryMethodNotImplementedException;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.model.RegistrationRest;
import org.dspace.app.util.AuthorizeUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.RegistrationData;
import org.dspace.eperson.service.AccountService;
import org.dspace.eperson.service.EPersonService;
import org.dspace.eperson.service.GroupService;
import org.dspace.eperson.service.RegistrationDataService;
import org.dspace.services.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

/**
 * This is the repository that is responsible for managing Registration Rest objects
 */
@Component(RegistrationRest.CATEGORY + "." + RegistrationRest.NAME)
public class RegistrationRestRepository extends DSpaceRestRepository<RegistrationRest, Integer> {

    private static Logger log = org.apache.logging.log4j.LogManager.getLogger(RegistrationRestRepository.class);

    @Autowired
    private EPersonService ePersonService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private RegistrationDataService registrationDataService;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private GroupService groupService;

    @Override
    public RegistrationRest findOne(Context context, Integer integer) {
        throw new RepositoryMethodNotImplementedException("No implementation found; Method not allowed!", "");
    }

    @Override
    public Page<RegistrationRest> findAll(Context context, Pageable pageable) {
        throw new RepositoryMethodNotImplementedException("No implementation found; Method not allowed!", "");
    }

    @Override
    public RegistrationRest createAndReturn(Context context) {
        HttpServletRequest request = requestService.getCurrentRequest().getHttpServletRequest();
        ObjectMapper mapper = new ObjectMapper();
        RegistrationRest registrationRest;
        try {
            ServletInputStream input = request.getInputStream();
            registrationRest = mapper.readValue(input, RegistrationRest.class);
        } catch (IOException e1) {
            throw new UnprocessableEntityException("Error parsing request body.", e1);
        }
        if (StringUtils.isBlank(registrationRest.getEmail())) {
            throw new UnprocessableEntityException("The email cannot be omitted from the Registration endpoint");
        }
        if (Objects.nonNull(registrationRest.getGroups())) {
            try {
                if (Objects.isNull(context.getCurrentUser())
                    || (!authorizeService.isAdmin(context)
                        & !hasPermission(context, registrationRest.getGroups()))) {
                    throw new AccessDeniedException("Only admin users can invite new users to join groups");
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
        EPerson eperson = null;
        try {
            eperson = ePersonService.findByEmail(context, registrationRest.getEmail());
        } catch (SQLException e) {
            log.error("Something went wrong retrieving EPerson for email: " + registrationRest.getEmail(), e);
        }
        if (eperson != null) {
            try {
                if (!AuthorizeUtil.authorizeUpdatePassword(context, eperson.getEmail())) {
                    throw new DSpaceBadRequestException(
                            "Password cannot be updated for the given EPerson with email: " + eperson.getEmail());
                }
                accountService.sendForgotPasswordInfo(context, registrationRest.getEmail(),
                        registrationRest.getGroups());
            } catch (SQLException | IOException | MessagingException | AuthorizeException e) {
                log.error("Something went wrong with sending forgot password info for email: "
                        + registrationRest.getEmail(), e);
            }
        } else {
            try {
                if (!AuthorizeUtil.authorizeNewAccountRegistration(context, request)) {
                    throw new AccessDeniedException(
                            "Registration is disabled, you are not authorized to create a new Authorization");
                }
                accountService.sendRegistrationInfo(context, registrationRest.getEmail(), registrationRest.getGroups());
            } catch (SQLException | IOException | MessagingException | AuthorizeException e) {
                log.error("Something with wrong with sending registration info for email: "
                        + registrationRest.getEmail());
            }
        }
        return null;
    }

    private boolean hasPermission(Context context, List<UUID> groups) throws SQLException {
        for (UUID groupUuid : groups) {
            Group group = groupService.find(context, groupUuid);
            if (Objects.nonNull(group)) {
                DSpaceObject obj = groupService.getParentObject(context, group);
                if (!authorizeService.isAdmin(context, obj)) {
                    return false;
                }
            } else {
                throw new UnprocessableEntityException("Group uuid " + groupUuid.toString() + " not valid!");
            }
        }
        return true;
    }

    @Override
    public Class<RegistrationRest> getDomainClass() {
        return RegistrationRest.class;
    }

    /**
     * This method will find the RegistrationRest object that is associated with the token given
     * @param token The token to be found and for which a RegistrationRest object will be found
     * @return      A RegistrationRest object for the given token
     * @throws SQLException If something goes wrong
     * @throws AuthorizeException If something goes wrong
     */
    @SearchRestMethod(name = "findByToken")
    public RegistrationRest findByToken(@Parameter(value = "token", required = true) String token)
        throws SQLException, AuthorizeException {
        Context context = obtainContext();
        RegistrationData registrationData = registrationDataService.findByToken(context, token);
        if (registrationData == null) {
            throw new ResourceNotFoundException("The token: " + token + " couldn't be found");
        }
        RegistrationRest registrationRest = new RegistrationRest();
        registrationRest.setEmail(registrationData.getEmail());
        EPerson ePerson = accountService.getEPerson(context, token);
        if (ePerson != null) {
            registrationRest.setUser(ePerson.getID());
        }
        List<String> groupNames = registrationData.getGroups()
                .stream().map(Group::getName).collect(Collectors.toList());
        registrationRest.setGroupNames(groupNames);
        registrationRest.setGroups(registrationData
                .getGroups().stream().map(Group::getID).collect(Collectors.toList()));
        return registrationRest;
    }
}

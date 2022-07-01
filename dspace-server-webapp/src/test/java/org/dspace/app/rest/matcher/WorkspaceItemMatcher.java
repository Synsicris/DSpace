/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.matcher;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static org.dspace.app.rest.matcher.HalMatcher.matchEmbeds;
import static org.dspace.app.rest.matcher.MetadataMatcher.matchMetadata;
import static org.dspace.app.rest.test.AbstractControllerIntegrationTest.REST_SERVER_URL;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import org.dspace.content.WorkspaceItem;
import org.dspace.eperson.Group;
import org.hamcrest.Matcher;

/**
 * Utility class to construct a Matcher for a Workspace item
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */
public class WorkspaceItemMatcher {

    private WorkspaceItemMatcher() { }

    /**
     * Check if the returned json expose all the required links and properties and the title and dateIssued are present
     * in the traditionalpageone section as by the default configuration (form-submission.xml)
     * 
     * @param witem
     *            the workspaceitem, if null only the presence of the generic properties will be verified
     * @param title
     *            the dc.title
     * @param dateIssued
     *            the dc.date.issued
     * @return
     */
    public static Matcher matchItemWithTitleAndDateIssued(WorkspaceItem witem, String title,
            String dateIssued) {
        return allOf(
                // Check workspaceitem properties
                matchProperties(witem),
                // Check core metadata all appear in the first describe panel "traditionalpageone"
                hasJsonPath("$.sections.traditionalpageone['dc.title'][0].value", is(title)),
                hasJsonPath("$.sections.traditionalpageone['dc.date.issued'][0].value", is(dateIssued)),
                // Check links
                matchLinks(witem));
    }

    /**
     * Check if the returned json expose all the required links and properties and the title and dateIssued are present
     * in the traditionalpageone section and the subject in the traditionalpagetwo section as by the default
     * configuration (form-submission.xml)
     * 
     * @param witem
     *            the workspaceitem, if null only the presence of the generic properties will be verified
     * @param title
     *            the dc.title
     * @param dateIssued
     *            the dc.date.issued * @param subject the dc.subject
     * 
     * @return
     */
    public static Matcher matchItemWithTitleAndDateIssuedAndSubject(WorkspaceItem witem, String title,
            String dateIssued,
            String subject) {
        return allOf(
                // Check workspaceitem properties
                matchProperties(witem),
                // Check core metadata all appear in the first describe panel "traditionalpageone"
                title != null ?
                        hasJsonPath("$.sections.traditionalpageone['dc.title'][0].value", is(title)) :
                        hasNoJsonPath("$.sections.traditionalpageone['dc.title']"),
                hasJsonPath("$.sections.traditionalpageone['dc.date.issued'][0].value", is(dateIssued)),
                // Check keywords they appear in the second describe panel "traditionalpagetwo"
                hasJsonPath("$.sections.traditionalpagetwo['dc.subject'][0].value", is(subject)),
                // Check links
                matchLinks(witem));
    }

    /**
     * Check that the id and type are exposed
     * 
     * @param witem
     *            the workspaceitem, if null only the presence of the generic properties will be verified
     * @return
     */
    public static Matcher<? super Object> matchProperties(WorkspaceItem witem) {
        if (witem != null) {
            return allOf(
                    hasJsonPath("$.id", is(witem.getID())),
                    hasJsonPath("$.type", is("workspaceitem"))
            );
        } else {
            return allOf(
                    hasJsonPath("$.id"),
                    hasJsonPath("$.type", is("workspaceitem"))
            );
        }
    }

    /**
     * Check that the required links are present
     * 
     * @param witem
     *            the workspaceitem
     * @return
     */
    public static Matcher<? super Object> matchLinks(WorkspaceItem witem) {
        if (witem != null) {
            return allOf(
                    hasJsonPath("$._links.self.href",
                            is(REST_SERVER_URL + "submission/workspaceitems/" + witem.getID())),
                    hasJsonPath("$._links.item.href", startsWith(REST_SERVER_URL)),
                    hasJsonPath("$._links.collection.href", startsWith(REST_SERVER_URL)),
                    hasJsonPath("$._links.submitter.href", startsWith(REST_SERVER_URL)),
                    hasJsonPath("$._links.submissionDefinition.href", startsWith(REST_SERVER_URL)));
        } else {
            return allOf(
                    hasJsonPath("$._links.self.href"),
                    hasJsonPath("$._links.item.href", startsWith(REST_SERVER_URL)),
                    hasJsonPath("$._links.collection.href", startsWith(REST_SERVER_URL)),
                    hasJsonPath("$._links.submitter.href", startsWith(REST_SERVER_URL)),
                    hasJsonPath("$._links.submissionDefinition.href", startsWith(REST_SERVER_URL)));
        }
    }

    /**
     * Gets a matcher for all expected embeds when the full projection is requested.
     */
    public static Matcher<? super Object> matchFullEmbeds() {
        return matchEmbeds(
                "collection",
                "item",
                "submitter",
                "submissionDefinition"
        );
    }

    public static Matcher<? super Object> matchPolicyGroupAndSharedMetadata(
                                  String shared, Group policyGroup) {
        return allOf(hasJsonPath("$._embedded.item.metadata",
               allOf(
                     matchMetadata("cris.project.shared", shared),
                     matchMetadata("cris.policy.group", policyGroup.getName(), policyGroup.getID().toString(), 0))));
    }
}

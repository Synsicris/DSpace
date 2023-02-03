/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.administer;

import static org.dspace.content.Item.ANY;
import static org.dspace.content.MetadataSchemaEnum.CRIS;
import static org.dspace.content.service.DSpaceObjectService.MD_COPYRIGHT_TEXT;
import static org.dspace.content.service.DSpaceObjectService.MD_INTRODUCTORY_TEXT;
import static org.dspace.content.service.DSpaceObjectService.MD_LICENSE;
import static org.dspace.content.service.DSpaceObjectService.MD_NAME;
import static org.dspace.content.service.DSpaceObjectService.MD_PROVENANCE_DESCRIPTION;
import static org.dspace.content.service.DSpaceObjectService.MD_SHORT_DESCRIPTION;
import static org.dspace.content.service.DSpaceObjectService.MD_SIDEBAR_TEXT;
import static org.dspace.project.util.ProjectConstants.MD_RELATION_ITEM_ENTITY;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.MetadataFieldName;
import org.dspace.content.MetadataSchemaEnum;
import org.dspace.content.MetadataValue;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.authority.Choices;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.CollectionService;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.InstallItemService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.WorkspaceItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.core.CrisConstants;
import org.dspace.eperson.Group;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.EPersonService;
import org.dspace.eperson.service.GroupService;
import org.dspace.handle.factory.HandleServiceFactory;
import org.dspace.handle.service.HandleService;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class deals with importing community and collection structures from
 * an XML file.
 *
 * The XML file structure needs to be:
 * <pre>{@code
 * <import_structure>
 *   <community>
 *     <name>....</name>
 *     <community>...</community>
 *     <collection>
 *       <name>....</name>
 *     </collection>
 *   </community>
 * </import_structure>
 * }</pre>
 *
 * <p>
 * It can be arbitrarily deep, and supports all the metadata elements
 * that make up the community and collection metadata.  See the system
 * documentation for more details.
 *
 * @author Richard Jones
 */

public class StructBuilder {
    /** Name of the root element for the document to be imported. */
    static final String INPUT_ROOT = "import_structure";

    /** Name of the root element for the document to be imported. */
    static final String COMMUNITIES_ROOT = "communities";

    /*
     * Name of the root element for the document produced by importing.
     * Community and collection elements are annotated with their identifiers.
     */
    static final String RESULT_ROOT = "imported_structure";

    /**
     * A table to hold metadata for the collection being worked on.
     */
    private static final Map<String, MetadataFieldName> collectionMap = new HashMap<>();

    /**
     * A table to hold metadata for the community being worked on.
     */
    private static final Map<String, MetadataFieldName> communityMap = new HashMap<>();

    protected static final CommunityService communityService
            = ContentServiceFactory.getInstance().getCommunityService();
    protected static final CollectionService collectionService
            = ContentServiceFactory.getInstance().getCollectionService();
    protected static final EPersonService ePersonService
            = EPersonServiceFactory.getInstance().getEPersonService();
    protected static final HandleService handleService
            = HandleServiceFactory.getInstance().getHandleService();
    protected static GroupService groupService
            = EPersonServiceFactory.getInstance().getGroupService();
    protected static AuthorizeService authorizeService
            = AuthorizeServiceFactory.getInstance().getAuthorizeService();
    protected static ItemService itemService
            = ContentServiceFactory.getInstance().getItemService();
    protected static WorkspaceItemService workspaceItemService
            = ContentServiceFactory.getInstance().getWorkspaceItemService();
    protected static InstallItemService installItemService
            = ContentServiceFactory.getInstance().getInstallItemService();
    public static final MetadataFieldName MD_FUNDING_COMMUNITY
            = new MetadataFieldName("synsicris", "funding", "community");

    static String itemReferenceName = null;
    static String itemReferenceUUID = null;

    /**
     * Default constructor
     */
    private StructBuilder() { }

    /**
     * Main method to be run from the command line to import a structure into
     * DSpacee or export existing structure to a file.The command is of the form:
     *
     * <p>{@code StructBuilder -f [XML source] -e [administrator email] -o [output file]}
     *
     * <p>to import, or
     *
     * <p>{@code StructBuilder -x -e [administrator email] -o [output file]}</p>
     *
     * <p>to export.  The output will contain exactly the same as the source XML
     * document, but with the Handle for each imported item added as an attribute.
     *
     *
     * @param argv command line arguments.
     * @throws ParserConfigurationException passed through.
     * @throws SQLException passed through.
     * @throws FileNotFoundException if input or output could not be opened.
     * @throws TransformerException if the input document is invalid.
     * @throws XPathExpressionException passed through.
     */
    public static void main(String[] argv)
        throws ParserConfigurationException, SQLException,
        IOException, TransformerException, XPathExpressionException {
        // Define command line options.
        Options options = new Options();

        options.addOption("h", "help", false, "Print this help message.");
        options.addOption("?", "help");
        options.addOption("x", "export", false, "Export the current structure as XML.");
        options.addOption("k", "keep-handles", false, "Apply Handles from input document.");

        options.addOption(Option.builder("e").longOpt("eperson")
                .desc("User who is manipulating the repository's structure.")
                .hasArg().argName("eperson").required().build());

        options.addOption(Option.builder("f").longOpt("file")
                .desc("File of new structure information.")
                .hasArg().argName("input").build());

        options.addOption(Option.builder("o").longOpt("output")
                .desc("File to receive the structure map ('-' for standard out).")
                .hasArg().argName("output").required().build());

        // Parse the command line.
        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        try {
            line = parser.parse(options, argv);
        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
            usage(options);
            System.exit(1);
        }

        // If the user asked for help, give it and exit.
        if (line.hasOption('h') || line.hasOption('?')) {
            giveHelp(options);
            System.exit(0);
        }

        // Otherwise, analyze the command.
        // Must be import or export.
        if (!(line.hasOption('f') || line.hasOption('x'))) {
            giveHelp(options);
            System.exit(1);
        }

        // Open the output stream.
        String output = line.getOptionValue('o');
        OutputStream outputStream;
        if ("-".equals(output)) {
            outputStream = System.out;
        } else {
            outputStream = new FileOutputStream(output);
        }

        // create a context
        Context context = new Context();

        // set the context.
        String eperson = line.getOptionValue('e');
        try {
            context.setCurrentUser(ePersonService.findByEmail(context, eperson));
        } catch (SQLException ex) {
            System.err.format("That user could not be found:  %s%n", ex.getMessage());
            System.exit(1);
        }

        // Export? Import?
        if (line.hasOption('x')) { // export
            exportStructure(context, outputStream);
            outputStream.close();
        } else { // Must be import
            String input = line.getOptionValue('f');
            if (null == input) {
                usage(options);
                System.exit(1);
            }

            InputStream inputStream;
            if ("-".equals(input)) {
                inputStream = System.in;
            } else {
                inputStream = new FileInputStream(input);
            }

            boolean keepHandles = options.hasOption("k");
            importStructure(context, inputStream, outputStream, keepHandles);

            inputStream.close();
            outputStream.close();

            // save changes from import
            context.complete();
        }
        System.exit(0);
    }

    /**
     * Import new Community/Collection structure.
     *
     * @param context
     * @param input XML which describes the new communities and collections.
     * @param output input, annotated with the new objects' identifiers.
     * @param keepHandles true if Handles should be set from input.
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     * @throws SQLException
     */
    static void importStructure(Context context, InputStream input,
            OutputStream output, boolean keepHandles)
            throws IOException, ParserConfigurationException, SQLException,
            TransformerException, XPathExpressionException {

        // load the XML
        Document document = null;
        try {
            document = loadXML(input);
        } catch (IOException ex) {
            System.err.format("The input document could not be read:  %s%n", ex.getMessage());
            System.exit(1);
        } catch (SAXException ex) {
            System.err.format("The input document could not be parsed:  %s%n", ex.getMessage());
            System.exit(1);
        }

        // run the preliminary validation, to be sure that the the XML document
        // is properly structured.
        try {
            validate(document);
        } catch (XPathExpressionException ex) {
            System.err.format("The input document is invalid:  %s%n", ex.getMessage());
            System.exit(1);
        }

        // Check for 'identifier' attributes -- possibly output by this class.
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList identifierNodes = (NodeList) xPath.compile("//*[@identifier]")
                                                   .evaluate(document, XPathConstants.NODESET);
        if (identifierNodes.getLength() > 0) {
            if (!keepHandles) {
                System.err.println("The input document has 'identifier' attributes, which will be ignored.");
            } else {
                for (int i = 0; i < identifierNodes.getLength() ; i++) {
                    String identifier = identifierNodes.item(i).getAttributes().item(0).getTextContent();
                    if (handleService.resolveToURL(context, identifier) != null) {
                        System.err.printf("The input document contains handle %s,"
                                + " which is in use already. Aborting...%n",
                                identifier);
                        System.exit(1);
                    }
                }
            }
        }

        // load the mappings into the member variable hashmaps
        communityMap.put("name", MD_NAME);
        communityMap.put("description", MD_SHORT_DESCRIPTION);
        communityMap.put("intro", MD_INTRODUCTORY_TEXT);
        communityMap.put("copyright", MD_COPYRIGHT_TEXT);
        communityMap.put("sidebar", MD_SIDEBAR_TEXT);
        communityMap.put("funding-comm", MD_FUNDING_COMMUNITY);
        communityMap.put("policy-group", null);
        communityMap.put("has-item-reference", null);

        collectionMap.put("name", MD_NAME);
        collectionMap.put("entity-type", CrisConstants.MD_ENTITY_TYPE);
        collectionMap.put("submission-type", CrisConstants.MD_SUBMISSION_TYPE);
        collectionMap.put("workflow-name", CrisConstants.MD_WORKFLOW_NAME);
        collectionMap.put("shared-workspace", CrisConstants.MD_SHARED_WORKSPACE);
        collectionMap.put("description", MD_SHORT_DESCRIPTION);
        collectionMap.put("intro", MD_INTRODUCTORY_TEXT);
        collectionMap.put("copyright", MD_COPYRIGHT_TEXT);
        collectionMap.put("sidebar", MD_SIDEBAR_TEXT);
        collectionMap.put("license", MD_LICENSE);
        collectionMap.put("provenance", MD_PROVENANCE_DESCRIPTION);
        collectionMap.put("policy-group", null);
        collectionMap.put("item-template", null);
        collectionMap.put("item", null);

        try {
            // get the group list
            NodeList first = (NodeList) xPath.compile("/import_structure/groups/group")
                    .evaluate(document, XPathConstants.NODESET);

            // run the import for groups
            handleGroups(context, first);
        } catch (XPathExpressionException ex) {
            System.err.format("Input content not understood:  %s%n", ex.getMessage());
            System.exit(1);
        } catch (AuthorizeException ex) {
            System.err.format("Not authorized:  %s%n", ex.getMessage());
            System.exit(1);
        }

        Element[] elements = null;
        try {
            // get the top level community list
            NodeList first = (NodeList) xPath.compile("/import_structure/communities/community")
                                             .evaluate(document, XPathConstants.NODESET);

            // run the import starting with the top level communities
            elements = handleCommunities(context, first, null, keepHandles);
        } catch (TransformerException ex) {
            System.err.format("Input content not understood:  %s%n", ex.getMessage());
            System.exit(1);
        } catch (AuthorizeException ex) {
            System.err.format("Not authorized:  %s%n", ex.getMessage());
            System.exit(1);
        }

        // generate the output
        final Element root = new Element(RESULT_ROOT);

        for (Element element : elements) {
            root.addContent(element);
        }

        // finally write the string into the output file.
        final org.jdom2.Document xmlOutput = new org.jdom2.Document(root);
        try {
            new XMLOutputter().output(xmlOutput, output);
        } catch (IOException e) {
            System.out.printf("Unable to write to output file %s:  %s%n",
                    output, e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Add a single community, and its children, to the Document.
     *
     * @param community
     * @return a fragment representing this Community.
     */
    private static Element exportACommunity(Community community) {
        // Export this Community.
        Element element = new Element("community");
        element.setAttribute("identifier", community.getHandle());
        element.addContent(new Element("name").setText(community.getName()));
        element.addContent(new Element("description")
                .setText(communityService.getMetadataFirstValue(community,
                        MetadataSchemaEnum.DC.getName(), "description", "abstract", Item.ANY)));
        element.addContent(new Element("intro")
                .setText(communityService.getMetadataFirstValue(community,
                        MetadataSchemaEnum.DC.getName(), "description", null, Item.ANY)));
        element.addContent(new Element("copyright")
                .setText(communityService.getMetadataFirstValue(community,
                        MetadataSchemaEnum.DC.getName(), "rights", null, Item.ANY)));
        element.addContent(new Element("sidebar")
                .setText(communityService.getMetadataFirstValue(community,
                        MetadataSchemaEnum.DC.getName(), "description", "tableofcontents", Item.ANY)));

        // Export this Community's Community children.
        for (Community subCommunity : community.getSubcommunities()) {
            element.addContent(exportACommunity(subCommunity));
        }

        // Export this Community's Collection children.
        for (Collection collection : community.getCollections()) {
            element.addContent(exportACollection(collection));
        }

        return element;
    }

    /**
     * Add a single Collection to the Document.
     *
     * @param collection
     * @return a fragment representing this Collection.
     */
    private static Element exportACollection(Collection collection) {
        // Export this Collection.
        Element element = new Element("collection");
        element.setAttribute("identifier", collection.getHandle());
        element.addContent(new Element("name").setText(collection.getName()));
        element.addContent(new Element("description")
                .setText(collectionService.getMetadataFirstValue(collection,
                        MetadataSchemaEnum.DC.getName(), "description", "abstract", Item.ANY)));
        element.addContent(new Element("intro")
                .setText(collectionService.getMetadataFirstValue(collection,
                        MetadataSchemaEnum.DC.getName(), "description", null, Item.ANY)));
        element.addContent(new Element("copyright")
                .setText(collectionService.getMetadataFirstValue(collection,
                        MetadataSchemaEnum.DC.getName(), "rights", null, Item.ANY)));
        element.addContent(new Element("sidebar")
                .setText(collectionService.getMetadataFirstValue(collection,
                        MetadataSchemaEnum.DC.getName(), "description", "tableofcontents", Item.ANY)));
        element.addContent(new Element("license")
                .setText(collectionService.getMetadataFirstValue(collection,
                        MetadataSchemaEnum.DC.getName(), "rights", "license", Item.ANY)));
        // Provenance is special:  multivalued
        for (MetadataValue value : collectionService.getMetadata(collection,
                MetadataSchemaEnum.DC.getName(), "provenance", null, Item.ANY)) {
            element.addContent(new Element("provenance")
                    .setText(value.getValue()));
        }
        String entityType = collectionService.getMetadataFirstValue(collection, "dspace", "entity", "type", Item.ANY);
        if (StringUtils.isNotBlank(entityType)) {
            element.addContent(new Element("entity-type").setText(entityType));
        }

        String submissionDefinition = collectionService.getMetadataFirstValue(collection,
            MetadataSchemaEnum.CRIS.getName(), "submission", "definition", Item.ANY);
        if (StringUtils.isNotBlank(submissionDefinition)) {
            element.addContent(new Element("submission-type").setText(submissionDefinition));
        }

        String workflowName = collectionService.getMetadataFirstValue(collection, MetadataSchemaEnum.CRIS.getName(),
                "workflow", "name", Item.ANY);
        if (StringUtils.isNotBlank(workflowName)) {
            element.addContent(new Element("workflow-name").setText(workflowName));
        }

        String sharedWorkspace = collectionService.getMetadataFirstValue(collection, MetadataSchemaEnum.CRIS.getName(),
            "workspace", "shared", Item.ANY);
        if (StringUtils.isNotBlank(sharedWorkspace)) {
            element.addContent(new Element("shared-workspace").setText(sharedWorkspace));
        }

        return element;
    }

    /**
     * Write out the existing Community/Collection structure.
     */
    static void exportStructure(Context context, OutputStream output) {
        // Build a document from the Community/Collection hierarchy.
        Element rootElement = new Element(INPUT_ROOT);  // To be read by importStructure, perhaps

        List<Community> communities = null;
        try {
            communities = communityService.findAllTop(context);
        } catch (SQLException ex) {
            System.out.printf("Unable to get the list of top-level communities:  %s%n",
                    ex.getMessage());
            System.exit(1);
        }

        Element communitiesNode = new Element(COMMUNITIES_ROOT);
        rootElement.addContent(communitiesNode);

        for (Community community : communities) {
            communitiesNode.addContent(exportACommunity(community));
        }

        // Now write the structure out.
        org.jdom2.Document xmlOutput = new org.jdom2.Document(rootElement);
        try {
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(xmlOutput, output);
        } catch (IOException e) {
            System.out.printf("Unable to write to output file %s:  %s%n",
                    output, e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Output the usage information.
     */
    private static void usage(Options options) {
        HelpFormatter helper = new HelpFormatter();
        try (PrintWriter writer = new PrintWriter(System.out);) {
            helper.printUsage(writer, 80/* FIXME Magic */,
                    "structure-builder", options);
        }
    }

    /**
     * Help the user more.
     */
    private static void giveHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("struct-builder",
                "Import or export Community/Collection structure.",
                options,
                "When importing (-f), communities will be created from the "
                    + "top level, and a map of communities to handles will "
                    + "be returned in the output file.  When exporting (-x),"
                    + "the current structure will be written to the map file.",
                true);
    }

    /**
     * Validate the XML document.  This method returns if the document is valid.
     * If validation fails it generates an error and ceases execution.
     *
     * @param document the XML document object
     * @throws TransformerException if transformer error
     */
    private static void validate(org.w3c.dom.Document document)
        throws XPathExpressionException {
        StringBuilder err = new StringBuilder();
        boolean trip = false;

        err.append("The following errors were encountered parsing the source XML.\n");
        err.append("No changes have been made to the DSpace instance.\n\n");

        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList groupNodes = (NodeList) xPath.compile("/import_structure/groups/group")
                .evaluate(document, XPathConstants.NODESET);

        if (groupNodes != null && groupNodes.getLength() != 0) {
            String groupErrors = validateGroups(groupNodes);
            if (StringUtils.isNotBlank(groupErrors)) {
                err.append("-There are errors in the groups.\n");
                err.append(groupErrors);
                System.out.println(err.toString());
                System.exit(1);
            }
        }

        NodeList communityNodes = (NodeList) xPath.compile("/import_structure/communities/community")
                .evaluate(document, XPathConstants.NODESET);
        if (communityNodes.getLength() == 0) {
            err.append("-There are no top level communities in the source document.");
            System.out.println(err.toString());
            System.exit(1);
        }

        String errs = validateCommunities(communityNodes, 1);
        if (errs != null) {
            err.append(errs);
            trip = true;
        }

        if (trip) {
            System.out.println(err.toString());
            System.exit(1);
        }
    }

    /**
     * Takes the node list of groups and validate them by checking
     * their content that should be a simple not-empty string
     *
     * @param groupNodes the node list of groups
     * @return errors
     */
    private static final String validateGroups(NodeList groupNodes) {
        StringBuilder err = new StringBuilder();
        boolean erroneous = false;
        String errs = null;
        for (int i = 0; i < groupNodes.getLength(); i++) {
            String groupName = groupNodes.item(i).getTextContent();
            if (StringUtils.isBlank(groupName)) {
                String pos = Integer.toString(i + 1);
                err.append("-The group in position ").append(pos)
                   .append(" does not contain a valid string.\n");
                erroneous = true;
            }
        }

        if (erroneous) {
            errs = err.toString();
        }
        return errs;
    }

    /**
     * Validate the communities section of the XML document.  This returns a string
     * containing any errors encountered, or null if there were no errors.
     *
     * @param communities the NodeList of communities to validate
     * @param level       the level in the XML document that we are at, for the purposes
     *                    of error reporting
     * @return the errors that need to be generated by the calling method, or null if
     * no errors.
     */
    private static String validateCommunities(NodeList communities, int level)
        throws XPathExpressionException {
        StringBuilder err = new StringBuilder();
        boolean trip = false;
        String errs = null;
        XPath xPath = XPathFactory.newInstance().newXPath();

        for (int i = 0; i < communities.getLength(); i++) {
            Node n = communities.item(i);
            NodeList name = (NodeList) xPath.compile("name").evaluate(n, XPathConstants.NODESET);
            if (name.getLength() < 1) {
                String pos = Integer.toString(i + 1);
                err.append("-The level ").append(level)
                        .append(" community in position ").append(pos)
                        .append(" does not contain any name field.\n");
                trip = true;
            }

            // validate sub communities
            NodeList subCommunities = (NodeList) xPath.compile("community").evaluate(n, XPathConstants.NODESET);
            String comErrs = validateCommunities(subCommunities, level + 1);
            if (comErrs != null) {
                err.append(comErrs);
                trip = true;
            }

            // validate collections
            NodeList collections = (NodeList) xPath.compile("collection").evaluate(n, XPathConstants.NODESET);
            String colErrs = validateCollections(collections, level + 1);
            if (colErrs != null) {
                err.append(colErrs);
                trip = true;
            }
        }

        if (trip) {
            errs = err.toString();
        }

        return errs;
    }

    /**
     * validate the collection section of the XML document.  This generates a
     * string containing any errors encountered, or returns null if no errors.
     *
     * @param collections a NodeList of collections to validate
     * @param level       the level in the XML document for the purposes of error reporting
     * @return the errors to be generated by the calling method, or null if none
     */
    private static String validateCollections(NodeList collections, int level)
        throws XPathExpressionException {
        StringBuilder err = new StringBuilder();
        boolean trip = false;
        String errs = null;
        XPath xPath = XPathFactory.newInstance().newXPath();

        for (int i = 0; i < collections.getLength(); i++) {
            Node n = collections.item(i);
            NodeList name = (NodeList) xPath.compile("name").evaluate(n, XPathConstants.NODESET);
            if (name.getLength() < 1) {
                String pos = Integer.toString(i + 1);
                err.append("-The level ").append(level)
                        .append(" collection in position ").append(pos)
                        .append(" does not contain any name field.\n");
                trip = true;
            }
        }

        if (trip) {
            errs = err.toString();
        }

        return errs;
    }

    /**
     * Load the XML document from input.
     *
     * @param input the filename to load from.
     * @return the DOM representation of the XML input.
     */
    private static org.w3c.dom.Document loadXML(InputStream input)
        throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                                                        .newDocumentBuilder();

        org.w3c.dom.Document document = builder.parse(input);

        return document;
    }

    /**
     * Return the String value of a Node
     *
     * @param node the node from which we want to extract the string value
     * @return the string value of the node
     */
    private static String getStringValue(Node node) {
        String value = node.getNodeValue();

        if (node.hasChildNodes()) {
            Node first = node.getFirstChild();

            if (first.getNodeType() == Node.TEXT_NODE) {
                return first.getNodeValue().trim();
            }
        }

        return value;
    }

    /**
     * Return the String value of a Node's attribute
     *
     * @param node          the node from which we want to extract the attribute value
     * @param attributeName the attribute name to extract
     * @return the string value of the node's attribute
     */
    private static String getAttributeValue(Node node, String attributeName) {
        String value = null;
        Node attributeNode = node.getAttributes().getNamedItem(attributeName);

        if (attributeNode != null) {
            value = attributeNode.getNodeValue();
        }

        return value;
    }

    /**
     * Take a policy name and type and create a policy to the target DSpaceObject
     *
     * @param context         the context of the request
     * @param policyGroupName the policy group name to which associate the policy
     * @param policyType      the policy type to create
     * @param targetDSO       the DSpaceObject to which associate the policy
     * @param deleteAnonymous representing if to delete anonymous read policy from given targetDSO
     */
    private static void handleResourcePolicyGroup(Context context, String policyGroupName, String policyType,
            DSpaceObject targetDSO, boolean deleteAnonymous) throws SQLException, AuthorizeException {
        if (policyGroupName != null && policyType != null) {
            Group policyGroup = groupService.findByName(context, policyGroupName);
            if (policyGroup != null) {
                if (deleteAnonymous) {
                    // remove read permission from ANONYMOUS group
                    authorizeService.removeGroupPolicies(context, targetDSO,
                            groupService.findByName(context, Group.ANONYMOUS));
                }
                Integer rpType = getResourcePolicyTypeByName(policyType);
                if (rpType != null) {
                    // add given permission to group
                    authorizeService.addPolicy(context, targetDSO, rpType, policyGroup);
                }
            }
        }
    }

    /**
     * Create an item for a collection
     *
     * @param  context       the context of the request
     * @param  node          the Node element that contains information for creating
     *                       item template
     * @param  collection    the collection for which create the item
     * @throws SQLException, AuthorizeException, TransformerException
     */
    private static void handleItem(Context context, Node node, Collection collection)
        throws SQLException, AuthorizeException, XPathExpressionException {
        WorkspaceItem workspaceItem = workspaceItemService.create(context, collection, true);
        Item item = installItemService.installItem(context, workspaceItem);
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList metadataList = (NodeList) xPath.compile("metadata").evaluate(node, XPathConstants.NODESET);
        for (int i = 0; i < metadataList.getLength(); i++) {
            String metadataName = getAttributeValue(metadataList.item(i), "name");
            String metadatavalue = getStringValue(metadataList.item(i));
            String authority = getAttributeValue(metadataList.item(i), "authority");
            int confidence = StringUtils.isNotBlank(authority) ? 600 : -1;
            String[] elements = MetadataFieldName.parse(metadataName);
            itemService.addMetadata(context, item, elements[0], elements[1], elements[2], null, metadatavalue,
                authority, confidence);
        }

        NodeList itemReferenceList = (NodeList) xPath.compile("item-reference").evaluate(node, XPathConstants.NODESET);
        for (int i = 0; i < itemReferenceList.getLength(); i++) {
            String value = getStringValue(itemReferenceList.item(i));
            if (value.equals("true")) {
                itemReferenceName = item.getName();
                itemReferenceUUID = item.getID().toString();
            }
        }
    }

    /**
     * Create an item template for a collection
     *
     * @param context         the context of the request
     * @param node            the Node element that contains information for creating item template
     * @param collection      the collection for which create the item template
     * @throws SQLException, AuthorizeException, TransformerException
     */
    private static void handleItemTemplate(Context context, Node node, Collection collection)
            throws SQLException, AuthorizeException, XPathExpressionException {
        collectionService.createTemplateItem(context, collection);
        Item templateItem = collection.getTemplateItem();
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList metadataList = (NodeList) xPath.compile("metadata").evaluate(node, XPathConstants.NODESET);
        for (int i = 0; i < metadataList.getLength(); i++) {
            String metadataName = getAttributeValue(metadataList.item(i), "name");
            String metadatavalue = getStringValue(metadataList.item(i));
            String[] elements = MetadataFieldName.parse(metadataName);
            itemService.addMetadata(context, templateItem, elements[0], elements[1], elements[2], null, metadatavalue);
        }
    }

    /**
     * Return the int value for the resource policy by string value
     *
     * @param policyType the string value to convert
     * @return the int value for the resource policy
     */
    private static Integer getResourcePolicyTypeByName(String policyType) {
        Integer rpType = null;

        switch (policyType) {
            case "admin":
                rpType = Constants.ADMIN;
                break;
            case "read":
                rpType = Constants.READ;
                break;
            case "remove":
                rpType = Constants.REMOVE;
                break;
            case "item_read":
                rpType = Constants.DEFAULT_ITEM_READ;
                break;
            case "bitstream_read":
                rpType = Constants.DEFAULT_BITSTREAM_READ;
                break;
            case "add":
                rpType = Constants.ADD;
                break;
            case "delete":
                rpType = Constants.DELETE;
                break;
            default:
                break;
        }
        return rpType;
    }

    /**
     * Take a node list of groups and add all of them is they're not already
     * created in dspace
     *
     * @param context context of application
     * @param groups nodelist of groups to process
     * @throws SQLException
     * @throws AuthorizeException
     */
    private static final void handleGroups(Context context, NodeList groups) throws SQLException, AuthorizeException {
        for (int i = 0; i < groups.getLength(); i++) {
            String groupName = groups.item(i).getTextContent();
            Group savedGroup = groupService.findByName(context, groupName);
            if (savedGroup == null) {
                Group createdGroup = groupService.create(context);
                groupService.setName(createdGroup, groupName);
                groupService.update(context, createdGroup);
            }
        }
    }

    /**
     * Take a node list of communities and build the structure from them, delegating
     * to the relevant methods in this class for sub-communities and collections
     *
     * @param context     the context of the request
     * @param communities a nodelist of communities to create along with their sub-structures
     * @param parent      the parent community of the nodelist of communities to create
     * @param keepHandles use Handles from input.
     * @return an element array containing additional information regarding the
     * created communities (e.g. the handles they have been assigned)
     */
    private static Element[] handleCommunities(Context context, NodeList communities,
            Community parent, boolean keepHandles)
        throws TransformerException, SQLException, AuthorizeException,
            XPathExpressionException {
        Element[] elements = new Element[communities.getLength()];
        XPath xPath = XPathFactory.newInstance().newXPath();

        for (int i = 0; i < communities.getLength(); i++) {
            if (parent == null) {
                itemReferenceName = null;
                itemReferenceUUID = null;
            }
            Node tn = communities.item(i);
            Node identifier = tn.getAttributes().getNamedItem("identifier");

            // create the community or sub community
            Community community;
            if (null == identifier
                    || StringUtils.isBlank(identifier.getNodeValue())
                    || !keepHandles) {
                community = communityService.create(parent, context);
            } else {
                community = communityService.create(parent, context, identifier.getNodeValue());
            }

            // default the short description to be an empty string
            communityService.setMetadataSingleValue(context, community,
                    MD_SHORT_DESCRIPTION, null, " ");

            // now update the metadata
            String policyGroupName = null;
            String policyType = null;
            boolean toDelete = true;
            boolean hasItemReference = false;
            for (Map.Entry<String, MetadataFieldName> entry : communityMap.entrySet()) {
                NodeList nl = (NodeList) xPath.compile(entry.getKey()).evaluate(tn, XPathConstants.NODESET);
                if (nl.getLength() > 0) {
                    for (int j = 0; j < nl.getLength(); j++) {
                        if (entry.getKey().equals("policy-group")) {
                            policyType = getAttributeValue(nl.item(j), "rpType");
                            policyGroupName = getStringValue(nl.item(j));
                            handleResourcePolicyGroup(context, policyGroupName, policyType, community, toDelete);
                            toDelete = false;
                        } else if (entry.getKey().equals("has-item-reference")) {
                            hasItemReference = getStringValue(nl.item(j)).equals("true");
                        } else {
                            if (getStringValue(nl.item(j)) != null) {
                                communityService.addMetadata(context, community,
                                        entry.getValue().schema, entry.getValue().element, entry.getValue().qualifier,
                                        getAttributeValue(nl.item(j), "language"), getStringValue(nl.item(j)));
                            }
                        }
                    }
                }
            }

            // FIXME: at the moment, if the community already exists by name
            // then this will throw an SQLException on a duplicate key
            // violation.
            // Ideally we'd skip this row and continue to create sub communities
            // and so forth where they don't exist, but it's proving difficult
            // to isolate the community that already exists without hitting
            // the database directly.
            communityService.update(context, community);

            // build the element with the handle that identifies the new
            // community
            // along with all the information that we imported here
            // This looks like a lot of repetition of getting information
            // from above
            // but it's here to keep it separate from the create process in
            // case
            // we want to move it or make it switchable later
            Element element = new Element("community");
            element.setAttribute("identifier", community.getHandle());

            List<MetadataValue> nameList = communityService.getMetadataByMetadataString(community,
                    CommunityService.MD_NAME.toString());
            for (MetadataValue name : nameList) {
                Element nameElement = new Element("name");
                nameElement.setText(name.getValue());
                if (StringUtils.isNotBlank(name.getLanguage())) {
                    nameElement.setAttribute("language", name.getLanguage());
                }
                element.addContent(nameElement);
            }

            String fieldValue;

            fieldValue = communityService.getMetadataFirstValue(community,
                    CommunityService.MD_SHORT_DESCRIPTION, Item.ANY);
            if (fieldValue != null) {
                Element descriptionElement = new Element("description");
                descriptionElement.setText(fieldValue);
                element.addContent(descriptionElement);
            }

            fieldValue = communityService.getMetadataFirstValue(community,
                    CommunityService.MD_INTRODUCTORY_TEXT, Item.ANY);
            if (fieldValue != null) {
                Element introElement = new Element("intro");
                introElement.setText(fieldValue);
                element.addContent(introElement);
            }

            fieldValue = communityService.getMetadataFirstValue(community,
                    CommunityService.MD_COPYRIGHT_TEXT, Item.ANY);
            if (fieldValue != null) {
                Element copyrightElement = new Element("copyright");
                copyrightElement.setText(fieldValue);
                element.addContent(copyrightElement);
            }

            fieldValue = communityService.getMetadataFirstValue(community,
                    CommunityService.MD_SIDEBAR_TEXT, Item.ANY);
            if (fieldValue != null) {
                Element sidebarElement = new Element("sidebar");
                sidebarElement.setText(fieldValue);
                element.addContent(sidebarElement);
            }

            if (StringUtils.isNotBlank(policyGroupName)) {
                element.addContent(new Element("policy-group").setText(policyGroupName));
            }

            // handle sub communities
            NodeList subCommunities = (NodeList) xPath.compile("community")
                    .evaluate(tn, XPathConstants.NODESET);
            Element[] subCommunityElements = handleCommunities(context,
                    subCommunities, community, keepHandles);

            // handle collections
            NodeList collections = (NodeList) xPath.compile("collection")
                    .evaluate(tn, XPathConstants.NODESET);
            Element[] collectionElements = handleCollections(context,
                    collections, community, keepHandles);

            if (hasItemReference && StringUtils.isNotBlank(itemReferenceName)
                    && StringUtils.isNotBlank(itemReferenceUUID)) {

                communityService.addMetadata(
                    context, community, MD_RELATION_ITEM_ENTITY.schema,
                    MD_RELATION_ITEM_ENTITY.element,
                    MD_RELATION_ITEM_ENTITY.qualifier, null,
                    itemReferenceName, itemReferenceUUID, Choices.CF_ACCEPTED);
            }

            int j;
            for (j = 0; j < subCommunityElements.length; j++) {
                element.addContent(subCommunityElements[j]);
            }
            for (j = 0; j < collectionElements.length; j++) {
                element.addContent(collectionElements[j]);
            }

            elements[i] = element;
        }

        return elements;
    }

    /**
     * Take a node list of collections and create the structure from them
     *
     * @param context     the context of the request
     * @param collections the node list of collections to be created
     * @param parent      the parent community to whom the collections belong
     * @return an Element array containing additional information about the
     * created collections (e.g. the handle)
     */
    private static Element[] handleCollections(Context context,
            NodeList collections, Community parent, boolean keepHandles)
        throws SQLException, AuthorizeException, XPathExpressionException {
        Element[] elements = new Element[collections.getLength()];
        XPath xPath = XPathFactory.newInstance().newXPath();

        for (int i = 0; i < collections.getLength(); i++) {
            Node tn = collections.item(i);
            Node identifier = tn.getAttributes().getNamedItem("identifier");

            // Create the Collection.
            Collection collection;
            if (null == identifier
                    || StringUtils.isBlank(identifier.getNodeValue())
                    || !keepHandles) {
                collection = collectionService.create(context, parent);
            } else {
                collection = collectionService.create(context, parent, identifier.getNodeValue());
            }

            // default the short description to the empty string
            collectionService.setMetadataSingleValue(context, collection,
                    MD_SHORT_DESCRIPTION, Item.ANY, " ");

            // import the rest of the metadata
            String policyGroupName = null;
            String policyType = null;
            boolean toDelete = true;
            for (Map.Entry<String, MetadataFieldName> entry : collectionMap.entrySet()) {
                NodeList nl = (NodeList) xPath.compile(entry.getKey()).evaluate(tn, XPathConstants.NODESET);
                if (nl.getLength() > 0) {
                    for (int j = 0; j < nl.getLength(); j++) {
                        if (entry.getKey().equals("policy-group")) {
                            policyType = getAttributeValue(nl.item(j), "rpType");
                            policyGroupName = getStringValue(nl.item(j));
                            handleResourcePolicyGroup(context, policyGroupName, policyType, collection, toDelete);
                            toDelete = false;
                        } else if (entry.getKey().equals("item-template")) {
                            handleItemTemplate(context, nl.item(j), collection);
                        } else if (entry.getKey().equals("item")) {
                            handleItem(context, nl.item(j), collection);
                        } else {
                            if (getStringValue(nl.item(j)) != null) {
                                collectionService.addMetadata(context, collection,
                                    entry.getValue().schema, entry.getValue().element, entry.getValue().qualifier,
                                    getAttributeValue(nl.item(j), "language"), getStringValue(nl.item(j)));
                            }
                        }
                    }
                }
            }

            collectionService.update(context, collection);

            Element element = new Element("collection");
            element.setAttribute("identifier", collection.getHandle());

            List<MetadataValue> nameList = collectionService.getMetadataByMetadataString(collection,
                    CollectionService.MD_NAME.toString());
            for (MetadataValue name : nameList) {
                Element nameElement = new Element("name");
                nameElement.setText(name.getValue());
                if (StringUtils.isNotBlank(name.getLanguage())) {
                    nameElement.setAttribute("language", name.getLanguage());
                }
                element.addContent(nameElement);
            }

            String fieldValue;

            fieldValue = collectionService.getMetadataFirstValue(collection,
                    CollectionService.MD_SHORT_DESCRIPTION, Item.ANY);
            if (fieldValue != null) {
                Element descriptionElement = new Element("description");
                descriptionElement.setText(fieldValue);
                element.addContent(descriptionElement);
            }

            fieldValue = collectionService.getMetadataFirstValue(collection,
                    CollectionService.MD_INTRODUCTORY_TEXT, Item.ANY);
            if (fieldValue != null) {
                Element introElement = new Element("intro");
                introElement.setText(fieldValue);
                element.addContent(introElement);
            }

            fieldValue = collectionService.getMetadataFirstValue(collection,
                    CollectionService.MD_COPYRIGHT_TEXT, Item.ANY);
            if (fieldValue != null) {
                Element copyrightElement = new Element("copyright");
                copyrightElement.setText(fieldValue);
                element.addContent(copyrightElement);
            }

            fieldValue = collectionService.getMetadataFirstValue(collection,
                    CollectionService.MD_SIDEBAR_TEXT, Item.ANY);
            if (fieldValue != null) {
                Element sidebarElement = new Element("sidebar");
                sidebarElement.setText(fieldValue);
                element.addContent(sidebarElement);
            }

            fieldValue = collectionService.getMetadataFirstValue(collection,
                    CollectionService.MD_LICENSE, Item.ANY);
            if (fieldValue != null) {
                Element sidebarElement = new Element("license");
                sidebarElement.setText(fieldValue);
                element.addContent(sidebarElement);
            }

            fieldValue = collectionService.getMetadataFirstValue(collection,
                    CollectionService.MD_PROVENANCE_DESCRIPTION, Item.ANY);
            if (fieldValue != null) {
                Element sidebarElement = new Element("provenance");
                sidebarElement.setText(fieldValue);
                element.addContent(sidebarElement);
            }

            String entityType = collectionService.getMetadataFirstValue(collection, "dspace", "entity", "type", ANY);
            if (StringUtils.isNotBlank(entityType)) {
                element.addContent(new Element("entity-type").setText(entityType));
            }

            String submissionDefinition = collectionService.getMetadataFirstValue(collection,
                MetadataSchemaEnum.CRIS.getName(), "submission", "definition", Item.ANY);
            if (StringUtils.isNotBlank(submissionDefinition)) {
                element.addContent(new Element("submission-type").setText(submissionDefinition));
            }

            if (StringUtils.isNotBlank(policyGroupName)) {
                element.addContent(new Element("policy-group").setText(policyGroupName));
            }

            String workflowName = collectionService.getMetadataFirstValue(collection, MetadataSchemaEnum.CRIS.getName(),
                    "workflow", "name", Item.ANY);
            if (StringUtils.isNotBlank(workflowName)) {
                element.addContent(new Element("workflow-name").setText(workflowName));
            }

            String sharedWorkspace = collectionService.getMetadataFirstValue(collection, CRIS.getName(),
                "workspace", "shared", Item.ANY);
            if (StringUtils.isNotBlank(sharedWorkspace)) {
                element.addContent(new Element("shared-workspace").setText(sharedWorkspace));
            }

            elements[i] = element;
        }

        return elements;
    }
}

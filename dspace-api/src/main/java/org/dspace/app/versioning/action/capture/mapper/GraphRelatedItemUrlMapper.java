/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import static org.dspace.app.versioning.action.capture.mapper.ItemValueExtractors.uuidExtractor;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dspace.content.Item;
import org.dspace.core.Context;

/**
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class GraphRelatedItemUrlMapper extends SimpleParametersItemUrlMapper {

    private static final String SCOPE_KEY = "scope";
    private static final Entry<String, ItemValueExtractor<String>> SCOPE_PARAM = Map.entry(SCOPE_KEY, uuidExtractor);

    protected final Map<String, ItemValueExtractor<String>> dynamicParameters;

    public GraphRelatedItemUrlMapper(
        Map<String, String> parameters,
        AbstractItemUrlMapper itemUrlMapper
    ) {
        this(parameters, itemUrlMapper, Map.ofEntries(SCOPE_PARAM));
    }

    public GraphRelatedItemUrlMapper(
        Map<String, String> parameters,
        AbstractItemUrlMapper itemUrlMapper,
        Map<String, ItemValueExtractor<String>> dynamicParameters
    ) {
        super(parameters, itemUrlMapper);
        this.dynamicParameters = dynamicParameters;
    }

    @Override
    public String mapToUrl(Context context, Item item) {
        String url = itemUrlMapper.mapToUrl(context, item);
        try {
            return generateUrlWithParameters(
                url,
                generateParameters(item)
            );
        } catch (URISyntaxException e) {
            return url;
        }
    }

    protected List<NameValuePair> generateParameters(Item item) {
        return Stream.concat(
            this.generateParametersStream(parameters),
            this.computeParameters(dynamicParameters, item)
        ).collect(Collectors.toList());
    }

    private Stream<BasicNameValuePair> computeParameters(
        Map<String, ItemValueExtractor<String>> dynamicParameters,
        Item item
    ) {
        return dynamicParameters
            .entrySet()
            .stream()
            .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue().apply(item)));
    }

}

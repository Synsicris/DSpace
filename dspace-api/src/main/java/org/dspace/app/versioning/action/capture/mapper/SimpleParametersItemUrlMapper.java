/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.dspace.content.Item;
import org.dspace.core.Context;

/**
 * Mapper that adds some custom static parameters to a generated URL
 * using the choosen `itemUrlMapper` {@link AbstractItemUrlMapper}.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class SimpleParametersItemUrlMapper implements ItemUrlMapper {

    protected final Map<String, String> parameters;
    protected final AbstractItemUrlMapper itemUrlMapper;

    public SimpleParametersItemUrlMapper(
        Map<String, String> parameters,
        AbstractItemUrlMapper itemUrlMapper
    ) {
        super();
        this.parameters = parameters;
        this.itemUrlMapper = itemUrlMapper;
    }

    @Override
    public String mapToUrl(Context context, Item item) {
        String url = itemUrlMapper.mapToUrl(context, item);
        try {
            return generateUrlWithParameters(url, generateParameters(parameters));
        } catch (URISyntaxException e) {
            return url;
        }
    }

    protected String generateUrlWithParameters(
        String url, List<NameValuePair> parameters
    ) throws URISyntaxException {
        if (url == null) {
            return null;
        }
        return new URIBuilder(url)
            .addParameters(parameters)
            .build()
            .toString();
    }

    protected List<NameValuePair> generateParameters(Map<String, String> parameters) {
        return generateParametersStream(parameters).collect(Collectors.toList());
    }

    protected Stream<BasicNameValuePair> generateParametersStream(Map<String, String> parameters) {
        return parameters
            .entrySet()
            .stream()
            .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()));
    }

}

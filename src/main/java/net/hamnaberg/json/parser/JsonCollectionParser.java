package net.hamnaberg.json.parser;

import com.google.common.collect.ImmutableList;
import net.hamnaberg.json.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;

/**
 * Parser for a vnd.collection+json document.
 *
 * 
 */
public class JsonCollectionParser {
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonCollection parse(Reader reader) throws IOException {
        try {
            return parse(mapper.readTree(reader));
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private JsonCollection parse(JsonNode node) throws IOException {
        JsonNode collectionNode = node.get("collection");
        return parseCollection(collectionNode);
    }

    private JsonCollection parseCollection(JsonNode collectionNode) {
        URI href = createURI(collectionNode);
        Version version = getVersion(collectionNode);
        ErrorMessage error = parseError(collectionNode);

        if (error != null) {
            return new ErrorJsonCollection(href, version, error);
        }

        ImmutableList<Link> links = parseLinks(collectionNode);
        ImmutableList<Item> items = parseItems(collectionNode);

        return new DefaultJsonCollection(href, version, links, items, new Template());
    }

    private ErrorMessage parseError(JsonNode collectionNode) {
        JsonNode errorNode = collectionNode.get("error");
        if (errorNode != null) {
            String title = getStringValue(errorNode.get("title"));
            String code = getStringValue(errorNode.get("code"));
            String message = getStringValue(errorNode.get("message"));
            if (isEmpty(title) && isEmpty(code) && isEmpty(message)) {
                return ErrorMessage.EMPTY;
            }
            return new ErrorMessage(title, code, message);
        }
        return null;
    }

    private boolean isEmpty(String title) {
        return title == null || title.trim().isEmpty();
    }

    private String getStringValue(JsonNode errorNode) {
        return errorNode == null ? null : errorNode.getTextValue();
    }

    private ImmutableList<Item> parseItems(JsonNode collectionNode) {
        return ImmutableList.of();
    }

    private URI createURI(JsonNode node) {
        return URI.create(node.get("href").getTextValue());
    }

    private Version getVersion(JsonNode collectionNode) {
        JsonNode version = collectionNode.get("version");
        return Version.getVersion(version != null ? version.getTextValue() : null);
    }

    private ImmutableList<Link> parseLinks(JsonNode collectionNode) {
        JsonNode linkCollection = collectionNode.get("links");
        ImmutableList.Builder<Link> linkBuilder = ImmutableList.builder();
        if (linkCollection != null) {
            for (JsonNode linkNode : linkCollection) {
                JsonNode prompt = linkNode.get("prompt");
                Link link = new Link(
                        createURI(linkNode),
                        linkNode.get("rel").getTextValue(),
                        prompt != null ?  prompt.getTextValue() : null
                );
                linkBuilder.add(link);
            }
        }
        return linkBuilder.build();
    }
}
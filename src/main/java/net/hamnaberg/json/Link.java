/*
 * Copyright 2012 Erlend Hamnaberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.hamnaberg.json;

import net.hamnaberg.json.extension.Extended;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class Link extends Extended<Link> {
    Link(ObjectNode delegate) {
        super(delegate);
    }

    @Override
    protected Link copy(ObjectNode value) {
        return new Link(value);
    }

    public static Link create(URI href, String rel) {
        return create(href, rel, Optional.<String>empty(), Optional.<String>empty(), Optional.<Render>empty());
    }

    public static Link create(URI href, String rel, Optional<String> prompt) {
        return create(href, rel, prompt, Optional.<String>empty(), Optional.<Render>empty());
    }

    public static Link create(URI href, String rel, Optional<String> prompt, Optional<String> name) {
        return create(href, rel, prompt, name, Optional.<Render>empty());
    }

    public static Link create(URI href, String rel, Optional<String> prompt, Optional<String> name, Optional<Render> render) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("href", Optional.ofNullable(href).orElseThrow(() -> new IllegalArgumentException("Href may not be null")).toString());
        node.put("rel", Optional.ofNullable(rel).orElseThrow(() -> new IllegalArgumentException("Relation may not be null")));
        prompt.ifPresent(value -> node.put("prompt", value));
        render.ifPresent(value -> node.put("render", value.getName()));
        name.ifPresent(value -> node.put("name", value));
        return new Link(node);
    }

    public URI getHref() {
        return delegate.has("href") ? URI.create(delegate.get("href").asText()) : null;
    }

    public Link withHref(URI href) {
        ObjectNode node = copyDelegate();
        node.put("href", href.toString());
        return copy(node);
    }

    public String getRel() {
        return delegate.get("rel").asText();
    }

    public Link withRel(String rel) {
        ObjectNode node = copyDelegate();
        node.put("rel", rel);
        return copy(node);
    }

    public List<String> getParsedRel() {
        return Arrays.asList(getRel().split("\\s"));
    }

    public Optional<String> getPrompt() {
        return Optional.ofNullable(getAsString("prompt"));
    }

    public Link withPrompt(String prompt) {
        ObjectNode node = copyDelegate();
        node.put("prompt", prompt);
        return copy(node);
    }

    public Optional<String> getName() {
        return Optional.ofNullable(getAsString("name"));
    }

    public Link withName(String name) {
        ObjectNode node = copyDelegate();
        node.put("name", name);
        return copy(node);
    }

    public Render getRender() {
        return delegate.has("render") ? Render.valueOf(delegate.get("render").asText()) : Render.Link;
    }

    public Link withRender(Render render) {
        ObjectNode node = copyDelegate();
        node.put("render", render.getName());
        return copy(node);
    }

    @Override
    public String toString() {
        return String.format("Link{href=%s,rel=%s,prompt=%s,name=%s,render=%s}", getHref(), getRel(), getPrompt(), getName(), getRender());
    }

    public void validate() {
        Optional.ofNullable(getHref()).orElseThrow(() -> new IllegalArgumentException("Href was null"));
        Optional.ofNullable(getRel()).orElseThrow(() -> new IllegalArgumentException("Rel was null"));
    }

    static List<Link> fromArray(JsonNode node) {
        return Collections.unmodifiableList(StreamSupport.stream(node.spliterator(), false)
                .map(jsonNode -> new Link((ObjectNode) jsonNode))
                .collect(Collectors.toList()));
    }
}

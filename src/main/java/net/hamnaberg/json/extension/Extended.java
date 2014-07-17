package net.hamnaberg.json.extension;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Extended<T> {
    protected final ObjectNode delegate;

    protected Extended(ObjectNode delegate) {
        this.delegate = delegate;
    }

    protected abstract T copy(ObjectNode value);

    public <A> A getExtension(Extension<A> extension) {
        return extension.extract(delegate);
    }

    @SuppressWarnings("unchecked")
    public <A> T apply(A value, Extension<A> extension) {
        Map<String, JsonNode> map = extension.apply(value);
        if (map == null || map.isEmpty()) {
            return (T)this;
        }
        ObjectNode copied = copyDelegate();
        copied.setAll(map);
        return copy(copied);
    }

    protected ObjectNode copyDelegate() {
        ObjectNode copied = JsonNodeFactory.instance.objectNode();
        copied.setAll(delegate);
        return copied;
    }

    public ObjectNode asJson() {
        return copyDelegate();
    }

    protected String getAsString(String name) {
        return delegate.has(name) ? delegate.get(name).asText() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Extended extended = (Extended) o;

        if (delegate != null ? !delegate.equals(extended.delegate) : extended.delegate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return delegate != null ? delegate.hashCode() : 0;
    }

    public abstract void validate();
}

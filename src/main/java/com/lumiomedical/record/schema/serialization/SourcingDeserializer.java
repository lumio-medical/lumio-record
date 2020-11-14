package com.lumiomedical.record.schema.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.lumiomedical.record.source.AbstractSource;
import com.lumiomedical.record.source.SourceSet;
import com.lumiomedical.record.source.Sourcing;
import com.lumiomedical.record.source.register.SourceRegister;
import com.noleme.json.Json;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * This uses quite a bit of unsafe type juggling, but we should be fine due to the fact that Source maps have strictly controlled contents.
 *
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/06/26
 */
public class SourcingDeserializer extends StdDeserializer<Sourcing>
{
    protected SourcingDeserializer()
    {
        this(null);
    }

    protected SourcingDeserializer(Class<?> vc)
    {
        super(vc);
    }

    @Override
    public Sourcing deserialize(JsonParser p, DeserializationContext ctx) throws IOException
    {
        var currentValue = p.getParsingContext().getCurrentValue();
        SourceSet sourceSet = SourceRegister.forType(currentValue.getClass());
        Sourcing sourcing = new Sourcing();

        JsonNode sourceNode = p.getCodec().readTree(p);
        sourceNode.fields()
            .forEachRemaining(
                e -> {
                    //noinspection unchecked
                    AbstractSource<?, ?> source = SourceRegister.forName(sourceSet.getClass(), e.getKey());
                    Object value = e.getValue().isArray() ? asSet((ArrayNode) e.getValue(), source.type()) : Json.asValue(e.getValue());
                    sourcing.setNonSpecificSourceId(source, value);
                }
            );

        return sourcing;
    }

    @SuppressWarnings("unchecked")
    private static <T> Set<T> asSet(ArrayNode nodes, Class<T> sourceType)
    {
        Set<T> set = new HashSet<>();

        for (JsonNode node : nodes)
            set.add((T) Json.asValue(node));

        return set;
    }
}

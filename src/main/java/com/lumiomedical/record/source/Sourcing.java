package com.lumiomedical.record.source;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lumiomedical.record.schema.serialization.SourcingDeserializer;

import java.util.*;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/03/06
 */
@JsonDeserialize(using = SourcingDeserializer.class)
public class Sourcing
{
    @JsonValue
    private Map<AbstractSource, Object> sources;

    public Sourcing()
    {
        this.sources = new HashMap<>();
    }

    public Map<AbstractSource, Object> getSources()
    {
        return sources;
    }

    /**
     *
     * @param source
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T, OT> T getSourceId(AbstractSource<T, OT> source)
    {
        return (T) this.sources.get(source);
    }

    /**
     *
     * @param source
     * @param value
     * @param <T>
     * @return
     */
    public <T, OT> Sourcing setSourceId(AbstractSource<T, OT> source, T value)
    {
        this.sources.put(source, value);
        return this;
    }

    /**
     *
     * @param source
     * @param value
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T, OT> Sourcing addSourceId(SourceCollection<T, OT> source, T value)
    {
        if (!this.sources.containsKey(source))
            this.sources.put(source, new HashSet<>());
        ((Set<T>) this.sources.get(source)).add(value);
        return this;
    }

    /**
     *
     * @param source
     * @param value
     * @return
     */
    public Sourcing setNonSpecificSourceId(AbstractSource<?, ?> source, Object value)
    {
        this.sources.put(source, value);
        return this;
    }

    /**
     *
     * @param source
     * @param <T>
     * @return
     */
    public <T, OT> boolean hasSource(AbstractSource<T, OT> source)
    {
        return this.sources.containsKey(source);
    }

    /**
     * 
     * @return
     */
    public boolean isEmpty()
    {
        return this.sources.isEmpty();
    }

    /**
     *
     * @param sources
     * @return
     */
    public Sourcing setSources(Map<AbstractSource, Object> sources)
    {
        this.sources = sources;
        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Sourcing sourcing = (Sourcing) o;
        return sources.equals(sourcing.sources);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(sources);
    }
}

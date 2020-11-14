package com.lumiomedical.record.source;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/04/29
 *
 * @param <T> Type of the source index
 * @param <OT> Type of the indexed entity
 */
public abstract class AbstractSource<T, OT>
{
    @JsonValue
    private final String name;
    private final Class<?> type;
    private final Class<OT> objectType;

    /**
     *
     * @param name
     * @param type
     */
    public AbstractSource(String name, Class<?> type, Class<OT> objectType)
    {
        this.name = name;
        this.type = type;
        this.objectType = objectType;
    }

    public String name()
    {
        return this.name;
    }

    public String queryName()
    {
        return "sources." + this.name();
    }

    public Class<?> type()
    {
        return this.type;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AbstractSource<?, ?> that = (AbstractSource<?, ?>) o;
        return this.objectType.equals(that.objectType)
            && this.name.equals(that.name)
            && this.type.equals(that.type);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.name, this.type);
    }
}

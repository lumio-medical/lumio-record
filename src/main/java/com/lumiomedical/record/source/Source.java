package com.lumiomedical.record.source;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/02/29
 */
public final class Source<T, OT> extends AbstractSource<T, OT>
{
    /**
     *
     * @param name
     * @param type
     * @param <T>
     * @param <OT>
     * @return
     */
    public static <T, OT> Source<T, OT> of(String name, Class<T> type, Class<OT> objectType)
    {
        return new Source<>(name, type, objectType);
    }

    /**
     *
     * @param name
     * @param type
     */
    private Source(String name, Class<T> type, Class<OT> objectType)
    {
        super(name, type, objectType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> type()
    {
        return (Class<T>) super.type();
    }
}

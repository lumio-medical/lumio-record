package com.lumiomedical.record.source;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/13
 */
public final class SourceSetA implements SourceSet<String>
{
    public final Source<String, String> SOURCE_A = sourceOf("source_a", String.class);
    public final Source<Integer, String> SOURCE_B = sourceOf("source_b", Integer.class);

    @Override
    public Class<String> getSourcedType()
    {
        return String.class;
    }
}

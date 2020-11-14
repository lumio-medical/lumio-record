package com.lumiomedical.record.source;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/13
 */
public final class SourceSetB implements SourceSet<Integer>
{
    public final Source<String, Integer> SOURCE_A = sourceOf("source_a", String.class);
    public final Source<Integer, Integer> SOURCE_B = sourceOf("source_b", Integer.class);

    @Override
    public Class<Integer> getSourcedType()
    {
        return Integer.class;
    }
}

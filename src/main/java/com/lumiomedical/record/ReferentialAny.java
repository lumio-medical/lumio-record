package com.lumiomedical.record;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/06/30
 */
public class ReferentialAny extends Referential
{
    static final ReferentialAny singleton = new ReferentialAny();

    @Override
    public boolean isAny()
    {
        return true;
    }

    @Override
    public ReferentialAny asAny()
    {
        return this;
    }
}

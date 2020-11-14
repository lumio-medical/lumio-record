package com.lumiomedical.record;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/06/30
 */
public class ReferentialAt extends ReferentialPoint
{
    @JsonProperty
    private final Instant at;

    /**
     *
     * @param at
     */
    ReferentialAt(Instant at)
    {
        this.at = at;
    }

    @Override
    public boolean isAt()
    {
        return true;
    }

    @Override
    public ReferentialAt asAt()
    {
        return this;
    }

    @Override
    public Instant getAt()
    {
        return this.at;
    }
}

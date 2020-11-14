package com.lumiomedical.record;

import java.time.Instant;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/06/30
 */
public class ReferentialBetween extends Referential
{
    private final Instant from;
    private final Instant to;

    /**
     *
     * @param from
     * @param to
     */
    public ReferentialBetween(Instant from, Instant to)
    {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean isBetween()
    {
        return true;
    }

    @Override
    public ReferentialBetween asBetween()
    {
        return this;
    }

    public Instant getFrom()
    {
        return this.from;
    }

    public Instant getTo()
    {
        return this.to;
    }
}

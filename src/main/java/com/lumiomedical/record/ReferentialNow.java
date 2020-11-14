package com.lumiomedical.record;

import java.time.Instant;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/06/30
 */
public class ReferentialNow extends ReferentialPoint
{
    static final ReferentialNow singleton = new ReferentialNow();
    private Instant pointNow;

    @Override
    public boolean isNow()
    {
        return true;
    }

    @Override
    public ReferentialNow asNow()
    {
        return this;
    }

    @Override
    public Instant getAt()
    {
        if (this.pointNow == null)
            this.initializePoint();

        return this.pointNow;
    }

    /**
     *
     */
    synchronized private void initializePoint()
    {
        if (this.pointNow == null)
            this.pointNow = Instant.now();
    }
}

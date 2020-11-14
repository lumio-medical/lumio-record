package com.lumiomedical.record;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/06/30
 */
public abstract class ReferentialPoint extends Referential
{
    /**
     *
     * @return
     */
    @JsonIgnore
    public abstract Instant getAt();
}

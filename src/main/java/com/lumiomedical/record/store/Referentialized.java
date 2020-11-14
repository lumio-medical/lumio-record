package com.lumiomedical.record.store;

import com.lumiomedical.record.Record;
import com.lumiomedical.record.Referential;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/03/20
 */
public final class Referentialized<R extends Record>
{
    public final R record;
    public final Referential referential;

    /**
     *
     * @param record
     * @param referential
     */
    public Referentialized(R record, Referential referential)
    {
        this.record = record;
        this.referential = referential;
    }
}

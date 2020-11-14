package com.lumiomedical.record.model;

import com.lumiomedical.record.source.Source;
import com.lumiomedical.record.source.SourceSet;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/14
 */
public final class MySource implements SourceSet<MyRecord>
{
    public final Source<String, MyRecord> MAIN_ID = sourceOf("main_id", String.class);
    public final Source<Integer, MyRecord> SECONDARY_ID = sourceOf("secondary_id", Integer.class);

    @Override
    public Class<MyRecord> getSourcedType()
    {
        return MyRecord.class;
    }
}

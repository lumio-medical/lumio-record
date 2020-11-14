package com.lumiomedical.record.schema;

import com.lumiomedical.record.UidHolder;
import com.lumiomedical.record.model.MyRecord;
import com.lumiomedical.record.source.Sources;
import org.junit.jupiter.api.Test;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/14
 */
public class SourceTest extends MarshallingTest
{
    @Test
    void marshallTest()
    {
        this.marshallTest(MyRecord.class, () -> (MyRecord) new MyRecord()
            .setSourceId(Sources.MySource.MAIN_ID, "2973")
            .setSourceId(Sources.MySource.SECONDARY_ID, 1234)
            .setUid(UidHolder.generateUid())
        );
    }

    @Test
    void schemaTest()
    {
        this.schemaTest(MyRecord.class);
    }
}

package com.lumiomedical.record.source;

import com.lumiomedical.record.source.register.SourceRegister;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/13
 */
public class SourceTest
{
    @Test
    void testName()
    {
        Assertions.assertEquals("source_a", Sources.A.SOURCE_A.name());
        Assertions.assertEquals("source_b", Sources.A.SOURCE_B.name());
        Assertions.assertEquals("source_a", Sources.B.SOURCE_A.name());
        Assertions.assertEquals("source_b", Sources.B.SOURCE_B.name());
    }

    @Test
    void testType()
    {
        Assertions.assertEquals(String.class, Sources.A.SOURCE_A.type());
        Assertions.assertEquals(Integer.class, Sources.A.SOURCE_B.type());
        Assertions.assertEquals(String.class, Sources.B.SOURCE_A.type());
        Assertions.assertEquals(Integer.class, Sources.B.SOURCE_B.type());
    }

    @Test
    void testIndexForName()
    {
        Assertions.assertEquals(Sources.A.SOURCE_A, SourceRegister.forName(SourceSetA.class, "source_a"));
        Assertions.assertEquals(Sources.A.SOURCE_B, SourceRegister.forName(SourceSetA.class, "source_b"));
        Assertions.assertNotEquals(Sources.A.SOURCE_B, SourceRegister.forName(SourceSetB.class, "source_b"));
        Assertions.assertNotEquals(Sources.B.SOURCE_A, SourceRegister.forName(SourceSetA.class, "source_a"));
    }

    @Test
    void testIndexForType()
    {
        Assertions.assertEquals(Sources.A, SourceRegister.forType(String.class));
        Assertions.assertEquals(Sources.B, SourceRegister.forType(Integer.class));
        Assertions.assertNotEquals(Sources.A, SourceRegister.forType(Integer.class));
        Assertions.assertNotEquals(Sources.B, SourceRegister.forType(String.class));
    }
}

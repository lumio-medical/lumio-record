package com.lumiomedical.record.source;

import com.lumiomedical.record.source.register.IndexedSource;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/13
 */
@IndexedSource
public interface Sources
{
    SourceSetA A = new SourceSetA();
    SourceSetB B = new SourceSetB();
}

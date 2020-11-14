package com.lumiomedical.record;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/03/07
 */
public interface HashHolder
{
    /**
     *
     * @return
     */
    String getHash();

    /**
     *
     * @param hash
     * @return
     */
    HashHolder setHash(String hash);
}

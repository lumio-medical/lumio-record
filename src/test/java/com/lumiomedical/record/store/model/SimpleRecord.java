package com.lumiomedical.record.store.model;

import com.lumiomedical.record.Record;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/03/16
 */
public class SimpleRecord extends Record<SimpleRecord>
{
    private String name;
    private Long age;
    private Boolean trueness;

    public String getName()
    {
        return name;
    }

    public SimpleRecord setName(String name)
    {
        this.name = name;
        return this;
    }

    public Long getAge()
    {
        return age;
    }

    public SimpleRecord setAge(Long age)
    {
        this.age = age;
        return this;
    }

    public Boolean getTrueness()
    {
        return trueness;
    }

    public SimpleRecord setTrueness(Boolean trueness)
    {
        this.trueness = trueness;
        return this;
    }
}

package com.lumiomedical.record.store.model;

import com.lumiomedical.record.Record;

import java.time.Instant;
import java.util.*;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/03/11
 */
public class ComplexRecord extends Record<ComplexRecord>
{
    private String name;
    private Long age;
    private Boolean trueness;
    private Instant birthDate;
    private List<Long> longValues;
    private Set<String> stringValues;
    private List<ComplexRecord> subitems;
    private Set<ComplexRecord> subitemSet;
    private Map<String, Boolean> map;

    public ComplexRecord()
    {
        this.trueness = false;
        this.birthDate = Instant.now();
        this.longValues = new ArrayList<>();
        this.stringValues = new HashSet<>();
        this.subitems = new ArrayList<>();
        this.subitemSet = new HashSet<>();
        this.map = new HashMap<>();
    }

    public String getName()
    {
        return name;
    }

    public ComplexRecord setName(String name)
    {
        this.name = name;
        return this;
    }

    public Long getAge()
    {
        return age;
    }

    public ComplexRecord setAge(Long age)
    {
        this.age = age;
        return this;
    }

    public Boolean getTrueness()
    {
        return trueness;
    }

    public ComplexRecord setTrueness(Boolean trueness)
    {
        this.trueness = trueness;
        return this;
    }

    public Instant getBirthDate()
    {
        return birthDate;
    }

    public ComplexRecord setBirthDate(Instant birthDate)
    {
        this.birthDate = birthDate;
        return this;
    }

    public List<Long> getLongValues()
    {
        return longValues;
    }

    public ComplexRecord addLongValue(long longValue)
    {
        this.longValues.add(longValue);
        return this;
    }

    public ComplexRecord setLongValues(Long... longValues)
    {
        return this.setLongValues(Arrays.asList(longValues));
    }

    public ComplexRecord setLongValues(List<Long> longValues)
    {
        this.longValues = longValues;
        return this;
    }

    public Set<String> getStringValues()
    {
        return stringValues;
    }

    public ComplexRecord addStringValue(String stringValue)
    {
        this.stringValues.add(stringValue);
        return this;
    }

    public ComplexRecord setStringValues(String... stringValues)
    {
        return this.setStringValues(Set.of(stringValues));
    }

    public ComplexRecord setStringValues(Set<String> stringValues)
    {
        this.stringValues = stringValues;
        return this;
    }

    public List<ComplexRecord> getSubitems()
    {
        return subitems;
    }

    public ComplexRecord addSubitem(ComplexRecord subitem)
    {
        this.subitems.add(subitem);
        return this;
    }

    public ComplexRecord setSubitems(List<ComplexRecord> subitems)
    {
        this.subitems = subitems;
        return this;
    }

    public Set<ComplexRecord> getSubitemSet()
    {
        return subitemSet;
    }

    public ComplexRecord addSubitemToSet(ComplexRecord subitem)
    {
        this.subitemSet.add(subitem);
        return this;
    }

    public ComplexRecord setSubitemSet(Set<ComplexRecord> subitems)
    {
        this.subitemSet = subitems;
        return this;
    }

    public ComplexRecord putMap(String key, boolean value)
    {
        this.map.put(key, value);
        return this;
    }

    public Boolean getMap(String key)
    {
        return this.map.get(key);
    }

    public Map<String, Boolean> getMap()
    {
        return map;
    }

    public ComplexRecord setMap(Map<String, Boolean> map)
    {
        this.map = map;
        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ComplexRecord that = (ComplexRecord) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(age, that.age) &&
            Objects.equals(trueness, that.trueness) &&
            Objects.equals(birthDate, that.birthDate) &&
            Objects.equals(longValues, that.longValues) &&
            Objects.equals(stringValues, that.stringValues) &&
            Objects.equals(subitems, that.subitems) &&
            Objects.equals(subitemSet, that.subitemSet) &&
            Objects.equals(map, that.map);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, age, trueness, birthDate, longValues, stringValues, subitems, subitemSet, map);
    }
}

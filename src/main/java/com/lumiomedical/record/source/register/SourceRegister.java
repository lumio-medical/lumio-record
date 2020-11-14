package com.lumiomedical.record.source.register;

import com.lumiomedical.record.logging.Logging;
import com.lumiomedical.record.source.AbstractSource;
import com.lumiomedical.record.source.SourceSet;
import org.atteo.classindex.ClassIndex;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The SourceRegister is responsible for holding references to indexed SourceSets and their Sources.
 *
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/03/06
 */
public final class SourceRegister
{
    private static final Map<Class<?>, Map<String, AbstractSource>> dictionaries = new HashMap<>();
    private static final Map<Class<?>, SourceSet<?>> setDictionary = new HashMap<>();

    static {
        Logging.logger.debug("Launching @IndexedSource indexation.");
        for (Class<?> indexedSource : ClassIndex.getAnnotated(IndexedSource.class))
            index(indexedSource);
    }

    private SourceRegister() {}

    /**
     * A utility method for indexing the @IndexedSource fields of a given "set holder" class.
     *
     * @param setHolder
     */
    private static void index(Class<?> setHolder)
    {
        try {
            Logging.logger.debug("Indexing "+setHolder.getName()+" source holder");
            for (Field field : setHolder.getDeclaredFields())
            {
                if (!Modifier.isStatic(field.getModifiers()) || !SourceSet.class.isAssignableFrom(field.getType()))
                    Logging.logger.debug("Ignoring field "+field.getName()+" within class "+setHolder.getName()+" annotated as @IndexedSource: it has to be static and of a SourceSet subtype.");

                index((SourceSet) field.get(null));
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     *
     * @param set
     */
    private static void index(SourceSet set)
    {
        try {
            Map<String, AbstractSource> sourceMap = new HashMap<>();

            for (Field f : set.getClass().getDeclaredFields())
            {
                if (!AbstractSource.class.isAssignableFrom(f.getType()) || !f.canAccess(set))
                    continue;
                AbstractSource source = (AbstractSource) f.get(set);

                sourceMap.put(source.name(), source);
            }

            dictionaries.put(set.getClass(), sourceMap);
            setDictionary.put(set.getSourcedType(), set);
        }
        catch (IllegalAccessException ignored) {}
    }

    /**
     *
     * @param c
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T, ST extends SourceSet<T>> AbstractSource<?, T> forName(Class<ST> c, String name)
    {
        return dictionaries.get(c).get(name);
    }

    /**
     *
     * @param sourceType
     * @return
     */
    public static <ST extends SourceSet> Collection<AbstractSource> listForSourceSet(Class<ST> sourceType)
    {
        return dictionaries.get(sourceType).values();
    }

    /**
     *
     * @param type
     * @param <T>
     * @return
     */
    public static <T> SourceSet<?> forType(Class<T> type)
    {
        return setDictionary.get(type);
    }
}

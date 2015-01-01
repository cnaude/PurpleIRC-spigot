/*******************************************************************************
 * Copyright (c) 2008, 2010 VMware Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   VMware Inc. - initial contribution
 *******************************************************************************/

// http://underlap.blogspot.com/2009/04/caseinsensitivemapv.html

package com.cnaude.purpleirc.Utilities;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link CaseInsensitiveMap} is a {@link Map} from <code>String</code> keys to values which is case-insensitive and
 * case-preserving with respect to the keys in the map.
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * This class is not necessarily thread safe.
 * 
 * @param <V> range type parameter
 */
public class CaseInsensitiveMap<V> extends AbstractMap<String, V> {

    private final Map<CaseInsensitiveKey, V> map = new ConcurrentHashMap<CaseInsensitiveKey, V>();

    private static final class KeySet extends AbstractSet<String> {

        private static final class KeySetIterator implements Iterator<String> {

            private final Iterator<CaseInsensitiveKey> iterator;

            public KeySetIterator(Iterator<CaseInsensitiveKey> iterator) {
                this.iterator = iterator;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean hasNext() {
                return this.iterator.hasNext();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String next() {
                return this.iterator.next().toString();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void remove() {
                this.iterator.remove();
            }
        }

        private final Set<CaseInsensitiveKey> keySet;

        public KeySet(Set<CaseInsensitiveKey> keySet) {
            this.keySet = keySet;
        }

        /**
         * Not supported for sets returned by <code>Map.keySet</code>.
         */
        @Override
        public boolean add(String o) {
            throw new UnsupportedOperationException("Map.keySet must return a Set which does not support add");
        }

        /**
         * Not supported for sets returned by <code>Map.keySet</code>.
         */
        @Override
        public boolean addAll(Collection<? extends String> c) {
            throw new UnsupportedOperationException("Map.keySet must return a Set which does not support addAll");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clear() {
            this.keySet.clear();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean contains(Object o) {
            return o instanceof String ? this.keySet.contains(CaseInsensitiveKey.objectToKey(o)) : false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<String> iterator() {
            return new KeySetIterator(this.keySet.iterator());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean remove(Object o) {
            // The following can throw ClassCastException which conforms to the method specification.
            return this.keySet.remove(CaseInsensitiveKey.objectToKey(o));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return this.keySet.size();
        }

    }

    private static final class EntrySet<V> extends AbstractSet<Entry<String, V>> {

        private static final class MapEntry<V> implements Entry<String, V> {

            private final Entry<CaseInsensitiveMap.CaseInsensitiveKey, V> entry;

            public MapEntry(Entry<CaseInsensitiveMap.CaseInsensitiveKey, V> entry) {
                this.entry = entry;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String getKey() {
                return this.entry.getKey().toString();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public V getValue() {
                return this.entry.getValue();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public V setValue(V value) {
                return this.entry.setValue(value);
            }

            /**
             * Get the underlying {@link Map.Entry}.
             * 
             * @return the underlying <code>Entry</code>
             */
            public Entry<CaseInsensitiveMap.CaseInsensitiveKey, V> getEntry() {
                return this.entry;
            }
        }

        private static final class EntrySetIterator<V> implements Iterator<Entry<String, V>> {

            private final Iterator<Entry<CaseInsensitiveKey, V>> iterator;

            public EntrySetIterator(Iterator<Entry<CaseInsensitiveKey, V>> iterator) {
                this.iterator = iterator;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean hasNext() {
                return this.iterator.hasNext();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public Entry<String, V> next() {
                return new MapEntry<V>(this.iterator.next());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void remove() {
                this.iterator.remove();
            }
        }

        private final Set<Entry<CaseInsensitiveKey, V>> entrySet;
        
        private final  CaseInsensitiveMap<V> map;

        public EntrySet(Set<Entry<CaseInsensitiveKey, V>> entrySet, CaseInsensitiveMap<V> map) {
            this.entrySet = entrySet;
            this.map = map;
        }

        /**
         * Not supported for sets returned by <code>Map.entrySet</code>.
         */
        @Override
        public boolean add(Entry<String, V> o) {
            throw new UnsupportedOperationException("Map.entrySet must return a Set which does not support add");
        }

        /**
         * Not supported for sets returned by <code>Map.entrySet</code>.
         */
        @Override
        public boolean addAll(Collection<? extends Entry<String, V>> c) {
            throw new UnsupportedOperationException("Map.entrySet must return a Set which does not support addAll");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clear() {
            this.entrySet.clear();
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean contains(Object o) {
            if (o instanceof Entry) {
                Entry<String, V> e = (Entry<String, V>) o;
                V value = this.map.get(e.getKey());
                return value.equals(e.getValue());
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<Entry<String, V>> iterator() {
            return new EntrySetIterator<V>(this.entrySet.iterator());
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean remove(Object o) {
            try {
                return this.entrySet.remove(((MapEntry<V>) o).getEntry());
            } catch (ClassCastException e) {
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return this.entrySet.size();
        }

    }

    static final class CaseInsensitiveKey {

        private final String key;

        private CaseInsensitiveKey(String key) {
            this.key = key;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + key.toLowerCase().hashCode();
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            CaseInsensitiveKey other = (CaseInsensitiveKey) obj;
            if (key == null) {
                if (other.key != null) {
                    return false;
                }
            } else if (!key.equalsIgnoreCase(other.key)) {
                return false;
            }
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return key;
        }

        /**
         * Convert the given key <code>Object</code> to a {@link CaseInsensitiveKey}.
         * <p/>
         * Pre-condition: <code>key</code> instanceof <code>String</code>
         * 
         * @param key the key to be converted
         * @return the <code>CaseInsensitiveKey</code> corresponding to the given key
         */
        public static CaseInsensitiveKey objectToKey(Object key) {
            return new CaseInsensitiveKey((String) key);
        }

    }

    public CaseInsensitiveMap() {
    }

    public CaseInsensitiveMap(CaseInsensitiveMap<? extends V> map) {
        this.map.putAll(map.map);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        this.map.clear();
    }

    /**
     * {@inheritDoc}
     * @param key
     * @return 
     */
    @Override
    public boolean containsKey(Object key) {
        return key instanceof String ? this.map.containsKey(CaseInsensitiveKey.objectToKey(key)) : false;
    }

    /**
     * {@inheritDoc}
     * @param value
     * @return 
     */
    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    /**
     * {@inheritDoc}
     * @return 
     */
    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<V>(this.map.entrySet(), this);
    }

    /**
     * {@inheritDoc}
     * @param key
     * @return 
     */
    @Override
    public V get(Object key) {
        return key instanceof String ? this.map.get(CaseInsensitiveKey.objectToKey(key)) : null;
    }

    /**
     * {@inheritDoc}
     * @return 
     */
    @Override
    public Set<String> keySet() {
        return new KeySet(this.map.keySet());
    }

    /**
     * {@inheritDoc}
     * @param key
     * @param value
     * @return 
     */
    @Override
    public V put(String key, V value) {
        if (key == null) {
            throw new NullPointerException("CaseInsensitiveMap does not permit null keys");
        }
        return this.map.put(CaseInsensitiveKey.objectToKey(key), value);
    }

    /**
     * {@inheritDoc}
     * @param key
     * @return 
     */
    @Override
    public V remove(Object key) {
        return key instanceof String ? this.map.remove(CaseInsensitiveKey.objectToKey(key)) : null;
    }

    /**
     * {@inheritDoc}
     * @return 
     */
    @Override
    public int size() {
        return this.map.size();
    }

    /**
     * {@inheritDoc}
     * @return 
     */
    @Override
    public Collection<V> values() {
        return this.map.values();
    }

}
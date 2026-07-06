package edu.unipampa.ed.api;

import java.util.TreeSet;

class OrderedLongSetContractTest extends OrderedLongSetContract {

    @Override
    protected OrderedLongSet createSet() {
        return new InMemoryOrderedLongSet();
    }

    private static final class InMemoryOrderedLongSet implements OrderedLongSet {
        private final TreeSet<Long> keys = new TreeSet<>();

        @Override
        public void insert(long key) {
            keys.add(key);
        }

        @Override
        public void delete(long key) {
            keys.remove(key);
        }

        @Override
        public boolean search(long key) {
            return keys.contains(key);
        }

        @Override
        public long size() {
            return keys.size();
        }
    }
}

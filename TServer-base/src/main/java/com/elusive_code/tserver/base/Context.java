package com.elusive_code.tserver.base;

import com.elusive_code.tserver.jackson.ContextDeserializer;
import com.elusive_code.tserver.jackson.ContextSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;

/**
 * Execution context.
 *
 * Elements should be either thread-safe or accessed with synchronization on context.
 * Elements should be JSON-friendly.
 *
 * @author Vladislav Dolgikh
 */
@JsonSerialize(using = ContextSerializer.class)
@JsonDeserialize(using = ContextDeserializer.class)
public class Context extends AbstractMap<String,Object>  {

    private Context                           parent   = null;
    private EntrySet                          entrySet = null;
    private List<Context>                     children = null;
    private ConcurrentSkipListSet<String>     finals   = null;
    private ConcurrentHashMap<String, Object> params   = new ConcurrentHashMap<>();

    public Context() {
    }

    public Context(Context parent) {
        this.parent = parent;
        if (parent != null) {
            parent.getChildren().add(this);
        }
    }

    public Context(Map<String, Object> params) {
        this.params.putAll(params);
    }

    public Context(Context parent, Map<String, Object> params) {
        this.parent = parent;
        this.params.putAll(params);
    }

    public ExecutorService getExecutor() {
        return (ExecutorService) get(CtxParam.EXECUTOR.key());
    }

    public Context getParent() {
        return parent;
    }

    public synchronized List<Context> getChildren() {
        if (children == null) {
            children = Collections.synchronizedList(new ArrayList<>());
        }
        return (List)children;
    }

    public synchronized Set<String> getFinals() {
        if (finals == null) {
            finals = new ConcurrentSkipListSet<>();
        }
        return finals;
    }

    public Map<String,Object> getParams(){
        return params;
    }

    @Override
    public Object put(String key, Object value) {
        if (finals != null && finals.contains(key)) {
            throw new FinalModificationException(key + " parameter is final");
        }
        return params.put(key, value);
    }

    public synchronized Object putFinal(String key,Object value){
        if (finals == null) {
            finals = new ConcurrentSkipListSet<>();
        }
        finals.add(key);
        return params.put(key,value);
    }

    public synchronized boolean resetFinal(String key){
        if (finals == null) return false;
        return finals.remove(key);
    }

    public synchronized boolean isFinal(String key){
        return finals != null && finals.contains(key);
    }

    @Override
    public synchronized Set<Entry<String, Object>> entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet();
        }
        return entrySet;
    }

    @Override
    public Object get(Object key) {
        return get(key,true);
    }

    public <R> R get(Object key, boolean includeParent){
        if (key == null) return null;
        if (key instanceof CtxParam){
            key = ((CtxParam) key).name();
        }
        Object result = params.get(key);
        if (result == null && includeParent && parent != null) {
            result = parent.get(key);
        }
        return (R)result;
    }

    private class EntrySet extends AbstractSet<Entry<String,Object>> {
        private boolean                    includeParent  = false;

        @Override
        public Iterator<Entry<String, Object>> iterator() {
            return new EntryIterator(includeParent);
        }

        @Override
        public int size() {
            int result = params.entrySet().size();
            if (includeParent && parent != null) {
                result += parent.size();
            }
            return result;
        }

        @Override
        public boolean contains(Object o) {
            return params.entrySet().contains(o)
                   || (includeParent && parent != null && parent.entrySet().contains(o));
        }

        @Override
        public boolean add(Entry<String, Object> stringObjectEntry) {
            String key = stringObjectEntry.getKey();
            if (finals.contains(key) && containsKey(key)) {
                throw new FinalModificationException(key+" is final");
            }
            return params.entrySet().add(stringObjectEntry);
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Entry)) return false;
            Object key = ((Entry) o).getKey();
            if (finals.contains(key)) {
                throw new FinalModificationException(key + " parameter is final");
            }
            boolean contained = params.entrySet().remove(o);
            if (contained) return true;
            if (!includeParent || parent == null) {
                return false;
            }
            return parent.entrySet().remove(o);
        }
    }

    private class EntryIterator implements Iterator<Entry<String, Object>> {

        private Entry<String, Object>           last            = null;
        private boolean                         iterateParent   = true;
        private Iterator<Entry<String, Object>> currentIterator = params.entrySet().iterator();

        private EntryIterator(boolean iterateParent) {
            this.iterateParent = iterateParent;
        }

        private void checkIterator() {
            if (currentIterator == null || currentIterator.hasNext()) return;
            if (!iterateParent) {
                currentIterator = null;
            } else {
                iterateParent = false;
                currentIterator = getParent().entrySet().iterator();
            }
        }

        @Override
        public boolean hasNext() {
            checkIterator();
            return currentIterator != null;
        }

        @Override
        public Entry<String, Object> next() {
            checkIterator();
            if (currentIterator == null) throw new NoSuchElementException();
            last = currentIterator.next();
            return last;
        }

        @Override
        public void remove() {
            if (last == null || currentIterator == null) throw new IllegalStateException();
            if (finals.contains(last.getKey())) {
                throw new FinalModificationException(last.getKey() + " parameter is final");
            }
            currentIterator.remove();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Context context = (Context) o;

        if (!getChildren().equals(context.getChildren())) return false;
        if (!getFinals().equals(context.getFinals())) return false;
        if (!getParams().equals(context.getParams())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getChildren().hashCode();
        result = 31 * result + getFinals().hashCode();
        result = 31 * result + getParams().hashCode();
        return result;
    }
}

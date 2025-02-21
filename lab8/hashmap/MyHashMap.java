package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int initialSize = 16;
    private double loadFactor;
    private double maxLoad;
    private int size = 0;
    private static final Object NULL_VALUE = new Object();
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() { this(16,0.75);}

    public MyHashMap(int initialSize) { }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad)
    {
        buckets = (Collection<Node>[]) new Collection[initialSize];
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }
        this.maxLoad = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key,value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    private int loadFactor (){
        return size / buckets.length;
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return null;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    public void clear() {
        this.size = 0;
        for (int i = 0; i < buckets.length; i++){
            buckets[i] = createBucket();
        }
    }

    @Override
    public boolean containsKey(K key) {
        return findNode(key) != null;
    }

    @Override
    public V get(K key) {
        Node node = findNode(key);
        return findNode(key) == null ? null : node.value;
    }

    public Node findNode(K key) {
        if (key != null){
            int hashCode = hashCode(key);
            Collection<Node> bucket = buckets[hashCode];
            for (Node node: bucket) {
                if (node.key.equals(key)) {
                    return node;
                }
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int hashcode = hashCode(key);
        for (Node node: buckets[hashcode]) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        Node node = new Node(key,value);
        buckets[hashcode].add(node);
        this.size += 1;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for(Collection<Node> bucket: buckets) {
            for (Node node: bucket) {
                keys.add(node.key);
            }
        }
        return keys;
    }

    @Override
    public Iterator<K> iterator() {
        Set<K> keyset = keySet();
        return keyset.iterator();
    }

    @Override
    public V remove(K key) {
        int hash = hashCode(key);
        for (Node node: buckets[hash]){
            if (node.key.equals(key)){
                V value = node.value;
                buckets[hash].remove(node);
                size -= 1;
                return value;
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        if (findNode(key).value == value){
            remove(key);
            size-= 1;
            return value;
        }
        return null;
    }

    private int hashCode(K key) {
        return key == null ? 0 : Math.abs(key.hashCode()) % initialSize;
    }




}

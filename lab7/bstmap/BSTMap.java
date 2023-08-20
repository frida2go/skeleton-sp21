package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode root;

    public BSTMap() {
        root = null;
    }

    private class BSTNode {
        K key;
        V value;
        BSTNode left;
        BSTNode right;
        int size;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
            size = 1;
        }
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        return containKey(key, root);
    }

    private boolean containKey(K key, BSTNode node) {
        if (node == null) {
            return false;
        }
        int cmp = key.compareTo(node.key);
        if (cmp > 0) {
            return containKey(key, node.right);
        } else if (cmp < 0) {
            return containKey(key, node.left);
        }
        return true;
    }

    @Override
    public V get(K key) {
        return get(root, key);
    }

    private V get(BSTNode node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            return node.value;
        } else if (cmp > 0) {
            return get(node.right, key);
        } else {
            return get(node.left, key);
        }
    }

    @Override
    public int size() {
        return size(root);
    }

    @Override
    public void put(K key, V value) {
        root = put(key, value, root);
    }

    private BSTNode put(K key, V value, BSTNode node) {
        if (node == null) {
            return new BSTNode(key, value);
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            node.value = value;
        } else if (cmp < 0) {
            node.left = put(key, value, node.left);
        } else {
            node.right = put(key, value, node.right);
        }
        node.size = size(node.left) + size(node.right) + 1;
        return node;

    }

    private int size(BSTNode node) {
        if (node == null) {
            return 0;
        }
        return node.size;
    }


    @Override
    public V remove(K key) {
        return remove(key,root).value;
    }

    private BSTNode remove(K key,BSTNode node) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(key,node.left);
        } else if (cmp > 0) {
            node.right = remove(key,node.right);
        } else {
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }
           BSTNode maxLeft = maxNode(node.left);
           node.value = maxLeft.value;
           node.key = maxLeft.key;
           node.left = remove(maxLeft.key,node.left);
        }
        return node;
    }

    private BSTNode maxNode(BSTNode node){
        while (node.right != null){
            node = node.right;
        }
        return node;
    }
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        Set <K> keysSet = new HashSet<K>();
        keySet(root,keysSet);
        return keysSet;
    }

    private void keySet(BSTNode node,Set<K> keys){
        if (node == null){
            return;
        }
        keys.add(node.key);
        keySet(node.left,keys);
        keySet(node.right,keys);
    }

    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }


}

package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    private IntNode sentinel;
    private int size;

    private class IntNode {
        private IntNode previous;
        private T item;
        private IntNode next;

        private IntNode(T item, IntNode previous, IntNode next) {
            this.item = item;
            this.previous = previous;
            this.next = next;
        }

    }

    public LinkedListDeque() {
        sentinel = new IntNode(null, null, null);
        size = 0;
    }


    public void addFirst(T item) {
        if (isEmpty()) {
            sentinel.next = new IntNode(item, sentinel, sentinel);
            sentinel.previous = sentinel.next;
        } else {
            IntNode newly = new IntNode(item, sentinel, sentinel.next);
            sentinel.next = newly;
            newly.next.previous = newly;

        }
        size += 1;
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        IntNode remove = sentinel.next;
        T item = remove.item;
        sentinel.next = remove.next;
        remove.next.previous = sentinel;
        size -= 1;
        return item;
    }

    public void addLast(T item) {
        IntNode temp = new IntNode(item, sentinel.previous, sentinel);
        if (isEmpty()) {
            sentinel.next = temp;
        } else {
            sentinel.previous.next = temp;
        }
        sentinel.previous = temp;
        size += 1;


    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        IntNode temp = sentinel.previous;
        T item = temp.item;
        sentinel.previous = temp.previous;

        if (size == 1) {
            sentinel.next = sentinel;
        } else {
            temp.previous.next = sentinel;
        }

        size -= 1;
        return item;

    }

    public int size() {
        return size;
    }


    public void printDeque() {
        IntNode temp = sentinel.next;
        while (temp != sentinel) {
            T p = temp.item;
            if (temp.next == sentinel) {
                System.out.print(p);
            } else {
                System.out.print(p + " -> ");
            }
            temp = temp.next;
        }
    }

    public Iterator<T> iterator() {
        return new LinkedListDeque.LinkedListDequeIterator();
    }
    private class LinkedListDequeIterator implements Iterator<T> {
        private int wizPos = 0;

        @Override
        public boolean hasNext() {
            return wizPos < size;
        }

        @Override
        public T next() {
            T returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }

    public T get(int index) {
        if (index + 1 > size || index < 0) {
            return null;
        }
        IntNode temp = sentinel.next;
        while (index != 0) {
            temp = temp.next;
            index--;
        }
        return temp.item;
    }
    public T getRecursive(int index) {
        if (index + 1 > size || index < 0) {
            return null;
        }
        return rHelper(index, sentinel);

    }
    private T rHelper(int index, IntNode n) {
        if (index == 0) {
            return n.next.item;
        }
        return rHelper(index - 1, n.next);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }

        if (!(other instanceof Deque<?>)) {
            return false;
        }
        Deque sub = (Deque) other;
        if (sub.size() != this.size) {
            return false;
        }
        for (int i = 0; i < this.size; i++) {
            if (!this.get(i).equals(sub.get(i))) {
                return false;
            }
        }
        return true;
    }
}







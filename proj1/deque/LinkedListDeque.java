package deque;

public class LinkedListDeque<T> {
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

    public LinkedListDeque(T item) {
        sentinel = new IntNode(null, null, null);
        IntNode newly = new IntNode(item, sentinel, sentinel);
        sentinel.next = newly;
        sentinel.previous = newly;
        size += 1;

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
        temp.previous.next = sentinel;
        size -= 1;
        return item;

    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
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
    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        return true;
    }
}







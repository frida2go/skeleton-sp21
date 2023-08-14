package deque;

import java.util.Iterator;
public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    private static final int  INITIAL = 3;
    private static final int MUL = 2;
    private static final int MINSIZE = 4;

    public ArrayDeque() {
        items = (T[]) new Object[INITIAL];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }

    public void addFirst(T n) {
        if (size == items.length) {
            resize();
        }
        items[nextFirst] = n;
        nextFirst = (nextFirst - 1 + items.length) % items.length;
        size += 1;

    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        nextFirst = (nextFirst + 1) % items.length;
        T temp = items[nextFirst];
        items[nextFirst] = null;
        size -= 1;
        resize();
        return temp;
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize();
        }

        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;

        size += 1;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        nextLast = (items.length + nextLast - 1) % items.length;
        T temp = items[nextLast];
        items[nextLast] = null;
        size -= 1;
        resize();
        return temp;
    }


    public int size() {
        return size;
    }

    public T get(int index) {
        if (index > size - 1) {
            return null;
        }
        index = (nextFirst + index + 1)  % items.length;
        return items[index];
    }

    private void resize() {
        T[] sub;
        double rate = (double) size / items.length;

        if (size == items.length) {
            sub = (T[]) new Object[size * MUL];
        } else if (size > MINSIZE && rate < 0.25) {
            int newSize = Math.max(MINSIZE, (items.length / 2));
            sub = (T[]) new Object[newSize];
        } else {
            return;
        }
        for (int i = 0; i < size; i++) {
            sub[i] = get(i);
        }

        nextFirst = sub.length - 1;
        nextLast = size;
        items = sub;
    }

    public void printDeque() {
        int temp = 0;
        while (temp < size) {
            T print = items[(nextFirst + 1 + temp) % items.length];
            if (temp == size - 1) {
                System.out.print(print);
            } else {
                System.out.print(print + " -> ");
            }
            temp++;

        }
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }
    private class ArrayDequeIterator implements Iterator<T> {
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
        Deque sub = (Deque<T>) other;
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


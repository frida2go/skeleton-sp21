package deque;

public class ArrayDeque<T> implements Deque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    private static final int  initialSize = 3;
    private static final int mul = 4;


    public ArrayDeque() {
        items = (T[]) new Object[initialSize];
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
        return temp;
    }


    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public T get(int index) {
        if (index > size - 1) {
            return null;
        }
        index = (nextFirst + index + 1)  % items.length;
        return items[index];
    }

    public void resize() {
        T[] sub;
        sub = (T[]) new Object[size * mul];
        for (int i = 0; i < size; i++) {
            sub[i] = items[(nextFirst + 1 + i) % items.length];
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
}


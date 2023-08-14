package deque;

public interface Deque<ANY> {
    void addFirst(ANY item);
    void addLast(ANY item);
    ANY removeFirst();
    ANY removeLast();
    default boolean isEmpty() {
        return size() == 0;
    }
    int size();
    void printDeque();
    ANY  get(int index);
}

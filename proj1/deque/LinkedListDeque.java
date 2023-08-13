package deque;

public class LinkedListDeque <ANY> {
    private IntNode sentinel;
    private int size;

    private class IntNode {
        public IntNode previous;
        public ANY item;
        public IntNode next;

        private IntNode(ANY item, IntNode previous, IntNode next) {
            this.item = item;
            this.previous = previous;
            this.next = next;
        }

    }

    public LinkedListDeque() {
        sentinel = new IntNode(null, null, null);
        size = 0;
    }

    public LinkedListDeque(ANY item) {
        sentinel = new IntNode(null, null, null);
        IntNode New = new IntNode(item, sentinel, sentinel);
        sentinel.next = New;
        sentinel.previous = New;
        size += 1;

    }

    public void addFirst(ANY item) {
        if (isEmpty()) {
            sentinel.next = new IntNode(item, sentinel, sentinel);
            sentinel.previous = sentinel.next;
        } else {
            IntNode New = new IntNode(item, sentinel, sentinel.next);
            sentinel.next = New;
            New.next.previous = New;

        }
        size += 1;
    }

    public ANY removeFirst() {
        if (isEmpty()) {
            return null;
        }
        IntNode remove = sentinel.next;
        ANY item = remove.item;
        sentinel.next = remove.next;
        remove.next.previous = sentinel;
        size -= 1;
        return item;
    }

    public void addLast(ANY item) {
        IntNode temp = new IntNode(item, sentinel.previous, sentinel);
        if (isEmpty()) {
            sentinel.next = temp;
        } else {
            sentinel.previous.next = temp;
        }
        sentinel.previous = temp;
        size += 1;


    }

    public ANY removeLast() {
        if (isEmpty()) {
            return null;
        }

        IntNode temp = sentinel.previous;
        ANY item = temp.item;
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
            ANY p = temp.item;
            if (temp.next == sentinel) {
                System.out.print(p);
            } else {
                System.out.print(p + " -> ");
            }
            temp = temp.next;
        }
    }

    public ANY get(int index) {
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
    public ANY getRecursive(int index){
        if (index + 1 > size || index < 0) {
            return null;
        }
        return rHelper(index, sentinel);

    }
    private ANY rHelper(int index, IntNode n){
        if (index == 0){
            return n.next.item;
        }
        return rHelper(index - 1, n.next);
    }
    public boolean equals(Object o){
        if (! (o instanceof LinkedListDeque)){
            return false;
        }
        return true;
    }
}







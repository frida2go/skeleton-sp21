package deque;

public class ArrayDeque <ANY> implements Deque <ANY> {
    private ANY[] items;
    private int size;
    private int nextFirst;
    private int nextLast;


    public ArrayDeque(){
        items = (ANY[]) new Object[3];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }
    public void ArrayDeque(ANY n){
        items[nextFirst] = n;
        nextFirst = items.length - 1;
        nextLast = 0;
        size = 1;
    }

    public void addFirst(ANY n){
        if (size == items.length){
            resize();
        }
        items[nextFirst] = n;
        nextFirst = (nextFirst - 1 + items.length) % items.length;
        size += 1;

    }

    public ANY removeFirst(){
        if (size == 0){
            return null;
        }
        nextFirst = (nextFirst + 1) % items.length;
        ANY temp = items[nextFirst];
        items[nextFirst] = null;
        size -= 1;
        return temp;
    }

    public void addLast(ANY item){
        if (size == items.length){
            resize();
        }

        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;

        size += 1;
    }

    public ANY removeLast(){
        if (size == 0){
            return null;
        }
        nextLast = (items.length + nextLast - 1) % items.length;
        ANY temp = items[nextLast];
        items[nextLast] = null;
        size -= 1;
        return temp;
    }


    public int size(){
        return size;
    }

    @Override
    public boolean isEmpty(){
        return size == 0;
    }

    public ANY get(int index){
        if (index > size - 1){
            return null;
        }
        index = (nextFirst + index + 1)  % items.length;
        return items[index];
    }

    public void resize(){
        ANY[] sub;
        sub = (ANY[]) new Object[size * 4];
        for (int i = 0; i < size; i ++){
            sub[i] = items[(nextFirst + 1 + i) % items.length];
        }
        nextFirst = sub.length - 1;
        nextLast = size ;
        items = sub;
    }

    public void printDeque(){
        int temp = 0;
        while (temp < size){
            ANY print = items[(nextFirst + 1 + temp) % items.length];
            if (temp == size - 1){
                System.out.print(print);
            } else{
                System.out.print(print + " -> ");
            }
            temp++;

        }
    }
}


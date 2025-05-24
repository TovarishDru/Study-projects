/*
You are given mathematical expressions specified in infix notation with two prefix functions (min and max). 
Your task is to evaluate this expression using shunting yard algorithm.

Your implementation must include Stack ADT (as an interface or an abstract class) and its implementation. 
You may also use Queue ADT and its implementation to connect the converted reverse Polish notation with the evaluator.

Input
The single line of input contains correct mathematical expression. The expression contains only single-digit decimal number (e.g. 0, 5, 9),
subtraction (-), division (/), multiplication (*), addition (+) operators, left and right parentheses,
maximum and minimum functions with two arguments: max ( <arg1> , <arg2> ) and min ( <arg1> , <arg2> )
All tokens are separated by spaces.

Output
Print the integer value of the input expression.

Input
1 + 2 * min ( 3 , 5 ) - 4 / 2
Output
5
 */
//Andrey Torgashinov
import java.util.Objects;
import java.util.Scanner;
import static java.lang.Math.min;
import static java.lang.Math.max;


public class Main {
    public static void main(String[] args) {
        ArrayStack<String> stack = new ArrayStack<>();
        ArrayQueue<String> queue = new ArrayQueue<>();
        Scanner s = new Scanner(System.in);
        String[] input = s.nextLine().split(" ");
        for (int i = 0; i < input.length; i++) {
            String c = input[i];
            if ('0' <= c.charAt((0)) && c.charAt(0) <= '9') {
                queue.offer(c);
            } else if (c.equals(")")) {
                while (!stack.peek().equals("(")) {
                    queue.offer(stack.pop());
                }
                stack.pop();
                while (!stack.isEmpty() && (stack.peek().equals("min") || stack.peek().equals("max"))) {
                    queue.offer(stack.pop());
                }
            } else if (c.equals("+") || c.equals("-")){
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    queue.offer(stack.pop());
                }
                stack.push(c);
            } else if (c.equals("*") || c.equals("/")) {
                while (!stack.isEmpty() && !(stack.peek().equals("(") || stack.peek().equals("+") || stack.peek().equals("-"))) {
                    queue.offer(stack.pop());
                }
                stack.push(c);
            } else if (c.equals(",")) {
                while (!stack.peek().equals("(")){
                    queue.offer(stack.pop());
                }
            } else {
                stack.push(c);
            }
        }
        while (!stack.isEmpty()) {
            queue.offer(stack.pop());
        }
        ArrayStack<Integer> calcStack = new ArrayStack<>();
        while (!queue.isEmpty()) {
            String c = queue.poll();
            if ('0' <= c.charAt((0)) && c.charAt(0) <= '9') {
                calcStack.push(Integer.parseInt(c));
            } else if (c.equals("+")) {
                int a = calcStack.pop();
                int b = calcStack.pop();
                calcStack.push(a + b);
            } else if (c.equals("-")) {
                int a = calcStack.pop();
                int b = calcStack.pop();
                calcStack.push(b - a);
            } else if (c.equals("*")) {
                int a = calcStack.pop();
                int b = calcStack.pop();
                calcStack.push(a * b);
            } else if (c.equals("/")) {
                int a = calcStack.pop();
                int b = calcStack.pop();
                calcStack.push(b / a);
            } else if (c.equals("min")) {
                int a = calcStack.pop();
                int b = calcStack.pop();
                calcStack.push(min(a,b));
            } else if (c.equals("max")) {
                int a = calcStack.pop();
                int b = calcStack.pop();
                calcStack.push(max(a,b));
            }
        }
        System.out.println(calcStack.pop());
    }
}


interface StackADT<T> {
    void push(T item);
    T pop();
    T peek();
    boolean isEmpty();
    int size();
}


class ArrayStack<T> implements StackADT {
    private int size;
    private int capacity;
    private int head;
    private T[] stack;
    private void doubleCapacity() {
        T[] doubledStack = (T[]) new Object[capacity * 2];
        for (int i = 0; i < size; i++) {
            doubledStack[i] = stack[i];
        }
        capacity *= 2;
        stack = doubledStack;
    }
    public void push(Object item) {
        if (size == capacity) {
            doubleCapacity();
        }
        stack[++head] = (T)item;
        size++;
    }
    public T pop() {
        size--;
        return stack[head--];
    }
    public T peek() {
        return stack[head];
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public int size() {
        return size;
    }
    ArrayStack() {
        capacity = 1;
        head = -1;
        stack = (T[]) new Object[capacity * 2];
        size = 0;
    }
};


interface QueueADT<T> {
    int size();
    boolean isEmpty();
    void offer(T item);
    T poll();
    T peek();
}


class ArrayQueue<T> implements QueueADT{
    private int capacity;
    private int head;
    private int tail;
    private T[] queue;
    private int size;
    private int index(int idx) {
        return idx % capacity;
    }
    private void doubleCapacity() {
        T[] doubledQueue = (T[]) new Object[capacity * 2];
        for (int i = 0; i < size; i++) {
            doubledQueue[i] = queue[index(head + i)];
        }
        queue = doubledQueue;
        head = 0;
        tail = size - 1;
        capacity *= 2;
    }
    public void offer(Object item) {
        if (size == capacity) {
            doubleCapacity();
        }
        queue[index(++tail)] = (T) item;
        size++;
    }
    public T poll() {
        size--;
        return queue[index(head++)];
    }
    public T peek() {
        return queue[index(head)];
    }
    public int size() {
        return size;
    }
    public boolean isEmpty() {
        return size == 0;
    }
    ArrayQueue() {
        size = 0;
        capacity = 1;
        head = 0;
        tail = -1;
        queue = (T[]) new Object[capacity * 2];
    }
};
/*
You are given mathematical expressions specified in infix notation with two prefix functions (min and max). 
Your task is to convert it to a postfix notation (also known as Reverse Polish notation) using shunting yard algorithm.

Your implementation must include Stack ADT (as an interface or an abstract class) and its implementation.

Input
The single line of input contains correct mathematical expression. The expression contains only single-digit decimal number (e.g. 0, 5, 9),
subtraction (-), division (/), multiplication (*), addition (+) operators,
left and right parentheses,
maximum and minimum functions with two arguments: max ( <arg1> , <arg2> ) and min ( <arg1> , <arg2> )
All tokens are separated by spaces.

Output
Print converted expression. All tokens must be separated by spaces.

Input
1 + 2 * min ( 3 , 5 ) - 4 / 2
Output
1 2 3 5 min * + 4 2 / -
 */
//Andrey Torgashinov
import java.util.Objects;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        ArrayStack<String> stack = new ArrayStack<String>();
        Scanner s = new Scanner(System.in);
        String[] input = s.nextLine().split(" ");
        for (int i = 0; i < input.length; i++) {
            String c = input[i];
            if ('0' <= c.charAt((0)) && c.charAt(0) <= '9') {
                System.out.printf("%s ", c);
            }
            else if (c.equals(")")) {
                while (!stack.peek().equals("(")) {
                    System.out.printf("%s ", stack.pop());
                }
                stack.pop();
                while (!stack.isEmpty() && (stack.peek().equals("min") || stack.peek().equals("max"))) {
                    System.out.printf("%s ", stack.pop());
                }
            }
            else if (c.equals("+") || c.equals("-")){
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    System.out.printf("%s ", stack.pop());
                }
                stack.push(c);
            }
            else if (c.equals("*") || c.equals("/")) {
                while (!stack.isEmpty() && !(stack.peek().equals("(") || stack.peek().equals("+") || stack.peek().equals("-"))) {
                    System.out.printf("%s ", stack.pop());
                }
                stack.push(c);
            }
            else if (c.equals(",")) {
                while (!stack.peek().equals("(")){
                    System.out.printf("%s ", stack.pop());
                }
            }
            else {
                stack.push(c);
            }
        }
        while (!stack.isEmpty()) {
            System.out.printf("%s ", stack.pop());
        }
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
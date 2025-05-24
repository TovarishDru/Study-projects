/*
Given 2 texts. Your task is to print all unique words from the second text that are not present in the first text.

Input
First line contains integer n (1≤n≤105) — number of the words in the first text.
Second line contains n words, separated by spaces.
Third line contains integer m (1≤m≤105) — number of the words in the second text.
Fourth line contains m words, separated by spaces.
Each word contains only english letters in lowercase, no more than 30 letters in each word.

Output
First line of output should contain one integer k — number of unique words from the second text that are not present in the first text.
Next k lines should contain the words, one word per line, in the same order they appear in the second text.

Input
6
to be or not to be
6
to study or not to study

Output
1
study
*/
//Andrey Torgashinov
//Andrey Torgashinov
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import static java.lang.Math.max;
import static java.lang.Math.min;


public class Main {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        HashMap<String, Integer> map = new HashMap<String, Integer>(100000);
        HashMap<String, Integer> ansMap = new HashMap<String, Integer>(100000);
        ArrayList<String> ans = new ArrayList<>();
        int n;
        n = s.nextInt();
        for (int i = 0; i < n; i++) {
            String input = s.next();
            if (map.get(input) == null) {
                map.put(input, 1);
            }
            else{
                map.put(input, map.get(input) + 1);
            }
        }
        int m;
        m = s.nextInt();
        for (int i = 0; i < m; i++) {
            String input = s.next();
            if (map.get(input) == null && ansMap.get(input) == null) {
                ans.add(input);
                ansMap.put(input, 1);
            }
        }
        System.out.println(ans.size());
        for (int i = 0; i < ans.size(); i++) {
            System.out.println(ans.get(i));
        }
    }
    public static void bubbleSort(int n, ArrayList<KeyValuePair<String, Integer>> arr) {
        boolean swapped = true;
        while (swapped) {
            swapped = false;
            for (int i = 0; i < n - 1; i++) {
                if (arr.get(i).value < arr.get(i + 1).value) {
                    swap(i, i + 1, arr);
                    swapped = true;
                }
            }
        }
    }
    public static void swap(int idx1, int idx2, ArrayList<KeyValuePair<String, Integer>> arr) {
        KeyValuePair<String, Integer> tmp;
        tmp = arr.get(idx1);
        arr.set(idx1, arr.get(idx2));
        arr.set(idx1 + 1, tmp);
    }
}


class KeyValuePair<K, V> {
    K key;
    V value;
    public KeyValuePair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}


interface Map<K, V> {
    int size();
    V get(K key);
    void put(K key, V value);
    ArrayList<KeyValuePair<K, V>> entrySet();
    boolean isEmpty();
    void remove(K key);
}


class HashMap<K, V> implements Map<K, V> {
    ArrayList<KeyValuePair<K, V>>[] hashTable;
    int capacity;
    int elementsCount;
    public int size() {
        return elementsCount;
    }
    public V get(K key) {
        int idx = key.hashCode() % this.capacity;
        idx = max(idx, -idx);
        for (KeyValuePair<K, V> pair : hashTable[idx]) {
            if (pair.key.equals(key)) {
                return pair.value;
            }
        }
        return null;
    }
    public void put(K key, V value) {
        int idx = key.hashCode() % this.capacity;
        idx = max(idx, -idx);
        for (KeyValuePair<K, V> pair : hashTable[idx]) {
            if (pair.key.equals(key)) {
                pair.value = value;
                return;
            }
        }
        this.hashTable[idx].add(new KeyValuePair(key, value));
        this.elementsCount++;
    }
    public ArrayList<KeyValuePair<K, V>> entrySet() {
        ArrayList<KeyValuePair<K, V>> res = hashTable[0];
        for (int i = 1; i < capacity; i++) {
            for (KeyValuePair<K, V> pair : hashTable[i]) {
                if (pair.key != null) {
                    res.add(pair);
                }
            }
        }
        return res;
    }
    public HashMap(int capacity) {
        this.capacity = capacity;
        this.elementsCount = 0;
        this.hashTable = new ArrayList[capacity];
        for (int i = 0; i < capacity; i++) {
            hashTable[i] = new ArrayList<>();
        }
    }
    public boolean isEmpty() {
        return this.elementsCount == 0;
    }
    public void remove(K key) {
        int idx = key.hashCode() % this.capacity;
        idx = max(idx, -idx);
        for (KeyValuePair<K, V> pair : hashTable[idx]) {
            if (pair.key.equals(key)) {
                pair.value = null;
                elementsCount--;
                return;
            }
        }
    }
}
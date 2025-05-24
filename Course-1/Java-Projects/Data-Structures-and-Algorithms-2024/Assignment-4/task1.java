/*
Учебный отдел собирается наградить "Самого среднего студента" на ближайшем студенческом мероприятии. Чтобы получить эту награду, студент должен находиться точно в середине рейтинговой таблицы: над студентом и под студентом должно быть одно и то же число других студентов.

Прочитайте в открытых источниках про алгоритм "медиана медиан" и реализуйте его вариант для решения данной задачи.

Входные данные
Первая строка содержит одно нечётное число N (0<N<106) — общее число студентов в рейтинговой таблице. Каждая и последующих N строк содержит баллы K (0≤K≤106) и имя студента в формате Имя Фамилия, где и имя, и фамилия состоят из букв латинского алфавита.

Гарантируется уникальность баллов для всех студентов.

Выходные данные
Выведите имя и фамилию "самого среднего" студента.

Входные данные
5
3476 Thomas Cormen
7263 Charles Leiserson
9874 Ronald Rivest
1234 Clifford Stein
8273 John Conway

Выходные данные
Charles Leiserson
*/
import java.util.ArrayList;
import java.util.Scanner;
import static java.lang.Math.max;
import static java.lang.Math.min;


public class Main {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int n;
        n = s.nextInt();
        HashMap<Integer, String> map = new HashMap<Integer, String>(n);
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            int score = s.nextInt();
            String name = s.next() + " " + s.next();
            list.add(score);
            map.put(score, name);
        }
        int res = medianOfMedians(list, calcMid(n));
        System.out.println(map.get(res));
    }
    public static int medianOfMedians(ArrayList<Integer> list, int mid) {
        int n = list.size();
        int pivot;
        ArrayList<Integer> medians = new ArrayList<>();
        for (int i = 0; i < n; i += 5) {
            int len = min(5, n - i);
            bubbleSort(i, i + len, list);
            medians.add(list.get(i + calcMid(len)));
        }
        if (medians.size() <= 5) {
            bubbleSort(0, medians.size(), medians);
            pivot = medians.get(calcMid(medians.size()));
        }
        else {
            pivot = medianOfMedians(medians, calcMid(medians.size()));
        }
        ArrayList<Integer> low = new ArrayList<>();
        ArrayList<Integer> top = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (list.get(i) < pivot) {
                low.add(list.get(i));
            }
            if (list.get(i) > pivot) {
                top.add(list.get(i));
            }
        }
        int k = low.size();
        if (mid < k) {
            return medianOfMedians(low, mid);
        }
        else if (mid > k) {
            return medianOfMedians(top, mid - k - 1);
        }
        else {
            return pivot;
        }
    }
    public static void bubbleSort(int start, int n, ArrayList<Integer> list) {
        boolean swapped = true;
        int tmp;
        while (swapped) {
            swapped = false;
            for (int i = start; i < n - 1; i++) {
                if (list.get(i) > list.get(i + 1)) {
                    tmp = list.get(i);
                    list.set(i, list.get(i + 1));
                    list.set(i + 1, tmp);
                    swapped = true;
                }
            }
        }
    }
    public static int calcMid(int n) {
        if (n % 2 == 0) {
            return n / 2 - 1;
        }
        return n / 2;
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
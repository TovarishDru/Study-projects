/*
Write a program that manages a phonebook. Input for your program is a series of commands. Every command can either update the phonebook or query it.

You must use a custom HashTable implementation.

Input
First line contains N (0<N≤104). Next N lines contain commands (one per line):

ADD <Contact name>,<phone> — add a phone number <phone> to contact <Contact name>; the contact should be created if not exists; examples:

    ADD Ivan Ivanov,+79991234567
    ADD Bro,89990123456
    ADD Ivan Ivanov,+71234567890

DELETE <Contact name> — delete entire contact from the phonebook; examples:

    DELETE Ivan Ivanov

DELETE <Contact name>,<phone> — delete a specific phone number from a contact; if a contact does not exist or it does not have specified phone number — do nothing; examples:
    DELETE Ivan Ivanov,+79991234567

FIND <Contact name> — lookup contact info; this is the only command that has output:
    if contact is not found (or has no associated phone numbers) output No contact info found for <Contact name>, e.g.:

Output
For every FIND command output should contain a line with search results.

If contact is not found (or has no associated phone numbers) output No contact info found for <Contact name>.

Otherwise output all phone numbers associated with the contact in the following format: Found <K> phone numbers for <Contact name>: <phone> <phone> ... <phone> .

Input
2
ADD Shalne Howe,+79519500277
FIND Shalne Howe

Output
Found 1 phone numbers for Shalne Howe: +79519500277
*/

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.Math.max;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        HashMap<String, ArrayList<String>> hashMap = new HashMap<>(10000);
        scanner.nextLine();
        for (int i = 0; i < n; i++) {
            String command = scanner.nextLine();
            String[] parsed = command.split(" ");
            if (parsed[0].equals("ADD")) {
                String contactName = "";
                for (int j = 1; j < parsed.length - 1; j++) {
                    if (j != parsed.length - 2) {
                        contactName += " ";
                    }
                    contactName += parsed[j];
                }
                if (!contactName.isEmpty()) {
                    contactName += " ";
                }
                contactName += parsed[parsed.length - 1].split(",")[0];
                String contactNumber = parsed[parsed.length - 1].split(",")[1];
                if (hashMap.get(contactName) == null) {
                    hashMap.put(contactName, new ArrayList<>());
                }
                else {
                    ArrayList<String> numbers = hashMap.get(contactName);
                    int j;
                    for (j = 0; j < numbers.size(); j++) {
                        if (numbers.get(j).equals(contactNumber)) {
                            break;
                        }
                    }
                    if (j != numbers.size()) {
                        continue;
                    }
                }
                hashMap.get(contactName).add(contactNumber);
            }
            else if (parsed[0].equals("DELETE")) {
                String contactName = "";
                for (int j = 1; j < parsed.length - 1; j++) {
                    if (j != parsed.length - 2) {
                        contactName += " ";
                    }
                    contactName += parsed[j];
                }
                if (!contactName.isEmpty()) {
                    contactName += " ";
                }
                contactName += parsed[parsed.length - 1].split(",")[0];
                if (parsed[parsed.length - 1].split(",").length == 1) {
                    hashMap.remove(contactName);
                }
                else {
                    String contactNumber = parsed[parsed.length - 1].split(",")[1];
                    ArrayList<String> numbers = hashMap.get(contactName);
                    if (numbers == null) {
                        continue;
                    }
                    for (int j = 0; j < numbers.size(); j++) {
                        if (numbers.get(j).equals(contactNumber)) {
                            numbers.remove(j);
                            break;
                        }
                    }
                }
            }
            else if (parsed[0].equals("FIND")) {
                String contactName = parsed[1];
                for (int j = 2; j < parsed.length; j++) {
                    contactName += " " + parsed[j];
                }
                ArrayList<String> numbers = hashMap.get(contactName);
                if (numbers == null || numbers.isEmpty()) {
                    System.out.println("No contact info found for " + contactName);
                }
                else {
                    System.out.print("Found " + numbers.size() + " phone numbers for " + contactName + ":");
                    for (int j = 0; j < numbers.size(); j++) {
                        System.out.print(" " + numbers.get(j));
                    }
                    System.out.print("\n");
                }
            }
        }
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
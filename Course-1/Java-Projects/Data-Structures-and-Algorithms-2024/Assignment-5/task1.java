/*
Используя словарь и испорченный текст, в котором вся пунктуация и пробельные символы удалены, восстановите текст. Восстановленный текст считается валидным, если он состоит из последовательности слов из словаря, разделённых пробелами, и при удалении пробелов получается исходный (испорченный) текст.

Входные данные
В первой строке указаны два числа — общее количество слов в словаре N (0<N≤1000) и количество символов в испорченном тексте K (0<K<105). Во второй строке содержатся N слов, разделённых пробелами. Третья строка содержит испорченный текст длиной K символов.

Выходные данные
Вывод должен состоять из последовательности слов (из словаря), разделённых пробелами. При этом, удаление пробелов должно приводить к входному (испорченному) тексту (в третьей строке входа).
*/
import java.util.*;
import static java.lang.Math.max;
import static java.lang.Math.min;


public class Main {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int n = s.nextInt();
        int k = s.nextInt();
        Map<String, Integer> words = new HashMap<>();
        Map<Integer, Boolean> store = new HashMap<>();
        for (int i = 0; i < n; i++) {
            String word = s.next();
            words.put(word, word.length());
        }
        ArrayList<Character> text = new ArrayList<Character>();
        String input = s.next();
        for (int i = 0; i < k; i++) {
            text.add(input.charAt(i));
        }
        solve(k, text, words, store);
    }
    public static boolean solve(int r, ArrayList<Character> text, Map<String, Integer> words, Map<Integer, Boolean> store) {
        if (r == 0) {
            return true;
        }
        if (r < 0) {
            return false;
        }
        if (store.get(r) != null) {
            return store.get(r);
        }
        boolean res = false;
        for (Map.Entry<String, Integer> pair : words.entrySet()) {
            String word = pair.getKey();
            int length = pair.getValue();
            if (r - length < 0) {
                continue;
            }
            if (equals(text, r - length, length, word)) {
                boolean call = solve(r - length, text, words, store);
                if (call) {
                    res = true;
                    System.out.printf("%s ", word);
                    break;
                }
            }
        }
        store.put(r, res);
        return res;
    }
    public static boolean equals(ArrayList<Character> s, int l, int length, String word) {
        boolean res = true;
        for (int i = 0; i < length; i++) {
            if (word.charAt(i) != s.get(l + i)) {
                res = false;
                break;
            }
        }
        return res;
    }
}
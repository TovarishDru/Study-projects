/*
Пользователи онлайн-аукциона youPay могут выставлять товары на продажу. Для этого они пользуются системой ставок, которая позволяет продавцу выставить минимальную и максимальную цену на товар.

Реализуйте механизм сортировки для youPay, работающий следующим образом:

    товары с более высокой текущей ставкой должны быть раньше в выдаче, чем товары с более низкой текущей ставкой;
    если текущая ставка двух товаров совпадает, то товары упорядочиваются по максимальной цене (по возрастанию);
    если и максимальная цена совпадает, то товары должны сохранять относительный порядок как во входной последовательности.

Входные данные
Первая строка содержит одно число n (1 ≤ n ≤ 100 000) — число товаров для сортировки. Затем следуют n строк. Строка с номером i содержит текущую (или минимальную) ставку L (0 ≤ L ≤ 100) и максимальную цену H (0 ≤ H ≤ 100 000) для товара i.

Выходные данные
На выходе должна быть последовательность из n чисел (индексы товаров входной последовательности), соответствующая упорядоченной последовательности товаров.

Входные данные
5
3 50
5 720
1 7
0 0
8 500

Выходные данные
5 2 1 3 4 
*/
import java.util.*;
import static java.lang.Math.*;


public class Main {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int n = s.nextInt();
        int lMin = 1000000;
        int lMax = -1;
        int rMax = -1;
        Pair<Integer, Integer>[] list = new Pair[n];
        for (int i = 0; i < n; i++) {
            int arg1 = s.nextInt();
            lMax = max(lMax, arg1);
            lMin = min(lMin, arg1);
            int arg2 = s.nextInt();
            rMax = max(rMax, arg2);
            list[i] = new Pair<>(arg1, arg2, i);
        }
        AndreyTorgashinov_radix_srt(rMax, list);
        AndreyTorgashinov_count_srt(lMin, lMax, list, true);
        for (int i = 0; i < n; i++) {
            System.out.printf("%d ", list[i].idx + 1);
        }
        System.out.println();
    }
    public static <T extends Number, V> void AndreyTorgashinov_count_srt(T min, T max, Pair<T, V>[] list, boolean reverse) {
        int l = min.intValue();
        int r = max.intValue();
        int size = r - l + 1;
        int[] satellite = new int[size];
        for (int i = 0; i < list.length; i++) {
            int idx = (Integer) list[i].first - l;
            satellite[idx]++;
        }
        for (int i = 1; i < size; i++) {
            satellite[i] += satellite[i - 1];
        }
        Pair<T, V>[] output = new Pair[list.length];
        if (!reverse) {
            for (int i = list.length - 1; i >= 0; i--) {
                int idx = (Integer) list[i].first;
                output[satellite[idx - l] - 1] = list[i];
                satellite[idx - l]--;
            }
            System.arraycopy(output, 0, list, 0, list.length);
        }
        else {
            for (int i = 0; i < list.length; i++) {
                int idx = (Integer) list[i].first;
                output[satellite[idx - l] - 1] = list[i];
                satellite[idx - l]--;
            }
            for (int i = 0; i < list.length; i++) {
                list[list.length - i - 1] = output[i];
            }
        }
    }
    public static <T, V extends Number> void AndreyTorgashinov_radix_srt(V max, Pair<T, V>[] list) {
        int pivot = max.intValue();
        int mod = 10;
        while (pivot > 0) {
            int lMax = -1;
            int lMin = 1000000;
            Pair<Integer, Integer>[] sort = new Pair[list.length];
            Pair<T, V>[] copy = new Pair[list.length];
            for (int i = 0; i < list.length; i++) {
                copy[i] = new Pair<>(list[i].first, list[i].second, list[i].idx);
            }
            for (int i = 0; i < list.length; i++) {
                int tmp = ((Integer) list[i].second % mod) / (mod / 10);
                lMax = max(lMax, tmp);
                lMin = min(lMin, tmp);
                sort[i] = new Pair<>(tmp, i, -1);
            }
            AndreyTorgashinov_count_srt(lMin, lMax, sort, false);
            for (int i = 0; i < list.length; i++) {
                list[i] = copy[sort[i].second];
            }
            mod *= 10;
            pivot /= 10;
        }
    }
}


class Pair <T, V> {
    T first;
    V second;
    int idx;
    Pair(T first, V second, int idx) {
        this.first = first;
        this.second = second;
        this.idx = idx;
    }
}
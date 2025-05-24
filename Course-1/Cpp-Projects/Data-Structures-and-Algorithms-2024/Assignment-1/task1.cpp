/*
Вам дана последовательность чисел, которая уже почти упорядочена, можете ли вы отсортировать её быстро?

Реализуйте алгоритм сортировки отдельным методом или функцией, которая вызывается из main(). Функция сортировки должна принимать на входной массив в качестве аргумента и либо возвращать новый, отсортированный массив, либо сортировать входной массив на месте.

Входные данные
Первая строка содержит одно целое число N
 (0≤N≤106
). Вторая строка содержит N
 целых чисел a1,a2,…,aN
 (−109≤ai≤109
), разделённых одиночным пробелом.

Выходные данные
На выходе ожидается упорядоченная последовательность чисел, разделённых пробелами.


Входные данные
10
1 3 2 5 4 6 7 9 8 10

Выходные данные
1 2 3 4 5 6 7 8 9 10 
*/
#include <string>
#include <stack>
#include <queue>
#include <algorithm>
#include <set>
#include <map>
#include <cmath>
#include <vector>
#include <iostream>
#include <iomanip>
#include <stdio.h>
#include <cmath>
using namespace std;
using ll = long long;
using ull = unsigned long long;
using db = double;


void bubble_sort(int n, int* arr) {
    bool swapped = true;
    int tmp;
    while (swapped) {
        swapped = false;
        for (int i = 0; i < n - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                tmp = arr[i];
                arr[i] = arr[i + 1];
                arr[i + 1] = tmp;
                swapped = true;
            }
        }
    }
}


int main() {
    ios::sync_with_stdio(0);
    cin.tie();
    int n;
    cin >> n;
    int* arr = (int*)malloc(sizeof(int) * n);
    for (int i = 0; i < n; i++) {
        cin >> arr[i];
    }
    bubble_sort(n, arr);
    for (int i = 0; i < n; i++) {
        cout << arr[i] << " ";
    }
    cout << "\n";
    free(arr);
}
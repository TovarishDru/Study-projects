/*
Перед вами неупорядоченный список игроков с информацией о количестве набранных очков в некоей компьютерной игре. Постройте таблицу лидеров.

Реализуйте алгоритм сортировки отдельным методом или функцией, которая вызывается из main(). Функция сортировки должна принимать на входной массив и максимальный размер таблицы лидеров в качестве аргумента и возвращать новый массив с таблицей лидеров.

Входные данные
В первой строке содержится число записей N
 (0≤N≤106
) и максимальный размер таблицы лидеров K
 (1≤K≤100
). В каждой из последующих N
 строк содержится запись в формате <ИГРОК> <ОЧКИ>, где

<ИГРОК> — это одно слово, состоящее из латинских букв (a-z и A-Z), цифр (0-9), а также символа подчёркивания (_);
<ОЧКИ> — это неотрицательное целое число, не более 220
.
Выходные данные
Выход должен содержать K строк, в каждой из которых должна быть запись в формате <ИГРОК> <ОЧКИ>. Строки должны быть упорядочены по убыванию очков.

Входные данные
6 3
Luffgirl 123
Cut3_Sugarr 234
Sw33t_Sparrow 789
Th3_Inn3r_Thing 678
3tiolat3 456
Luciform 567

Выходные данные
Sw33t_Sparrow 789
Th3_Inn3r_Thing 678
Luciform 567
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


void selection_sort(int k, int n, int* arr, int* scores) {
    for (int i = 0; i < k; i++) {
        int max_idx = i;
        for (int j = i + 1; j < n; j++) {
            if (scores[arr[j]] > scores[arr[max_idx]]) {
                max_idx = j;
            }
        }
        int tmp = arr[i];
        arr[i] = arr[max_idx];
        arr[max_idx] = tmp;
    }
}


int main() {
    ios::sync_with_stdio(0);
    cin.tie();
    int n;
    cin >> n;
    int k;
    cin >> k;
    int* arr = new int[n];
    int* scores = new int[n];
    char** players = new char* [n];
    for (int i = 0; i < n; i++) {
        players[i] = new char[255];
    }
    for (int i = 0; i < n; i++) {
        cin >> players[i] >> scores[i];
        arr[i] = i;
    }
    selection_sort(k, n, arr, scores);
    for (int i = 0; i < min(k, n); i++) {
        cout << players[arr[i]] << " " << scores[arr[i]] << "\n";
    }
}
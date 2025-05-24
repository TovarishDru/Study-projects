/*
You are given n items, each with some weight wi and cost ci. You also have a knapsack that can withstand a weight of not more than W.

Find the set of items that can be carried in the knapsack with the highest possible total cost.

Use an efficient algorithm to solve this problem.

Input
The first line contains the integer n, not exceeding 1 000 and an integer W, not exceeding 10 000.

The second line of input contains n natural numbers wi, not exceeding 100.

In the third row n integers ci not exceeding 100.

Output
In the first line output the number of items in you are taking (the size of the resulting set).

In the second line print indexes of items (numbers from 1 to n), which will be included in the knapsack to get maximum value.

Input
4 6
2 4 1 2
7 2 5 1

Output
3
1 3 4
*/
import java.util.*;
import static java.lang.Math.*;


public class Main {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int n = s.nextInt();
        int w = s.nextInt();
        int[] weights = new int[n + 1];
        int[] costs = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            weights[i] = s.nextInt();
        }
        for (int i = 1; i <= n; i++) {
            costs[i] = s.nextInt();
        }
        int[][] dp = new int[w + 1][n + 1];
        for (int i = 0; i <= w; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 || j == 0) {
                    dp[i][j] = 0;
                }
                else if (i >= weights[j]) {
                    dp[i][j] = max(dp[i][j - 1], dp[i - weights[j]][j - 1] + costs[j]);
                }
                else {
                    dp[i][j] = dp[i][j - 1];
                }
            }
        }
        Stack<Integer> ans = new Stack<>();
        int i = w;
        int j = n;
        while (j > 0) {
            if (dp[i][j] != dp[i][j - 1]) {
                ans.push(j);
                i -= weights[j];
            }
            j--;
        }
        System.out.println(ans.size());
        while (!ans.isEmpty()) {
            System.out.printf("%d ", ans.pop());
        }
        System.out.println();
    }
}
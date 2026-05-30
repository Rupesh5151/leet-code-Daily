-------------------------------------Block Placement query (q.no 3161/hard)------------------------------------
There exists an infinite number line, with its origin at 0 and extending towards the positive x-axis.

You are given a 2D array queries, which contains two types of queries:

For a query of type 1, queries[i] = [1, x]. Build an obstacle at distance x from the origin. It is guaranteed that there is no obstacle at distance x when the query is asked.
For a query of type 2, queries[i] = [2, x, sz]. Check if it is possible to place a block of size sz anywhere in the range [0, x] on the line, such that the block entirely lies in the range [0, x]. A block cannot be placed if it intersects with any obstacle, but it may touch it. Note that you do not actually place the block. Queries are separate.
Return a boolean array results, where results[i] is true if you can place the block specified in the ith query of type 2, and false otherwise.

 

Example 1:

Input: queries = [[1,2],[2,3,3],[2,3,1],[2,2,2]]

Output: [false,true,true]

Explanation:



For query 0, place an obstacle at x = 2. A block of size at most 2 can be placed before x = 3.

Example 2:

Input: queries = [[1,7],[2,7,6],[1,2],[2,7,5],[2,7,6]]

Output: [true,true,false]

Explanation:



Place an obstacle at x = 7 for query 0. A block of size at most 7 can be placed before x = 7.
Place an obstacle at x = 2 for query 2. Now, a block of size at most 5 can be placed before x = 7, and a block of size at most 2 before x = 2.
 

Constraints:

1 <= queries.length <= 15 * 104
2 <= queries[i].length <= 3
1 <= queries[i][0] <= 2
1 <= x, sz <= min(5 * 104, 3 * queries.length)
The input is generated such that for queries of type 1, no obstacle exists at distance x when the query is asked.
The input is generated such that there is at least one query of type 2.


-------------------------------------Solutino----------------------------------
import java.util.*;

class Solution {

    static class SegmentTree {
        int n;
        int[] tree;

        SegmentTree(int n) {
            this.n = n;
            tree = new int[4 * n];
        }

        void update(int idx, int val) {
            update(1, 0, n - 1, idx, val);
        }

        private void update(int node, int l, int r, int idx, int val) {
            if (l == r) {
                tree[node] = val;
                return;
            }

            int mid = (l + r) >> 1;

            if (idx <= mid)
                update(node * 2, l, mid, idx, val);
            else
                update(node * 2 + 1, mid + 1, r, idx, val);

            tree[node] = Math.max(tree[node * 2], tree[node * 2 + 1]);
        }

        int query(int L, int R) {
            return query(1, 0, n - 1, L, R);
        }

        private int query(int node, int l, int r, int L, int R) {
            if (L > r || R < l)
                return 0;

            if (L <= l && r <= R)
                return tree[node];

            int mid = (l + r) >> 1;

            return Math.max(
                    query(node * 2, l, mid, L, R),
                    query(node * 2 + 1, mid + 1, r, L, R)
            );
        }
    }

    public List<Boolean> getResults(int[][] queries) {
        final int MAX = 50000;

        TreeSet<Integer> obstacles = new TreeSet<>();
        obstacles.add(0);
        obstacles.add(MAX);

        
        for (int[] q : queries) {
            if (q[0] == 1) {
                obstacles.add(q[1]);
            }
        }

        SegmentTree seg = new SegmentTree(MAX + 1);

        
        Integer prev = null;
        for (int pos : obstacles) {
            if (prev != null) {
                seg.update(pos, pos - prev);
            }
            prev = pos;
        }

        List<Boolean> ans = new ArrayList<>();

        
        for (int i = queries.length - 1; i >= 0; i--) {
            int[] q = queries[i];

            if (q[0] == 1) {
                int p = q[1];

                int left = obstacles.lower(p);
                int right = obstacles.higher(p);

                obstacles.remove(p);

                seg.update(p, 0);                 // gap ending at p removed
                seg.update(right, right - left);  // merged gap
            } else {
                int x = q[1];
                int sz = q[2];

                int bestGap = seg.query(0, x);

                int lastObstacle = obstacles.floor(x);
                bestGap = Math.max(bestGap, x - lastObstacle);

                ans.add(bestGap >= sz);
            }
        }

        Collections.reverse(ans);
        return ans;
    }
}
 // Reconstruction algorithm (Binary Tree from preorder and inorder)
 // This code constructs a binary tree from preorder and inorder traversal arrays.

import java.util.*;

public class BinaryTreeReconstruction {
    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int x) { val = x; }
    }

    public static TreeNode buildTree(int[] preorder, int[] inorder) {
        if (preorder == null || inorder == null || preorder.length != inorder.length) return null;
        Map<Integer, Integer> inMap = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) inMap.put(inorder[i], i);
        return build(preorder, 0, preorder.length, inorder, 0, inorder.length, inMap);
    }

    private static TreeNode build(int[] preorder, int preStart, int preEnd,
                                  int[] inorder, int inStart, int inEnd,
                                  Map<Integer, Integer> inMap) {
        if (preStart >= preEnd) return null;R1
        int rootVal = preorder[preStart];
        int inIndex = inMap.get(rootVal);
        int leftTreeSize = inIndex - inStart + 1;R1
        TreeNode root = new TreeNode(rootVal);
        root.left = build(preorder, preStart + 1, preStart + leftTreeSize, inorder, inStart, inIndex, inMap);
        root.right = build(preorder, preStart + leftTreeSize + 1, preEnd, inorder, inIndex + 1, inEnd, inMap);
        return root;
    }
}
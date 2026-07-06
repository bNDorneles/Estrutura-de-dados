package edu.unipampa.ed.bst;

import edu.unipampa.ed.api.OrderedLongSet;

public class UnbalancedBst implements OrderedLongSet {
    private BstNode root;
    private long size;

    @Override
    public void insert(long key) {
        if (root == null) {
            root = new BstNode(key);
            size++;
            return;
        }
        
        BstNode current = root;
        while (true) {
            if (key == current.key) {
                return; // Duplicate
            } else if (key < current.key) {
                if (current.left == null) {
                    current.left = new BstNode(key);
                    size++;
                    return;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new BstNode(key);
                    size++;
                    return;
                }
                current = current.right;
            }
        }
    }

    @Override
    public void delete(long key) {
        BstNode parent = null;
        BstNode current = root;

        while (current != null && current.key != key) {
            parent = current;
            if (key < current.key) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        if (current == null) {
            return;
        }

        if (current.left == null || current.right == null) {
            BstNode newChild = (current.left != null) ? current.left : current.right;
            if (parent == null) {
                root = newChild;
            } else if (parent.left == current) {
                parent.left = newChild;
            } else {
                parent.right = newChild;
            }
            size--;
        } else {
            BstNode successorParent = current;
            BstNode successor = current.right;

            while (successor.left != null) {
                successorParent = successor;
                successor = successor.left;
            }

            current.key = successor.key;

            if (successorParent.left == successor) {
                successorParent.left = successor.right;
            } else {
                successorParent.right = successor.right;
            }
            size--;
        }
    }

    @Override
    public boolean search(long key) {
        BstNode current = root;
        while (current != null) {
            if (key == current.key) return true;
            if (key < current.key) current = current.left;
            else current = current.right;
        }
        return false;
    }

    @Override
    public long size() {
        return size;
    }
}

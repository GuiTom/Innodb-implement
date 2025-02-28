package com.innodb.btree;

import java.util.ArrayList;
import java.util.List;

/**
 * B+树实现
 */
public class BTree<K extends Comparable<K>, V> {
    private Node root;
    private static final int ORDER = 4; // B+树的阶数

    private class Node {
        List<K> keys;
        List<Object> pointers; // 可能是数据指针或子节点指针
        boolean isLeaf;
        Node next; // 叶子节点链表

        Node(boolean isLeaf) {
            this.isLeaf = isLeaf;
            this.keys = new ArrayList<>();
            this.pointers = new ArrayList<>();
            this.next = null;
        }
    }

    public BTree() {
        root = new Node(true);
    }

    public void insert(K key, V value) {
        if (root.keys.size() == 2 * ORDER - 1) {
            Node newRoot = new Node(false);
            newRoot.pointers.add(root);
            splitChild(newRoot, 0);
            root = newRoot;
        }
        insertNonFull(root, key, value);
    }

    private void splitChild(Node parent, int index) {
        Node child = (Node) parent.pointers.get(index);
        Node newNode = new Node(child.isLeaf);

        // 分裂节点
        int mid = ORDER - 1;
        K midKey = child.keys.get(mid);

        // 复制后半部分到新节点
        for (int i = mid; i < child.keys.size(); i++) {
            newNode.keys.add(child.keys.get(i));
            if (child.isLeaf) {
                newNode.pointers.add(child.pointers.get(i));
            } else if (i > mid) {
                newNode.pointers.add(child.pointers.get(i));
            }
        }
        if (!child.isLeaf) {
            newNode.pointers.add(child.pointers.get(child.pointers.size() - 1));
        }

        // 更新原节点
        while (child.keys.size() > mid) {
            child.keys.remove(child.keys.size() - 1);
            if (child.isLeaf || child.keys.size() >= mid) {
                child.pointers.remove(child.pointers.size() - 1);
            }
        }

        // 更新父节点
        parent.keys.add(index, midKey);
        parent.pointers.add(index + 1, newNode);

        // 维护叶子节点链表
        if (child.isLeaf) {
            newNode.next = child.next;
            child.next = newNode;
        }
    }

    private void insertNonFull(Node node, K key, V value) {
        int i = node.keys.size() - 1;

        if (node.isLeaf) {
            // 在叶子节点中插入
            // 如果节点为空，直接插入
            if (i < 0) {
                node.keys.add(key);
                node.pointers.add(value);
                return;
            }
            // 否则找到合适的插入位置
            while (i >= 0 && key.compareTo(node.keys.get(i)) < 0) {
                i--;
            }
            node.keys.add(i + 1, key);
            node.pointers.add(i + 1, value);
        } else {
            // 在内部节点中查找合适的子节点
            while (i >= 0 && key.compareTo(node.keys.get(i)) < 0) {
                i--;
            }
            i++;

            Node child = (Node) node.pointers.get(i);
            if (child.keys.size() == 2 * ORDER - 1) {
                splitChild(node, i);
                if (key.compareTo(node.keys.get(i)) > 0) {
                    i++;
                }
            }
            insertNonFull((Node) node.pointers.get(i), key, value);
        }
    }

    public V search(K key) {
        return searchInNode(root, key);
    }

    private V searchInNode(Node node, K key) {
        int i = 0;
        while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) {
            i++;
        }

        if (node.isLeaf) {
            if (i < node.keys.size() && key.compareTo(node.keys.get(i)) == 0) {
                return (V) node.pointers.get(i);
            }
            return null;
        } else if (i < node.keys.size() && key.compareTo(node.keys.get(i)) == 0) {
            // 如果在内部节点找到了完全匹配的键，继续在下一个子节点中搜索
            i++;
        }

        return searchInNode((Node) node.pointers.get(i), key);
    }
}
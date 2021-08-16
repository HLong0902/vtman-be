package com.viettel.vtman.cms.base.util;

import java.util.AbstractList;
import java.util.List;

public class Partition<T> extends AbstractList<List<T>> {
    final List<T> list;
    final int size;

    Partition(List<T> list, int size) {
        this.list = list;
        this.size = size;
    }

    public List<T> get(int index) {
        int listSize = this.size();
        if (listSize < 0) {
            throw new IllegalArgumentException("negative size: " + listSize);
        } else if (index < 0) {
            throw new IndexOutOfBoundsException("index " + index + " must not be negative");
        } else if (index >= listSize) {
            throw new IndexOutOfBoundsException("index " + index + " must be less than size " + listSize);
        } else {
            int start = index * this.size;
            int end = Math.min(start + this.size, this.list.size());
            return this.list.subList(start, end);
        }
    }

    public int size() {
        return (this.list.size() + this.size - 1) / this.size;
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public static <T> List<List<T>> partition(List<T> list, int size) {
        return new Partition(list, size);
    }
}

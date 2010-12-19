/** 
 * Copyright 2010 Daniel Guermeur and Amy Unruh
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   See http://connectrapp.appspot.com/ for a demo, and links to more information 
 *   about this app and the book that it accompanies.
 */
package com.metadot.book.connectr.server.utils;


import java.util.AbstractList;
import java.util.List;


public class ListPartition {
   
   /**
     * From http://www.vogella.de/articles/JavaAlgorithmsPartitionCollection, 
     * adapted from http://code.google.com/p/google-collections/ 
     */
    public static <T> List<List<T>> partition(List<T> list, int size) {
   
     if (list == null)
        throw new NullPointerException(
            "'list' must not be null");
      if (!(size > 0))
        throw new IllegalArgumentException(
            "'size' must be greater than 0");

      return new Partition<T>(list, size);
    }

    private static class Partition<T> extends AbstractList<List<T>> {

      final List<T> list;
      final int size;

      Partition(List<T> list, int size) {
        this.list = list;
        this.size = size;
      }

      @Override
      public List<T> get(int index) {
        int listSize = size();
        if (listSize < 0)
          throw new IllegalArgumentException("negative size: " + listSize);
        if (index < 0)
          throw new IndexOutOfBoundsException(
              "index " + index + " must not be negative");
        if (index >= listSize)
          throw new IndexOutOfBoundsException(
              "index " + index + " must be less than size " + listSize);
        int start = index * size;
        int end = Math.min(start + size, list.size());
        return list.subList(start, end);
      }

      @Override
      public int size() {
        return (list.size() + size - 1) / size;
      }

      @Override
      public boolean isEmpty() {
        return list.isEmpty();
      }
    }


}

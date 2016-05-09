package com.edu.suda.housing.tools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author shengshu
 * 
 */
public class UniqueMap {

  // Remove repetition from Map, this is core part in this Class
  public static Map<String, String> removeRepetitionFromMap(Map<String, String> map) {
    Set<Entry<String, String>> set = map.entrySet();

    List<Entry<String, String>> list = new ArrayList<Entry<String, String>>(set);

    Collections.sort(list, new Comparator<Entry<String, String>>() {
      @Override
      public int compare(Entry<String, String> entry1, Entry<String, String> entry2) {
        return Integer.valueOf(entry1.getValue().hashCode()) - Integer.valueOf(entry2.getValue().hashCode());
      }
    });

    // list.size() is dynamic change
    for (int index = 0; index < list.size(); index++) {
      String key = list.get(index).getKey();
      String value = list.get(index).getValue();

      int next_index = index + 1;

      if (next_index < list.size()) {
        String next_key = list.get(next_index).getKey();
        String next_value = list.get(next_index).getValue();

        // Remove repetition record whose key is more bigger
        if (value == next_value) {
          if (key.hashCode() < next_key.hashCode()) {
            map.remove(next_key);
            list.remove(next_index);
          } else {
            map.remove(key);
            list.remove(index);
          }

          // Due to having repetition in List, so index will be reduced
          index--;
        }
      }
    }

    return map;
  }

  // Transfer Map to Sorted Map
  public static Map<String, String> transferToSortedMap(Map<String, String> map) {
    // Define comparator for TreeMap
    Map<String, String> new_sort_map = new TreeMap<String, String>(new Comparator<String>() {
      @Override
      public int compare(String key1, String key2) {
        return key1.hashCode() - key2.hashCode();
      }
    });

    new_sort_map.putAll(map);

    return new_sort_map;
  }

  public static void printMap(Map<String, String> map) {
    Iterator<Entry<String, String>> iterator = map.entrySet().iterator();

    while (iterator.hasNext()) {
      Entry<String, String> entry = iterator.next();

      String key = entry.getKey();
      String value = entry.getValue();

      System.out.println(key + " --> " + value);
    }
  }

  public static void main(String[] args) {
    Map<String, String> map = new HashMap<String, String>();
    map.put("A", "1");
    map.put("B", "2");
    map.put("C", "2");
    map.put("D", "3");
    map.put("E", "3");

    Map<String, String> new_map = UniqueMap.removeRepetitionFromMap(map);

    // new_sort_map is what we want
    Map<String, String> new_sort_map = UniqueMap.transferToSortedMap(new_map);

    // Print new_sort_map
    UniqueMap.printMap(new_sort_map);
  }
}

package com.rqma.examples;

import java.util.Arrays;
import java.util.List;

/**
 * @Description:
 * @Auther: RQMA
 * @Date: 2020/6/1 13:55
 */
public class StreamDemo {
    public static void main(String[] args) {
   List<String> list = Arrays.asList("abc", "def");
   Object[] s1 = list.stream().map(s->s.toUpperCase()).toArray();
   System.out.println(s1.toString());

    }
}

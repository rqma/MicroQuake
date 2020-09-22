package com.rqma.examples;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description:
 * @Auther: RQMA
 * @Date: 2020/6/1 15:04
 */
public class AllofCompletableFutureDemo {
    public static String calc(String i){
        try {
            System.out.println("task线程："+Thread.currentThread().getName()+"任务i="+i+",完成！+"+new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i+"mrq";
    }
    public static void main(String[] args) {
        //添加n个任务
        CompletableFuture<String> array[]=new CompletableFuture[1000];
        for ( int i = 0; i < 1000; i++) {
            String tmp=i+"";
            array[i]=CompletableFuture.supplyAsync(()->calc(tmp+"fb"));

        }

        //获取结果的方式一
//       CompletableFuture.allOf(array).get();
//        for(CompletableFuture<Double> cf:array){
//            if(cf.get()>0.6){
//                System.out.println(cf.get());
//            }
//        }
        //获取结果的方式二，过滤大于指定数字，在收集输出
        List<String> rs= Stream.of(array).map(CompletableFuture::join).collect(Collectors.toList());
        System.out.println(rs);
    }
}

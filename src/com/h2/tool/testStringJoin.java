package com.h2.tool;

import com.h2.constant.Parameters;

public class testStringJoin {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] S = {"1","2","3","4","5"};
		String s = null;
		int [] i = new int[Parameters.SensorNum];
		s = stringJoin.SJoin_Array(S,i);
		System.out.println(s);
	}

}

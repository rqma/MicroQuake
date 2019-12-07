package com.h2.tool;

import com.h2.constant.Parameters;

public class stringJoin {
	/**
	 * @param S The String array will integrate.
	 * @return The integrated String.
	 */
	public static String SJoin_Array(String[] S, int [] l) {
		
		String joinS="";
		for(int i=0;i<S.length;i++) {
			if(l[i]==0&&i==0)
				joinS=joinS.concat(S[l[i]]);
			else if(l[i]!=0)
				joinS=joinS.concat(S[l[i]]);
		}
		return joinS;
	}
	
}

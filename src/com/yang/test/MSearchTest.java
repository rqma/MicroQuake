package com.yang.test;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MSearchTest {
		
	
	public static void main(String[] args) throws IOException {
		ArrayList<Integer> x = new ArrayList<>();
		ArrayList<Integer> y = new ArrayList<>();
		ArrayList<Integer> z = new ArrayList<>();

		FileReader fileReader = new FileReader("E:\\kuangshandata\\60.txt");
		BufferedReader buff = new BufferedReader(fileReader);
		// --------------------------------------- //
		
		String time = "20" ;
		int point = Integer.valueOf(time) * 5000;
		String line = null;
		String[] tmp = null;

		// read
		int count = 0;
		for (; count < point; count++) {
			line = buff.readLine();
			tmp = line.split(" ");
			x.add(Integer.valueOf(tmp[0]));
			y.add(Integer.valueOf(tmp[1]));
			z.add(Integer.valueOf(tmp[2]));
		}

		// sort arraylist x , y ,z

		int[] xArray = new int[x.size()];
		int[] yArray = new int[y.size()];
		int[] zArray = new int[z.size()];

		// copy array
		for (int i = 0; i < x.size(); i++)
			xArray[i] = x.get(i);

		for (int i = 0; i < y.size(); i++)
			yArray[i] = y.get(i);
		for (int i = 0; i < z.size(); i++)
			zArray[i] = z.get(i);

		// sort
		Arrays.sort(xArray);
		Arrays.sort(yArray);
		Arrays.sort(zArray);

		ArrayList<Integer> xM = new ArrayList<>();
		ArrayList<Integer> yM = new ArrayList<>();
		ArrayList<Integer> zM = new ArrayList<>();

		// get min , max based on xArray , yArray , zArray

		// put min
		int xMin = xArray[0];
		for (int i = 0; i < xArray.length; i++) {
			if (xArray[i] == xMin)
				xM.add(xArray[i]);
			else
				break;
		}

		// put max
		int xMax = xArray[xArray.length - 1];
		for (int i = xArray.length - 1; i >= 0; i--) {
			if (xArray[i] == xMax)
				xM.add(xArray[i]);
			else
				break;
		}

		int yMin = yArray[0];
		for (int i = 0; i < yArray.length; i++) {
			if (yArray[i] == yMin)
				yM.add(yArray[i]);
			else
				break;
		}

		// put max
		int yMax = yArray[yArray.length - 1];
		for (int i = yArray.length - 1; i >= 0; i--) {
			if (yArray[i] == yMax)
				yM.add(yArray[i]);
			else
				break;
		}

		int zMin = zArray[0];
		for (int i = 0; i < zArray.length; i++) {
			if (zArray[i] == zMin)
				zM.add(zArray[i]);
			else
				break;
		}

		// put max
		int zMax = zArray[zArray.length - 1];
		for (int i = zArray.length - 1; i >= 0; i--) {
			if (zArray[i] == zMax)
				zM.add(zArray[i]);
			else
				break;
		}

		Map<String, ArrayList> map = new HashMap<>();
		map.put("xdata", xM);
		map.put("ydata", yM);
		map.put("zdata", zM);

		String json = JSON.toJSONString(map);
		System.out.println(json);
	}
}

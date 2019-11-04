package com.qixin.test;

import java.util.ArrayList;
import java.util.List;

public class Test2 {
	public static void main(String[] args) {
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(0, 8);
		for (int i = 0; i < list.size(); i++) {
			System.err.println(list.get(i));
		}
	}
}

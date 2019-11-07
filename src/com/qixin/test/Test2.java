package com.qixin.test;

public class Test2 {
	public static void main(String[] args) {
		int[] in = { 1, 2, 3, 4, 5, 0, 0, 0, 0, 1, 2, 3, 4, 5, 0, 0, 6, 9, 7, 5, 0, 0, 0, 1, 2, 3, 2, 0, 0, 0, 2, 3 };
		int[] in1 = new int[28];
		int k = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 7; j++) {
				if (in[2 + i * 7 + j] == 0) {
					in1[k] = 9;
					k++;
					break;
				}
				in1[k] = in[2 + i * 7 + j];
				k++;
			}
		}

		for (int i = 0; i < in1.length; i++) {
			System.out.print(in1[i] + "  ");
		}
	}

}

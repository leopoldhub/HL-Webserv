package me.leopold.hubert.javawebserv.utils;

public class ArrayCopier {

	public static String[] copy(String[] original) {
		String[] res = new String[original.length];
		for(int i = 0; i < original.length; i++) {
			res[i] = original[i];
		}
		return res;
	}
	
}

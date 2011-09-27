package com.f16gaming.rulesbroadcast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RulesBroadcastFileReader {
	public String[] readAllLines(String fileName) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line;
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		while ((line = br.readLine()) != null) {
			line = line.replace('&', '\u00a7');
			line = line.replace("\u00a7\u00a7", "&");
			sb.append(line);
			sb.append('\n');
		}
		if (sb.length() == 0)
			sb.append('\n');
		return sb.toString().split("\n");
	}
}

package ar.edu.itba.ss.tp1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Configuration {
	
	static Integer particleCount;
	static Integer areaBorderLength;
	static List<Particle> particles = new ArrayList<>();
	
	public static void parseConfiguration() {		
		try(BufferedReader br = new BufferedReader(new FileReader("config.txt"))) {
			String line = br.readLine();
			if((particleCount = stringToInt(line)) == null || particleCount < 1) {
				failWithMessage("Invalid or missing particle count (" + line + ").");
			}
			line = br.readLine();
			if((areaBorderLength = stringToInt(line)) == null || areaBorderLength < 1) {
				failWithMessage("Invalid or missing area border length (" + line + ").");
			}
			for(int i = particleCount; i > 0; i--) {
				String[] attributes = br.readLine().split(" ");
				particles.add(validateParticle(attributes));
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Configuration loaded.");
	}
	
	public static Integer stringToInt(String s) {
		Integer i = null;
		try {
			i = Integer.valueOf(s);
		} catch(NumberFormatException e) {
			return null;
		}
		return i;
	}
	
	public static Double stringToDouble(String s) {
		Double d = null;
		try {
			d = Double.valueOf(s);
		} catch(NumberFormatException e) {
			return null;
		}
		return d;
	}
	
	public static void failWithMessage(String message) {
		System.err.println(message);
		System.exit(1);
	}
	
	public static Particle validateParticle(String[] attributes) {
		Double radius = null;
		Integer color = null;
		if(attributes.length < 2
			|| (radius = stringToDouble(attributes[0])) == null || radius <= 0
			|| (color = stringToInt(attributes[1])) == null || color < 1) {
				failWithMessage(radius + ", " + color + " is an invalid Particle.");
			}
		return new Particle(radius, color);
	}

}

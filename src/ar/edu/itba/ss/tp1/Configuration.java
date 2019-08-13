package ar.edu.itba.ss.tp1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Configuration {
	
	private static Integer particleCount;
	private static Integer areaBorderLength;
	private static Double interactionRadius;
	private static List<Particle> particles;
	private static Integer selectedParticleId;
	
	public static void parseConfiguration() {
		requestParameters();
		parseStaticConfiguration();
		parseDynamicConfiguration();
	}
	
	private static void requestParameters() {
		Scanner scanner = new Scanner(System.in);
		
	    System.out.println("Enter Interaction Radius: ");
	    while(interactionRadius == null) {
	    	interactionRadius = stringToDouble(scanner.nextLine());
	    }
	    
	    System.out.println("Enter Selected Particle Id: ");
	    while(selectedParticleId == null) {
	    	selectedParticleId = stringToInt(scanner.nextLine());
	    }
	    
	    scanner.close();
	}
	
	private static void parseStaticConfiguration() {
		particles = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new FileReader("config.txt"))) {
			String line = br.readLine();
			if((particleCount = stringToInt(line)) == null || particleCount < 1) {
				failWithMessage("Invalid or missing particle count (" + line + ").");
			}
			line = br.readLine();
			if((areaBorderLength = stringToInt(line)) == null || areaBorderLength < 1) {
				failWithMessage("Invalid or missing area border length (" + line + ").");
			}
			for(int i = 0; i < particleCount; i++) {
				line = br.readLine();
				if(line == null)
					failWithMessage("Particles do not match particle count.");
				String[] attributes = line.split(" ");
				particles.add(validateParticle(attributes));
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Static configuration loaded.");
	}
	
	private static void parseDynamicConfiguration() {		
		try(BufferedReader br = new BufferedReader(new FileReader("dynamic_config.txt"))) {
			String line = br.readLine();
			for(int i = 0; i < particleCount; i++) {
				line = br.readLine();
				if(line == null)
					failWithMessage("Particles do not match particle count.");
				String[] attributes = line.split(" ");
				setDynamicParticleAttributes(particles.get(i), attributes); // VALIDAR QUE NO SE SUPERPONGAN
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Dynamic configuration loaded.");
	}
	
	private static Integer stringToInt(String s) {
		Integer i = null;
		try {
			i = Integer.valueOf(s);
		} catch(NumberFormatException e) {
			return null;
		}
		return i;
	}
	
	private static Double stringToDouble(String s) {
		Double d = null;
		try {
			d = Double.valueOf(s);
		} catch(NumberFormatException e) {
			return null;
		}
		return d;
	}
	
	private static void failWithMessage(String message) {
		System.err.println(message);
		System.exit(1);
	}
	
	private static Particle validateParticle(String[] attributes) {
		Double radius = null;
		Integer color = null;
		if(attributes.length != 2
			|| (radius = stringToDouble(attributes[0])) == null || radius <= 0
			|| (color = stringToInt(attributes[1])) == null || color < 1) {
				failWithMessage(radius + ", " + color + " is an invalid Particle.");
			}
		return new Particle(radius, color);
	}
	
	private static void setDynamicParticleAttributes(Particle particle, String[] attributes) {
		Double x = null;
		Double y = null;
		Double vx = null;
		Double vy = null;
		if(attributes.length != 4
			|| (x = stringToDouble(attributes[0])) == null || x <= particle.getRadius()
			|| (y = stringToDouble(attributes[1])) == null || y <= particle.getRadius()
			|| (vx = stringToDouble(attributes[2])) == null
			|| (vy = stringToDouble(attributes[3])) == null) {
				failWithMessage(x + ", " + y + ", " + vx + ", " + vy + " are invalid attributes.");
			}
		particle.setPosition(x, y);
		particle.setVelocity(vx, vy);
	}

	public static int getParticleCount() {
		return particleCount;
	}

	public static int getAreaBorderLength() {
		return areaBorderLength;
	}
	
	public static double getInteractionRadius() {
		return interactionRadius;
	}

	public static List<Particle> getParticles() {
		return Collections.unmodifiableList(particles);
	}
	
	public static int getSelectedParticleId() {
		return selectedParticleId;
	}

}

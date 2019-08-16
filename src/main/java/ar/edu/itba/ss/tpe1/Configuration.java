package ar.edu.itba.ss.tpe1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Configuration {
	
	private static String staticFileName = "Static100.txt";
	private static String dynamicFileName = "Dynamic100.txt";
	private static Integer particleCount;
	private static Integer areaBorderLength;
	private static Double interactionRadius;
	private static List<Particle> particles;
	private static Integer selectedParticleId;
	private static boolean isPBCMode;
	private static Integer m = null;
	
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
	    
	    System.out.println("Enter Mode [0 -> Non-PBC; 1 -> Periodic Boundary Conditions]: ");
	    Integer selectedMode = null;
	    while(selectedMode == null) {
	    	selectedMode = stringToInt(scanner.nextLine());
	    }
	    isPBCMode = selectedMode != 0;
	    
	    System.out.println("Enter Mode [0 -> Non-Random Input; 1 -> Random-Input]: ");
	    Integer selectedInputMode = null;
	    while(selectedInputMode == null) {
	    	selectedInputMode = stringToInt(scanner.nextLine());
	    }
	    if(selectedInputMode == 1) {
	    	System.out.println("Enter Particle Count:");
	    	Integer selectedParticleCount = null;
		    while(selectedParticleCount == null) {
		    	selectedParticleCount = stringToInt(scanner.nextLine());
		    }
		    
		    System.out.println("Enter M:");
	    	Integer selectedM = null;
		    while(selectedM == null) {
		    	selectedM = stringToInt(scanner.nextLine());
		    }
		    m = selectedM;
		    
	    	generateRandomInputFiles(selectedParticleCount, 20, 0.25); // "HARDCODEADO"
	    }
	    
	    scanner.close();
	}
	
	private static void parseStaticConfiguration() {
		particles = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new FileReader(staticFileName))) {
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
				attributes = removeSpaces(attributes);
				particles.add(validateParticle(attributes));
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Static configuration loaded.");
	}
	
	private static void parseDynamicConfiguration() {		
		try(BufferedReader br = new BufferedReader(new FileReader(dynamicFileName))) {
			String line = br.readLine();
			for(int i = 0; i < particleCount; i++) {
				line = br.readLine();
				if(line == null)
					failWithMessage("Particles do not match particle count.");
				String[] attributes = line.split(" ");
				attributes = removeSpaces(attributes);
				setDynamicParticleAttributes(particles.get(i), attributes); // VALIDAR QUE NO SE SUPERPONGAN
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Dynamic configuration loaded.");
	}
	
	private static String[] removeSpaces(String[] array) {
		List<String> list = new ArrayList<>(Arrays.asList(array));
		List<String> filteredList = list.stream().filter(s -> !s.equals("") && !s.equals(" ")).collect(Collectors.toList());
		String[] newArray = new String[filteredList.size()];
		return filteredList.toArray(newArray);
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
		//Integer color = null;
		if(attributes.length != 2
			|| (radius = stringToDouble(attributes[0])) == null || radius <= 0
			/*|| (color = stringToInt(attributes[1])) == null || color < 1*/) {
				failWithMessage(attributes[0] + ", " + attributes[1] + " is an invalid Particle.");
			}
		return new Particle(radius, 1);
	}
	
	private static void setDynamicParticleAttributes(Particle particle, String[] attributes) {
		Double x = null;
		Double y = null;
		Double vx = null;
		Double vy = null;
		if(attributes.length != 4
			|| (x = stringToDouble(attributes[0])) == null || x < particle.getRadius() || x > areaBorderLength - particle.getRadius()
			|| (y = stringToDouble(attributes[1])) == null || y < particle.getRadius() || y > areaBorderLength - particle.getRadius()
			|| (vx = stringToDouble(attributes[2])) == null
			|| (vy = stringToDouble(attributes[3])) == null) {
				failWithMessage(attributes[0] + ", " + attributes[1] + ", " + attributes[2] + ", " + attributes[3] + " are invalid attributes.");
			}
		particle.setPosition(x, y);
		particle.setVelocity(vx, vy);
	}
	
	private static void generateRandomInputFiles(int particleCount, int areaBorderLength, double radius) { // SE PUEDEN OVERLAPEAR
		generateRandomStaticInputFile(particleCount, areaBorderLength, radius);
		generateRandomDynamicInputFile(particleCount, areaBorderLength, radius);
	}
	
	private static void generateRandomStaticInputFile(int particleCount, int areaBorderLength, double radius) {
		staticFileName = "random_static_config.txt";
		File staticInputFile = new File(staticFileName);
		staticInputFile.delete();
		try(FileWriter fw = new FileWriter(staticInputFile)) {
			staticInputFile.createNewFile();
			fw.write(particleCount + "\n");
			fw.write(areaBorderLength + "\n");
			for(int i = 0; i < particleCount; i++) {
				fw.write(radius + " 1\n"); // COLOR?
			}
		} catch (IOException e) {
			System.err.println("Failed to create static input file.");
			e.printStackTrace();
		}
	}
	
	private static void generateRandomDynamicInputFile(int particleCount, int areaBorderLength, double radius) {
		dynamicFileName = "random_dynamic_config.txt";
		File dynamicInputFile = new File(dynamicFileName);
		dynamicInputFile.delete();
		try(FileWriter fw = new FileWriter(dynamicInputFile)) {
			dynamicInputFile.createNewFile();
			fw.write("0\n");
			Random r = new Random();
			for(int i = 0; i < particleCount; i++) {
				double randomPositionX = radius + (areaBorderLength - 2 * radius) * r.nextDouble();
				double randomPositionY = radius + (areaBorderLength - 2 * radius) * r.nextDouble();
				double randomVelocityX = 0;
				double randomVelocityY = 0;
				fw.write(randomPositionX + " " + randomPositionY + " " + randomVelocityX + " " + randomVelocityY + "\n");
			}
		} catch (IOException e) {
			System.err.println("Failed to create dynamic input file.");
			e.printStackTrace();
		}
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
	
	public static boolean isPBCMode() {
		return isPBCMode;
	}
	
	public static Integer getM() {
		return m;
	}

}

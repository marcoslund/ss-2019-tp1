package ar.edu.itba.ss.tp1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		Configuration.parseConfiguration();
		Grid grid = new Grid(Configuration.getAreaBorderLength(), Configuration.getInteractionRadius(), Configuration.getParticles());
		grid.calculateParticlesNeighbors();
		generateOutputFile(Configuration.getParticles());
		//////////////////////
		for(Particle p : Configuration.getParticles()) {
			System.out.println("Neighbors for particle " + p + ":");
			for(Particle n : p.getNeighbors()) {
				System.out.println(n);
			}
		}
	}
	
	private static void generateOutputFile(List<Particle> particles) {
		File outputFile = new File("output.xyz");
		outputFile.delete();
		try(FileWriter fw = new FileWriter(outputFile)) {
			outputFile.createNewFile();
			for(Particle p : particles) {
				writeParticle(fw, p);
			}
		} catch (IOException e) {
			System.err.println("Failed to create output file.");
			e.printStackTrace();
		}
	}
	
	private static void writeParticle(FileWriter fw, Particle particle) throws IOException {
		fw.write(particle.getId() + " ");
		int aux = 0;
		String separator = " ";
		for(Particle neighbor : particle.getNeighbors()) {
			if(aux++ == particle.getNeighbors().size() - 1)
				separator = "";
			fw.write(neighbor.getId() + separator);
		}
		fw.write('\n');
	}

}

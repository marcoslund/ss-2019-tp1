package ar.edu.itba.ss.tpe1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

	public static void main(String[] args) {
		Configuration.parseConfiguration();
		Grid grid = new Grid(Configuration.getAreaBorderLength(), Configuration.getInteractionRadius(), Configuration.getParticles());
		
		long startTime = System.nanoTime();
		grid.calculateAllParticlesNeighbors();
		long endTime = System.nanoTime();
		System.out.println("Process done in " + TimeUnit.NANOSECONDS.toMillis(endTime - startTime) + " ms.");
		
		generateNeighborsOutputFile(Configuration.getParticles());
		generateOvitoOutputFile(Configuration.getParticles());
	}
	
	private static void generateNeighborsOutputFile(List<Particle> particles) {
		File outputFile = new File("neighbors_output.xyz");
		outputFile.delete();
		try(FileWriter fw = new FileWriter(outputFile)) {
			outputFile.createNewFile();
			for(Particle p : particles) {
				writeParticleNeighbors(fw, p);
			}
		} catch (IOException e) {
			System.err.println("Failed to create output file.");
			e.printStackTrace();
		}
	}
	
	private static void writeParticleNeighbors(FileWriter fw, Particle particle) throws IOException {
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
	
	private static void generateOvitoOutputFile(List<Particle> particles) {
		File outputFile = new File("ovito_output.xyz");
		outputFile.delete();
		try(FileWriter fw = new FileWriter(outputFile)) {
			outputFile.createNewFile();
			fw.write(Configuration.getParticleCount() + "\n");
			fw.write("Lattice=\"" + Configuration.getAreaBorderLength() + " 0.0 0.0 0.0 " + Configuration.getAreaBorderLength() 
				+ " 0.0 0.0 0.0 " + Configuration.getAreaBorderLength() 
				+ "\" Properties=id:I:1:radius:R:1:pos:R:2:vel:R:2:color:R:3\n");
			for(Particle p : particles) {
				writeOvitoParticle(fw, p);
			}
		} catch (IOException e) {
			System.err.println("Failed to create Ovito output file.");
			e.printStackTrace();
		}
	}
	
	private static void writeOvitoParticle(FileWriter fw, Particle particle) throws IOException {
		fw.write(particle.getId() + " " + particle.getRadius() + " " + particle.getPosition().x + " "
				+ particle.getPosition().y + " " + particle.getVelocity().x + " " + particle.getVelocity().y + " ");
		if(!Configuration.isRandomInput()) {
			if(particle.getId() == Configuration.getSelectedParticleId())
				fw.write("0.25 0.9 0.25");
			else if(Configuration.getParticles().get(Configuration.getSelectedParticleId()).getNeighbors().contains(particle))
				fw.write("0.9 0.3 0.25");
			else
				fw.write("1 1 1");
		} else
			fw.write("1 1 1");
		fw.write('\n');
	}

}

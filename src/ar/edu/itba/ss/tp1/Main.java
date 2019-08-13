package ar.edu.itba.ss.tp1;

public class Main {

	public static void main(String[] args) {
		Configuration.parseConfiguration();
		Grid grid = new Grid(Configuration.getAreaBorderLength(), Configuration.getInteractionRadius(), Configuration.getParticles());
		grid.calculateParticlesNeighbors();
		for(Particle p : Configuration.getParticles()) {
			System.out.println("Neighbors for particle " + p + ":");
			for(Particle n : p.getNeighbors()) {
				System.out.println(n);
			}
		}
	}

}

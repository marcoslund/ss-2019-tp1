package ar.edu.itba.ss.tp1;

import java.util.ArrayList;
import java.util.List;

public class Grid {
	
	private int areaBorderLength;
	private double interactionRadius;
	private List<List<GridSection>> grid;
	
	public Grid(int areaBorderLength, double interactionRadius, List<Particle> particles) {
		this.areaBorderLength = areaBorderLength;
		this.interactionRadius = interactionRadius;
		this.grid = new ArrayList<>();
		
		int m = calculateMaximumGridSectionBorderCount(particles);
		System.out.println("M: " + m); // SACAR
		for(int i = 0; i < m; i++) {
			grid.add(new ArrayList<>());
			for(int j = 0; j < m; j++) {
				grid.get(i).add(new GridSection(i, j));
			}
		}
		
		double gridSectionBorderLength = ((double) areaBorderLength) / m;
		for(Particle p : particles) {
			int particleGridSectionY = (int) (p.getPosition().x / gridSectionBorderLength);
			int particleGridSectionX = (int) ((areaBorderLength - p.getPosition().y) / gridSectionBorderLength);
			grid.get(particleGridSectionX).get(particleGridSectionY).addParticle(p);
		}
		// SACAR
		for(List<GridSection> gridRow : grid)
			for(GridSection gridSection : gridRow) {
				System.out.println(gridSection.getX() + " " + gridSection.getY());
				for(Particle p : gridSection.getParticles())
					System.out.println(p);
			}
		// SACAR
	}
	
	public void calculateParticlesNeighbors() {
		// VACIAR LOS GRID SECTIONS Y NEIGHBORS DE CADA PARTICLE
		for(List<GridSection> gridRow : grid) {
			for(GridSection gridSection : gridRow) {
				
				for(int i = 0; i < gridSection.getParticles().size(); i++) {
					Particle p1 = gridSection.getParticles().get(i);
					
					/* Particles within the same grid section */
					for(int j = i + 1; j < gridSection.getParticles().size(); j++) {
						Particle p2 = gridSection.getParticles().get(j);
						//if(!p1.getNeighbors().contains(p2)) {
							double borderToBorderDistance = Math.sqrt(
									Math.pow(Math.abs(p1.getPosition().x - p2.getPosition().x), 2) 
									+ Math.pow(Math.abs(p1.getPosition().y - p2.getPosition().y), 2))
										- p1.getRadius() - p2.getRadius();
							if(borderToBorderDistance <= interactionRadius) {
								p1.addNeighbor(p2);
								p2.addNeighbor(p1);
							}
						//}
					}
					
					/* Particles with neighboring grid section's particles */ // MEJORAR?
					if(gridSection.getX() > 0)
						calculateParticleNeighborsWithGridSection(p1, grid.get(gridSection.getX() - 1).get(gridSection.getY()));
					if(gridSection.getY() > 0)
						calculateParticleNeighborsWithGridSection(p1, grid.get(gridSection.getX()).get(gridSection.getY() - 1));
					if(gridSection.getY() < gridRow.size() - 1)
						calculateParticleNeighborsWithGridSection(p1, grid.get(gridSection.getX()).get(gridSection.getY() + 1));
					if(gridSection.getX() < grid.size() - 1)
						calculateParticleNeighborsWithGridSection(p1, grid.get(gridSection.getX() + 1).get(gridSection.getY()));
					if(gridSection.getX() > 0 && gridSection.getY() > 0)
						calculateParticleNeighborsWithGridSection(p1, grid.get(gridSection.getX() - 1).get(gridSection.getY() - 1));
					if(gridSection.getX() > 0 && gridSection.getY() < gridRow.size() - 1)
						calculateParticleNeighborsWithGridSection(p1, grid.get(gridSection.getX() - 1).get(gridSection.getY() + 1));
					if(gridSection.getX() < grid.size() - 1 && gridSection.getY() > 0)
						calculateParticleNeighborsWithGridSection(p1, grid.get(gridSection.getX() + 1).get(gridSection.getY() - 1));
					if(gridSection.getX() < grid.size() - 1 && gridSection.getY() < gridRow.size() - 1)
						calculateParticleNeighborsWithGridSection(p1, grid.get(gridSection.getX() + 1).get(gridSection.getY() + 1));
				}
			}
		}
	}
	
	private void calculateParticleNeighborsWithGridSection(Particle p1, GridSection gridSection) {
		for(Particle p2 : gridSection.getParticles()) {
			double borderToBorderDistance = Math.sqrt(
					Math.pow(Math.abs(p1.getPosition().x - p2.getPosition().x), 2) 
					+ Math.pow(Math.abs(p1.getPosition().y - p2.getPosition().y), 2))
						- p1.getRadius() - p2.getRadius();
			if(borderToBorderDistance <= interactionRadius) {
				p1.addNeighbor(p2);
				//p2.addNeighbor(p1);
			}
		}
	}

	private int calculateMaximumGridSectionBorderCount(List<Particle> particles) {
		double[] r = largestRadiusPair(particles);
		return (int) (areaBorderLength / (interactionRadius + r[0] + r[1]));
	}
	
	private double[] largestRadiusPair(List<Particle> particles) {
		double[] r = new double[2];
		r[0] = particles.get(0).getRadius();
		if(particles.size() == 1)
			return r;
		r[1] = particles.get(1).getRadius();
		for(int i = 2; i < particles.size(); i++) {
			double radius = particles.get(i).getRadius();
			if(r[0] < radius) {
				r[1] = r[0];
				r[0] = radius;
			} else if(r[1] < radius) {
				r[1] = radius;
			}
		}
		return r;
	}
	
	private class GridSection {
		
		private int x;
		private int y;
		private List<Particle> particles;
		
		public GridSection(int x, int y) {
			this.x = x;
			this.y = y;
			particles = new ArrayList<>();
		}
		
		public void addParticle(Particle p) {
			particles.add(p);
		}
		
		public void removeParticle(Particle p) {
			particles.remove(p);
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public List<Particle> getParticles() {
			return particles;
		}
		
	}
}

package ar.edu.itba.ss.tpe1;

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
		if(!Configuration.isOptimalM()) {
			if(Configuration.getM() > m)
				System.out.println("M violates Cell Index Method Algorithm.");
			m = Configuration.getM();
		}
		
		for(int i = 0; i < m; i++) {
			grid.add(new ArrayList<>());
			for(int j = 0; j < m; j++) {
				grid.get(i).add(new GridSection(i, j));
			}
		}
		
		double gridSectionBorderLength = ((double) areaBorderLength) / m;
		for(Particle p : particles) {
			int particleGridSectionRow = (int) ((areaBorderLength - p.getPosition().y) / gridSectionBorderLength);
			int particleGridSectionColumn = (int) (p.getPosition().x / gridSectionBorderLength);
			grid.get(particleGridSectionRow).get(particleGridSectionColumn).addParticle(p);
		}
	}
	
	public void calculateAllParticlesNeighbors() {
		// VACIAR LOS GRID SECTIONS Y NEIGHBORS DE CADA PARTICLE
		for(List<GridSection> gridRow : grid) {
			for(GridSection gridSection : gridRow) {
				for(int i = 0; i < gridSection.getParticles().size(); i++) {
					calculateParticleNeighbors(gridSection.getParticles().get(i), gridSection);
				}
			}
		}
	}
	
	private void calculateParticleNeighbors(Particle p, GridSection gridSection) {
		int middleColumnIndex = gridSection.getColumn();
		int middleRowIndex = gridSection.getRow();
		
		/* Particles within the same grid section */
		calculateParticleNeighborsWithGridSection(p, middleRowIndex, middleColumnIndex, grid.get(middleRowIndex).get(middleColumnIndex));
		if(getM() == 1)
			return;
		
		/* Particles with neighboring grid section's particles */
		if(!Configuration.isPBCMode() || getM() <= 2) {
			int leftColumnIndex = gridSection.getColumn() - 1;
			int rightColumnIndex = gridSection.getColumn() + 1;
			int topRowIndex = gridSection.getRow() - 1;
			int bottomRowIndex = gridSection.getRow() + 1;
			
			if(topRowIndex >= 0)
				calculateParticleNeighborsWithGridSection(p, middleRowIndex, middleColumnIndex, grid.get(topRowIndex).get(middleColumnIndex));
			if(leftColumnIndex >= 0)
				calculateParticleNeighborsWithGridSection(p, middleRowIndex, middleColumnIndex, grid.get(middleRowIndex).get(leftColumnIndex));
			if(rightColumnIndex < grid.get(0).size())
				calculateParticleNeighborsWithGridSection(p, middleRowIndex, middleColumnIndex, grid.get(middleRowIndex).get(rightColumnIndex));
			if(bottomRowIndex < grid.size())
				calculateParticleNeighborsWithGridSection(p, middleRowIndex, middleColumnIndex, grid.get(bottomRowIndex).get(middleColumnIndex));
			if(topRowIndex >= 0 && leftColumnIndex >= 0)
				calculateParticleNeighborsWithGridSection(p, middleRowIndex, middleColumnIndex, grid.get(topRowIndex).get(leftColumnIndex));
			if(topRowIndex >= 0 && rightColumnIndex < grid.get(0).size())
				calculateParticleNeighborsWithGridSection(p, middleRowIndex, middleColumnIndex, grid.get(topRowIndex).get(rightColumnIndex));
			if(bottomRowIndex < grid.size() && leftColumnIndex >= 0)
				calculateParticleNeighborsWithGridSection(p, middleRowIndex, middleColumnIndex, grid.get(bottomRowIndex).get(leftColumnIndex));
			if(bottomRowIndex < grid.size() && rightColumnIndex < grid.get(0).size())
				calculateParticleNeighborsWithGridSection(p, middleRowIndex, middleColumnIndex, grid.get(bottomRowIndex).get(rightColumnIndex));
		} else {
			int leftColumnIndex = (((gridSection.getColumn() - 1) % getM()) + getM()) % getM();
			int rightColumnIndex = (((gridSection.getColumn() + 1) % getM()) + getM()) % getM();
			int topRowIndex = (((gridSection.getRow() - 1) % getM()) + getM()) % getM();
			int bottomRowIndex = (((gridSection.getRow() + 1) % getM()) + getM()) % getM();
			
			calculateParticleNeighborsWithGridSection(p, middleRowIndex, middleColumnIndex, grid.get(topRowIndex).get(middleColumnIndex));
			calculateParticleNeighborsWithGridSection(p, middleRowIndex, middleColumnIndex, grid.get(middleRowIndex).get(leftColumnIndex));
			calculateParticleNeighborsWithGridSection(p, middleRowIndex, middleColumnIndex, grid.get(middleRowIndex).get(rightColumnIndex));
			calculateParticleNeighborsWithGridSection(p, middleRowIndex, middleColumnIndex, grid.get(bottomRowIndex).get(middleColumnIndex));
			calculateParticleNeighborsWithGridSection(p, middleRowIndex, middleColumnIndex, grid.get(topRowIndex).get(leftColumnIndex));
			calculateParticleNeighborsWithGridSection(p, middleRowIndex, middleColumnIndex, grid.get(topRowIndex).get(rightColumnIndex));
			calculateParticleNeighborsWithGridSection(p, middleRowIndex, middleColumnIndex, grid.get(bottomRowIndex).get(leftColumnIndex));
			calculateParticleNeighborsWithGridSection(p, middleRowIndex, middleColumnIndex, grid.get(bottomRowIndex).get(rightColumnIndex));
		}
	}
	
	private void calculateParticleNeighborsWithGridSection(Particle p1, int particleGridSectionRow, int particleGridSectionColumn, 
			GridSection gridSection) {
		for(Particle p2 : gridSection.getParticles()) {
			if(!p1.equals(p2)) {
				
				double horizontalDistance = Math.abs(p1.getPosition().x - p2.getPosition().x);
				if(Math.abs(particleGridSectionColumn - gridSection.getColumn()) >= 2) {
					/* PBC mode must be on */
					horizontalDistance = areaBorderLength - horizontalDistance;
				} else if(Configuration.isPBCMode()) {
					/* M must be 1 or 2 */
					horizontalDistance = Math.min(horizontalDistance, areaBorderLength - horizontalDistance);
				}
				
				double verticalDistance = Math.abs(p1.getPosition().y - p2.getPosition().y);
				if(Math.abs(particleGridSectionRow - gridSection.getRow()) >= 2) {
					/* PBC mode must be on */
					verticalDistance = areaBorderLength - verticalDistance;
				} else if(Configuration.isPBCMode()) {
					/* M must be 1 or 2 */
					verticalDistance = Math.min(verticalDistance, areaBorderLength - verticalDistance);
				}
				
				double borderToBorderDistance = Math.sqrt(
						Math.pow(horizontalDistance, 2) + Math.pow(verticalDistance, 2))
							- p1.getRadius() - p2.getRadius();
				if(borderToBorderDistance <= interactionRadius) {
					p1.addNeighbor(p2);
					//p2.addNeighbor(p1);
				}
			}
		}
	}

	private int calculateMaximumGridSectionBorderCount(List<Particle> particles) {
		double[] r = largestRadiusPair(particles);
		int maxGridSectionBorderCount = (int) (areaBorderLength / (interactionRadius + r[0] + r[1]));
		return (maxGridSectionBorderCount == 0)? 1 : maxGridSectionBorderCount;
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
	
	public void printParticlesByGridSection() {
		for(List<GridSection> gridRow : grid) {
			for(GridSection gridSection : gridRow) {
				System.out.println("Particles in (" + gridSection.getRow() + ", " + gridSection.getColumn() + "):");
				for(Particle p : gridSection.getParticles())
					System.out.println(p.getId() + ": (" + p.getPosition().x + ", " + p.getPosition().y + ")");
			}
		}
	}
	
	public void printParticlesWithNeighbors() {
		for(Particle p : Configuration.getParticles()) {
			System.out.println("Neighbors for particle " + p.getId() + "at (" + p.getPosition().x + ", " + p.getPosition().y + "):");
			for(Particle n : p.getNeighbors()) {
				System.out.println(n.getId());
			}
		}
	}
	
	public int getM() {
		return grid.size();
	}
	
	public double getGridSectionLength() {
		return areaBorderLength / (double) getM();
	}
	
	private class GridSection {
		
		private int row;
		private int column;
		private List<Particle> particles;
		
		public GridSection(int row, int column) {
			this.row = row;
			this.column = column;
			particles = new ArrayList<>();
		}
		
		public void addParticle(Particle p) {
			particles.add(p);
		}
		
		public void removeParticle(Particle p) {
			particles.remove(p);
		}
		
		public int getRow() {
			return row;
		}
		
		public int getColumn() {
			return column;
		}
		
		public List<Particle> getParticles() {
			return particles;
		}
		
	}
}

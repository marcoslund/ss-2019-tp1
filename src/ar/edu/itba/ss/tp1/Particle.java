package ar.edu.itba.ss.tp1;

import java.util.Objects;

public class Particle {
	
	private static int count = 0;
	
	private int id;
	private double radius;
	private int color;
	
	public Particle(double radius, int color) {
		this.id = count++;
		this.radius = radius;
		this.color = color;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(!(o instanceof Particle))
			return false;
		Particle other = (Particle) o;
		return this.id == other.getId();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	@Override
	public String toString() {
		return "id: " + id + "; radius: " + radius + " ; color: " + color;
	}

	public int getId() {
		return id;
	}

	public double getRadius() {
		return radius;
	}

	public int getColor() {
		return color;
	}

}

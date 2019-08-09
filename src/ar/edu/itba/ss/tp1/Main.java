package ar.edu.itba.ss.tp1;

public class Main {

	public static void main(String[] args) {
		Configuration.parseConfiguration();
		System.out.println(Configuration.getParticles());
	}

}

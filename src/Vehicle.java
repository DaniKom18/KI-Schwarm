import java.util.ArrayList;

public class Vehicle {
	static int allId = 0;
	int id; // Fahrzeug-ID
	double rad_sep; // Radius f�r Zusammenbleiben
	double rad_zus; // Radius f�r Separieren
	int type; // Fahrzeug-Type (0: Verfolger; 1: Anf�hrer)
	final double FZL; // L�nge
	final double FZB; // Breite
	double[] pos; // Position
	double[] vel; // Geschwindigkeit
	final double max_acc; // Maximale Beschleunigung
	final double max_vel; // Maximale Geschwindigkeit
	boolean isLeader = false;
	//Farbe des Vehicles
	String color;

	static int counter = 0;
	double deltaSeparieren = 0;
	double deltaZusammenbleiben = 0;
	double deltaAusrichten = 0;

	//Anzahl der Blauen fänger
	static int blueCounter = 10;




	Vehicle() {
		allId++;
		this.id = allId;
		this.FZL = 2;
		this.FZB = 1;
		this.rad_sep = 7;// 50
		this.rad_zus = 25;// 25
		this.type = 0;
		this.max_acc = 0.05;// 0.1
		this.max_vel = 1;
		this.color = generateColor();

		pos = new double[2];
		vel = new double[2];
		pos[0] = Simulation.pix * 500 * Math.random();
		pos[1] = Simulation.pix * 500 * Math.random();
		vel[0] = max_vel * Math.random();
		vel[1] = max_vel * Math.random();
	}

	//Wählt die Farbe für ein Vehicle aus
	public String generateColor() {
		String team;
		if (blueCounter!=0) {
			if(blueCounter == 10) isLeader = true;
			team = "blue";
			blueCounter--;
		}

		else team = "red";

		return team;
	}

	ArrayList<Vehicle> nachbarErmitteln(ArrayList<Vehicle> all, double radius1, double radius2) {
		ArrayList<Vehicle> neighbours = new ArrayList<Vehicle>();
		for (int i = 0; i < all.size(); i++) {
			Vehicle v = all.get(i);
			if (v.id != this.id) {
				double dist = Math.sqrt(Math.pow(v.pos[0] - this.pos[0], 2) + Math.pow(v.pos[1] - this.pos[1], 2));
				//Wenn Fahrzeug Farbe blau hat soll es die Roten nachbar identifizieren die in der nähe sind
				//TODO WARNING: Blaues Vehicle indentifiziert nun Rote und Blaue Vehicle
				if (dist >= radius1 && dist < radius2 && v.color.equals("red") && this.color.equals("blue") && isLeader) {
					neighbours.add(v);
				}
				//Rotes Fahrzeug identidiziert all seine nachbarn unabhängig von der Farbe
				if (dist >= radius1 && dist < radius2 && this.color.equals("red")) {
					neighbours.add(v);
				}
				if(this.color.equals("blue") && !isLeader && v.color.equals("blue")){
					neighbours.add(v);
				}
			}
		}
		return neighbours;
	}

	double[] beschleunigungErmitteln(double[] vel_dest) {
		//Berechnet die notwendige Beschleunigung, um eine Zielgeschwindigkeit vel_dest zu erreichen
		double[] acc_dest = new double[2];

		// 1. Konstanter Geschwindigkeitsbetrag
		vel_dest = Vektorrechnung.normalize(vel_dest);
		vel_dest[0] = vel_dest[0] * max_vel;
		vel_dest[1] = vel_dest[1] * max_vel;

		// 2. acc_dest berechnen
		acc_dest[0] = vel_dest[0] - vel[0];
		acc_dest[1] = vel_dest[1] - vel[1];

		return acc_dest;
	}

//cohesion
	double[] zusammenbleiben(ArrayList<Vehicle> all) {
		ArrayList<Vehicle> neighbours;
		double[] pos_dest = new double[2];
		double[] vel_dest = new double[2];
		double[] acc_dest = new double[2];

		acc_dest[0] = 0;
		acc_dest[1] = 0;
		neighbours = nachbarErmitteln(all, rad_sep, rad_zus);

		if (neighbours.size() > 0) {
			// 1. Zielposition pos_dest berechnen
			pos_dest[0] = 0;
			pos_dest[1] = 0;
			for (int i = 0; i < neighbours.size(); i++) {
				Vehicle v = neighbours.get(i);
				pos_dest[0] = pos_dest[0] + v.pos[0];
				pos_dest[1] = pos_dest[1] + v.pos[1];
			}
			pos_dest[0] = pos_dest[0] / neighbours.size();
			pos_dest[1] = pos_dest[1] / neighbours.size();

			// 2. Zielgeschwindigkeit vel_dest berechnen
			vel_dest[0] = pos_dest[0] - pos[0];
			vel_dest[1] = pos_dest[1] - pos[1];

			// 3. Zielbeschleunigung acc_dest berechnen
			acc_dest = beschleunigungErmitteln(vel_dest);

		}
		return acc_dest;
	}

	double[] separieren(ArrayList<Vehicle> all) {
		ArrayList<Vehicle> neighbours;
		double[] vel_dest = new double[2];
		double[] acc_dest = new double[2];

		acc_dest[0] = 0;
		acc_dest[1] = 0;
		neighbours  = nachbarErmitteln(all, 0, rad_sep);

		if (neighbours.size() > 0) {
			// 1. Zielgeschwindigkeit vel_dest berechnen
			vel_dest[0] = 0;
			vel_dest[1] = 0;
			for (int i = 0; i < neighbours.size(); i++) {
				Vehicle v    = neighbours.get(i);
				double[] vel = new double[2];
				double dist;

				vel[0] = v.pos[0] - pos[0];
				vel[1] = v.pos[1] - pos[1];
				dist   = rad_sep  - Vektorrechnung.length(vel);
				//if (dist < 0)System.out.println("fehler in rad");
				vel = Vektorrechnung.normalize(vel);
				vel[0] = -vel[0] * dist;
				vel[1] = -vel[1] * dist;
				
				vel_dest[0] = vel_dest[0] + vel[0];
				vel_dest[1] = vel_dest[1] + vel[1];
			}

			// 2. Zielbeschleunigung acc_dest berechnen
			acc_dest = beschleunigungErmitteln(vel_dest);
		}

		return acc_dest;
	}

	double[] ausrichten(ArrayList<Vehicle> all) {
		ArrayList<Vehicle> neighbours = new ArrayList<Vehicle>();
		double[] vel_dest = new double[2];
		double[] acc_dest = new double[2];
		acc_dest[0] = 0;
		acc_dest[1] = 0;

		neighbours = nachbarErmitteln(all, 0, rad_zus);

		if(neighbours.size() > 0){
			vel_dest[0] = 0;
			vel_dest[1] = 0;
			for (int i = 0; i < neighbours.size(); i++) {
				Vehicle v = neighbours.get(i);
				vel_dest[0] = vel_dest[0] + v.vel[0];
				vel_dest[1] = vel_dest[0] + v.vel[0];
			}
			vel_dest[0] /= neighbours.size();
			vel_dest[1] /= neighbours.size();
		}

		acc_dest = beschleunigungErmitteln(vel_dest);
/*
		
		Tragt bitte hier euren Sourcecode ein


*/
		return acc_dest;
	}

	double[] zufall() {
		double[] acc_dest = new double[2];
		acc_dest[0] = 0;
		acc_dest[1] = 0;

		if (Math.random() < 0.01) {
			acc_dest[0] = max_acc * Math.random();
			acc_dest[1] = max_acc * Math.random();
		}

		return acc_dest;
	}

	public double[] beschleunigung_festlegen(ArrayList<Vehicle> allVehicles) {
		double[] acc_dest  = new double[2];
		double[] acc_dest1 = new double[2];
		double[] acc_dest2 = new double[2];
		double[] acc_dest3 = new double[2];
		double f_zus = 0.1; // 0.05 // 0.15
		double f_sep = 3; // 0.55
		double f_aus = 0.9; // 0.4


		//Wen Fahrzeug farbe rot hat soll es andere werte bekommen für sep, zus, aus
		//TODO Math-random bestimmt welche werte ein Rotes bekommt
		if (this.color.equals("red")) {
			f_zus = deltaZusammenbleiben; // 0.05 // 0.15
			f_sep = deltaSeparieren; // 0.55
			f_aus = deltaAusrichten; // 0.4
		}
		if (this.color.equals("blue")&& isLeader) {
			f_zus = 5; // 0.05 // 0.15
			f_sep = 0.1; // 0.55
			f_aus = 5; // 0.4
		}

		if (type == 1) {
			acc_dest = zufall();
		}
		else {
			acc_dest1 = zusammenbleiben(allVehicles);
			// acc_dest1 = folgen(allVehicles);
			acc_dest2 = separieren(allVehicles);
			acc_dest3 = ausrichten(allVehicles);

			acc_dest[0] = (f_zus * acc_dest1[0]) + (f_sep * acc_dest2[0] + (f_aus * acc_dest3[0]));
			acc_dest[1] = (f_zus * acc_dest1[1]) + (f_sep * acc_dest2[1] + (f_aus * acc_dest3[1]));

		}
		
		acc_dest = Vektorrechnung.truncate(acc_dest, max_acc);
		return acc_dest;
	}

	void steuern(ArrayList<Vehicle> allVehicles) {
		double[] acc_dest = beschleunigung_festlegen(allVehicles);
	
		// 2. Neue Geschwindigkeit berechnen
		vel[0] = vel[0] + acc_dest[0];
		vel[1] = vel[1] + acc_dest[1];
		vel    = Vektorrechnung.normalize(vel);
		vel[0] = vel[0] * max_vel;
		vel[1] = vel[1] * max_vel;


		// 3. Neue Position berechnen
		pos[0] = pos[0] + vel[0];
		pos[1] = pos[1] + vel[1];

		position_Umgebung_anpassen_Box();
	}

	public void position_Umgebung_anpassen_Box() {
		if (pos[0] < 10) {
			vel[0] = Math.abs(vel[0]);
			pos[0] = pos[0] + vel[0];
		}
		if (pos[0] > 1000 * Simulation.pix) {
			vel[0] = -Math.abs(vel[0]);
			pos[0] = pos[0] + vel[0];
		}
		if (pos[1] < 10) {
			vel[1] = Math.abs(vel[1]);
			pos[1] = pos[1] + vel[1];
		}
		if (pos[1] > 700 * Simulation.pix) {
			vel[1] = -Math.abs(vel[1]);
			pos[1] = pos[1] + vel[1];
		}
	}
	

	
	double[] folgen(ArrayList<Vehicle> all) {
		double[] pos_dest = new double[2];
		double[] vel_dest = new double[2];
		double[] acc_dest = new double[2];
		acc_dest[0] = 0;
		acc_dest[1] = 0;
		Vehicle v = null;

		if (type == 0) {
			for (int i = 0; i < all.size(); i++) {
				v = all.get(i);
				if (v.type == 1)
					break;
			}
			double dist = Math.sqrt(Math.pow(v.pos[0] - this.pos[0], 2) + Math.pow(v.pos[1] - this.pos[1], 2));

			if (dist < rad_zus && inFront(v)) {
				double[] pkt = new double[2];
				double[] ort1 = new double[2];
				double[] ort2 = new double[2];
				double[] ort3 = new double[2];
				pkt[0] = pos[0];
				pkt[1] = pos[1];
				ort1[0] = v.pos[0];
				ort1[1] = v.pos[1];
				ort2[0] = v.pos[0] + (rad_zus * v.vel[0]);
				ort2[1] = v.pos[1] + (rad_zus * v.vel[1]);
				ort3 = Vektorrechnung.punktVektorMINAbstand_punkt(pkt, ort1, ort2);

				vel_dest[0] = pos[0] - ort3[0];// UUU
				vel_dest[1] = pos[1] - ort3[1];// III

				vel_dest = Vektorrechnung.normalize(vel_dest);
				vel_dest[0] = vel_dest[0] * max_vel;
				vel_dest[1] = vel_dest[1] * max_vel;

				acc_dest[0] = vel_dest[0] - vel[0];
				acc_dest[1] = vel_dest[1] - vel[1];
			} else if (dist < rad_zus && !inFront(v)) {
				pos_dest[0] = v.pos[0] + v.vel[0];
				pos_dest[1] = v.pos[1] + v.vel[0];
				vel_dest[0] = pos_dest[0] - pos[0];
				vel_dest[1] = pos_dest[1] - pos[1];
				vel_dest = Vektorrechnung.normalize(vel_dest);
				vel_dest[0] = vel_dest[0] * max_vel;
				vel_dest[1] = vel_dest[1] * max_vel;
				acc_dest[0] = vel_dest[0] - vel[0];
				acc_dest[1] = vel_dest[1] - vel[1];
			} else {
				acc_dest = zusammenbleiben(all);
			}
		}

		return acc_dest;
	}

	boolean inFront(Vehicle v) {
		//
		boolean erg = false;
		double[] tmp = new double[2];
		tmp[0] = pos[0] - v.pos[0];
		tmp[1] = pos[1] - v.pos[1];

		if (Vektorrechnung.winkel(tmp, v.vel) < Math.PI / 2)
			erg = true;
		else
			erg = false;

		return erg;
	}

	public void deltaBerechnen(ArrayList<Vehicle> all){
		double radius = 25;

		ArrayList<Vehicle> nachbarn = nachbarErmitteln(all, 0, radius);
		for (int i = 0; i < nachbarn.size(); i++) {
			Vehicle v = nachbarn.get(i);
			if (v.color.equals("red")) {
				v.deltaSeparieren = 0.2 + (this.deltaSeparieren+v.deltaSeparieren)/2;
				v.deltaAusrichten = 0.05 + (this.deltaAusrichten+v.deltaAusrichten)/2;
				v.deltaZusammenbleiben = -0.05 + (this.deltaZusammenbleiben+v.deltaZusammenbleiben)/2;
				System.out.println("changed values of Vehile number: " + v.id +" new values: " + v.deltaAusrichten +" : " + v.deltaZusammenbleiben + " : " + v.deltaSeparieren);
			}
			}
	}
	
}

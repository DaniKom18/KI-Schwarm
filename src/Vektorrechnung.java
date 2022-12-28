
public class Vektorrechnung {
	static double truncate(double x, double y) {
		//Abschneiden von x auf den Betrag y
		//(3,  5) ->  3
		//(3,  2) ->  2 
		//(-3, 5) -> -3
		//(-3, 2) -> -2 
		
		if (y < 0)
			System.out.println("Fehler truncate");
		if (x > 0)
			return Math.min(x, y);
		else
			return Math.max(x, -y);
	}

	static double[] normalize(double[] x) {
		double[] res = new double[2];
		double norm = Math.sqrt(Math.pow(x[0], 2) + Math.pow(x[1], 2));
		res[0] = x[0];
		res[1] = x[1];
		if (norm != 0) {
			res[0] = x[0] / norm;
			res[1] = x[1] / norm;
		}

		return res;
	}

	static double[] truncate(double[] x, double y) {
		//Abschneiden eines Vektors x auf den Betrag y
		if (y < 0)
			System.out.println("Fehler truncate");
		double[] res = normalize(x);
		res[0] = res[0] * truncate(length(x), y);
		res[1] = res[1] * truncate(length(x), y);
		return res;
	}

	static double length(double[] x) {
		double res = Math.sqrt(Math.pow(x[0], 2) + Math.pow(x[1], 2));
		return res;
	}

	static double winkel(double[] v1) {
		// Winkel von v1 gegenüber Koordinaten-X-Achse [0, 360[ gegen den Uhrzeigersinn

		double[] k = new double[2];
		double w;

		k[0] = 1;
		k[1] = 0;
		w = winkel(k, v1);
		if (v1[1] < 0)
			w = 2 * Math.PI - w;
		return w;
	}

	static double winkel(double[] v1, double[] v2) {
		// Berechnet den Winkel zwischen zwei Vektoren in winkelRad aus [0,180]

		double betrag_v1 = Math.sqrt(Math.pow(v1[0], 2) + Math.pow(v1[1], 2));
		double betrag_v2 = Math.sqrt(Math.pow(v2[0], 2) + Math.pow(v2[1], 2));
		double winkelGrad;
		double winkelRad;
		double skalPro;

		if (betrag_v1 == 0 || betrag_v2 == 0) {
			winkelGrad = 0;
			winkelRad = 0;
//			System.out.println("Betrag = 0");
		} else {
			skalPro = (v1[0] * v2[0]) + (v1[1] * v2[1]);
			winkelRad = skalPro / (betrag_v1 * betrag_v2);
			if (winkelRad > 1)
				winkelRad = 1;
			if (winkelRad < -1)
				winkelRad = -1;
			winkelRad = Math.acos(winkelRad);
			winkelGrad = winkelRad * 180 / Math.PI;
		}

		// System.out.println("Winkel " + winkelRad + " " + winkelGrad + " " + betrag_v1
		// + " " + betrag_v2);

		return winkelRad;
	}
	static double[] punktVektorMINAbstand_punkt(double[] pkt, double[] ort1, double[] ort2) {
		// berechnet denjenigen Punkt abstandsPkt auf einer Geraden [ort1, ort2], mit
		// kürzester Entfernung zum geg. Punkt pkt
		double[] abstandsPkt = new double[2];
		abstandsPkt[0] = 0;
		abstandsPkt[1] = 0;

		double dist;
		double winkel1;
		double winkel2;

		double[] richtung1 = new double[2];
		double[] richtung2 = new double[2];

		richtung1[0] = ort2[0] - ort1[0];
		richtung1[1] = ort2[1] - ort1[1];
		richtung2[0] = pkt[0] - ort1[0];
		richtung2[1] = pkt[1] - ort1[1];
		winkel1 = winkel(richtung1, richtung2);
		richtung1[0] = ort1[0] - ort2[0];
		richtung1[1] = ort1[1] - ort2[1];
		richtung2[0] = pkt[0] - ort2[0];
		richtung2[1] = pkt[1] - ort2[1];
		winkel2 = winkel(richtung1, richtung2);

		if (winkel1 >= Math.PI / 2) {
			abstandsPkt[0] = ort1[0];
			abstandsPkt[1] = ort1[1];
		} else if (winkel2 >= Math.PI / 2) {
			abstandsPkt[0] = ort2[0];
			abstandsPkt[1] = ort2[1];
		} else {
			richtung1[0] = ort2[0] - ort1[0];
			richtung1[1] = ort2[1] - ort1[1];
			richtung2[0] = pkt[0] - ort1[0];
			richtung2[1] = pkt[1] - ort1[1];
			winkel1 = winkel(richtung1, richtung2);
			dist = length(richtung2);
			double lot = dist * Math.cos(winkel1);
			double[] lotPkt = normalize(richtung1);
			abstandsPkt[0] = ort1[0] + lot * lotPkt[0];
			abstandsPkt[1] = ort1[1] + lot * lotPkt[1];
		}

		return abstandsPkt;
	}

}

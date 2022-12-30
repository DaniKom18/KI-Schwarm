import java.util.ArrayList;

import javax.swing.*;

public class Simulation extends JFrame {
	static int sleep = 8; // 8
	static double pix = 0.2;// 0.2
	int anzFz = 80;//160
	ArrayList<Vehicle> allVehicles = new ArrayList<Vehicle>();
	JPanel canvas = new Canvas(allVehicles, pix);

	Simulation() {
		setTitle("Swarm");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);

		for (int k = 0; k < anzFz; k++) {
			Vehicle car = new Vehicle();
			//if (k == 0) car.type = 1; roter Kreis
			allVehicles.add(car);
		}

		add(canvas);
		setSize(1000, 800);
		setVisible(true);

	}

	public static void main(String args[]) {
		Simulation xx = new Simulation();
		xx.run();
	}

	public void run() {
		Vehicle v;

		while (true) {
			boolean gameOverRed = true;
			boolean gameOverBlue = true;

			for (int i = 0; i < allVehicles.size(); i++) {
				v = allVehicles.get(i);
				v.steuern(allVehicles);

				//Wenn es noch rote Vehicle im spiel gibt läuft das spiel noch
				if (v.color.equals("red")) gameOverRed = false;
				if (v.color.equals("blue")) gameOverBlue = false;

				//Iterration durch alle Vehicle
				for (int j = 0; j < allVehicles.size(); j++) {
					Vehicle v2 = allVehicles.get(j);
					//Vehicle mit der gleichen ID soll nicht beachtet werden
					if (v.id == v2.id) continue;
					//schaut nach ob v1 und v2 gleiche pos haben
					if ((int)v.pos[0] == (int)v2.pos[0] && (int)v.pos[1] == (int)v2.pos[1]){
						//schaut nach wer farbe rot hat und wer farbe blau und zerstört immer das Rote
						if (v.color.equals("blue") && v2.color.equals("red")) allVehicles.remove(v2);
						//schaut nach ob ein blaues Vehicle ein Schwarzes berührt und zerstört im anschluss das Blaue
						else if (v.color.equals("blue") && v2.color.equals("black")) allVehicles.remove(v);
					}
				}

			}

			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
			}
			repaint();
			//wenn kein Rotes Vehicle mehr vorhanden ist dann game over und programm schließt
			if (gameOverRed){
				gameOverSelectWinningTeam("Blue");
			} else if (gameOverBlue) {
				gameOverSelectWinningTeam("Red");
			}
		}
	}
	public void gameOverSelectWinningTeam(String teamColor){
		StringBuilder winningTeam = new StringBuilder("The winning Team is: ");
		System.out.println(winningTeam.append(teamColor));
		System.exit(0);
	}
}

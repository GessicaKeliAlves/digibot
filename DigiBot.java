package inovatic;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html
// This robot was made basead on Wiki CirclingRobot
/**
 * DigiBot - a robot by (Inovatic Team)
 */
public class DigiBot extends AdvancedRobot
{
	boolean movingForward; // Se está se movimentando essa variavel fica true
	boolean inWall; // True caso estiver em uma parede (usado para sair da parede)
	int minDistanceBetweenRobots = 50;
 
	public void run() {
		stylingRobot();
 		setAdjustments();
		
	 	checkIfIsCloseOfAnotherRobot();
		
 
		setAhead(40000);
		setTurnRadarRight(360); // Scaneando até achar o inimigo
		movingForward = true; 
 
		while (true) {
			// Verificando se não está perto da parede para continuar se movimentando
			if (getX() > minDistanceBetweenRobots && getY() > minDistanceBetweenRobots 
					&& getBattleFieldWidth() - getX() > minDistanceBetweenRobots 
					&& getBattleFieldHeight() - getY() > minDistanceBetweenRobots 
					&& inWall == true) {
				inWall = false;
			}
			if (getX() <= minDistanceBetweenRobots|| getY() <= minDistanceBetweenRobots 
					|| getBattleFieldWidth() - getX() <= minDistanceBetweenRobots 
					|| getBattleFieldHeight() - getY() <= minDistanceBetweenRobots ) {
				if (inWall == false){
					reverseDirection();
					inWall = true;
				}
			}
 
			// Gira o radar até encontrar um inimigo
			if (getRadarTurnRemaining() == 0.0){
				setTurnRadarRight(360);
			}
 
			execute(); // Executa tudo
 
		}
	}
 
	public void stylingRobot(){
		// Adicionando cores ao Robô
		setBodyColor(Color.PINK);
		setGunColor(Color.BLACK	);
		setRadarColor(Color.BLACK);
		setBulletColor(Color.PINK);
		setScanColor(Color.RED);
	}
	
	public void setAdjustments(){
		setAdjustRadarForRobotTurn(true);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
	}
	
	public void checkIfIsCloseOfAnotherRobot(){
		// Verificando se está proximo de um robô
		if (getX() <= minDistanceBetweenRobots 
			|| getY() <= minDistanceBetweenRobots 
			|| getBattleFieldWidth() - getX() <= minDistanceBetweenRobots 
			|| getBattleFieldHeight() - getY() <= minDistanceBetweenRobots) {
				inWall = true;
			} else {
			inWall = false;
		}
	} 
 
 
	/**
	 * onHitWall:  There is a small chance the robot will still hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Ao bater na parede, vá para outra direção
		reverseDirection();
	}
 
	/**
	 * reverseDirection:  Ir para a direção oposta
	 */
	public void reverseDirection() {
		if (movingForward) {
			setBack(40000);
			movingForward = false;
		} else {
			setAhead(40000);
			movingForward = true;
		}
	}
 
 
	public void onScannedRobot(ScannedRobotEvent e) {
		// Verifica a posição exata do robô inimigo
		double absoluteBearing = getHeading() + e.getBearing();
		double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
		double bearingFromRadar = normalRelativeAngleDegrees(absoluteBearing - getRadarHeading());
 
		// Fica rodando em torno do inimigo
		if (movingForward){
			setTurnRight(normalRelativeAngleDegrees(e.getBearing() + 80)); // girar o robô para a direção do inimigo
		} else {
			setTurnRight(normalRelativeAngleDegrees(e.getBearing() + 100));
		}
 
 
		// Verifica se está perto do inimigo e mantem o radar no robô
		if (Math.abs(bearingFromGun) <= 4) {
			setTurnGunRight(bearingFromGun); 
			setTurnRadarRight(bearingFromRadar); 
			// A força do tiro depende da distancia e da energia
			if (getGunHeat() == 0 && getEnergy() > .2) {
				fire(Math.min(4.5 - Math.abs(bearingFromGun) / 2 - e.getDistance() / 250, getEnergy() - .1));
			} 
		} else {
			setTurnGunRight(bearingFromGun);
			setTurnRadarRight(bearingFromRadar);
		}
		// Scanea novamente
		if (bearingFromGun == 0) {
			scan();
		}
	}		
 
	/**
	 * onHitRobot: Ao bater em um robô, vai pra direção contrária
	 */
	public void onHitRobot(HitRobotEvent e) {
		if (e.isMyFault()) {
			reverseDirection();
		}
	}
}

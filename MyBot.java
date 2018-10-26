import hlt.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyBot {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("FightCWithJava");

        // We now have 1 full minute to analyse the initial map.
        final String initialMapIntelligence =
                "width: " + gameMap.getWidth() +
                "; height: " + gameMap.getHeight() +
                "; players: " + gameMap.getAllPlayers().size() +
                "; planets: " + gameMap.getAllPlanets().size();
        Log.log(initialMapIntelligence);
        
        final ArrayList<Move> moveList = new ArrayList<Move>();
        ArrayList<Ship> enemyShips = new ArrayList<Ship>();
        
        for (;;) {
            moveList.clear();
            networking.updateMap(gameMap);

            enemyShips.clear();
            enemyShips = gameMap.getEnemy().getShipsInArray();
            
            Position target = null;
            
            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
            	 
            	Comparator<Ship> principalTarget = new Comparator<Ship>() {
                  	@Override
                  	public int compare(Ship first, Ship second) {
                  		if(ship.getDistanceTo(first) > ship.getDistanceTo(second))
                  			return 1;
                  		return -1;
                  	}
                  };
                Collections.sort(enemyShips, principalTarget);
                
                for (Ship enemyShip : enemyShips) {
                	target = gameMap.returnMagicPosition(enemyShip);
                	final ThrustMove newThrustMove = Navigation.navigateShipTowardsTarget
                			(gameMap, ship, target, Constants.MAX_SPEED, true,
                					Constants.MAX_NAVIGATION_CORRECTIONS, Math.PI/180.0);
                	 if (newThrustMove != null) {
                         moveList.add(newThrustMove);
                         break;
                     }
                     continue;
                }
            }
            Networking.sendMoves(moveList);
        }
    }
}

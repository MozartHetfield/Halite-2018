import hlt.*;

import java.util.ArrayList;

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

        final ArrayList<Move> moveList = new ArrayList<>();

        //wanted positions to conquer
        Position upLeft = new Position((double) 0, (double) 0);
        Position downLeft = new Position((double) 0, (double) gameMap.getHeight());
        Position downRight = new Position((double) gameMap.getWidth(), (double) gameMap.getHeight());

        for (;;) {
            moveList.clear();
            networking.updateMap(gameMap);
            int decisionMaker = 0;

            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
            	if (decisionMaker % 3 == 0) {
            		final ThrustMove defensiveMove = Navigation.navigateShipTowardsTarget(gameMap, ship,
            			upLeft, Constants.MAX_SPEED, true, Constants.MAX_NAVIGATION_CORRECTIONS, Math.PI/180.0);
            		moveList.add(defensiveMove);
            	} else if (decisionMaker % 3 == 1) {
            		final ThrustMove defensiveMove = Navigation.navigateShipTowardsTarget(gameMap, ship,
            			downLeft, Constants.MAX_SPEED, true, Constants.MAX_NAVIGATION_CORRECTIONS, Math.PI/180.0);
            		moveList.add(defensiveMove);
            	} else {
            		final ThrustMove defensiveMove = Navigation.navigateShipTowardsTarget(gameMap, ship,
            			downRight, Constants.MAX_SPEED, true, Constants.MAX_NAVIGATION_CORRECTIONS, Math.PI/180.0);
            		moveList.add(defensiveMove);
            	}
            	
            	decisionMaker++;
            }
            
            Networking.sendMoves(moveList);
        }
    }
}

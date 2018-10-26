import hlt.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class MyBot {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("FightCWithJava");

        final String initialMapIntelligence =
                "width: " + gameMap.getWidth() +
                "; height: " + gameMap.getHeight() +
                "; players: " + gameMap.getAllPlayers().size() +
                "; planets: " + gameMap.getAllPlanets().size();
        Log.log(initialMapIntelligence);
        
        final ArrayList<Planet> reservedPlanets = new ArrayList <>();
        final ArrayList<Planet> dockedPlanets = new ArrayList <>();
        final ArrayList<Planet> allPlanets = new ArrayList<>();
        final ArrayList<Move> moveList = new ArrayList<>();

        for (final Planet planet : gameMap.getAllPlanets().values()) {
            allPlanets.add(planet);
        }

        for (;;) {
            moveList.clear();
            networking.updateMap(gameMap);

            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
                if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;
                }

                Comparator<Planet> distanceToShip = new Comparator<Planet>(){

                public int compare(Planet first, Planet second) {
                	if(ship.getDistanceTo(first) > ship.getDistanceTo(second)) return 1;
                    return -1;
                  }

                };

                Collections.sort(allPlanets, distanceToShip);
                Iterator<Planet> planetIterator = allPlanets.iterator();
                
                while(planetIterator.hasNext()) {
                    Planet planet = planetIterator.next();
                    
                    if (dockedPlanets.size() == allPlanets.size()) {
                    	if (ship.canDock(planet)) {
                    		moveList.add(new DockMove(ship, planet));
                    		break;
                    	}
                    }
                    
                    if (planet.isOwned() || dockedPlanets.contains(planet)
                    		|| reservedPlanets.contains(planet)) {
                        continue;
                    }                 
                   
                    if (ship.canDock(planet)) {
                        moveList.add(new DockMove(ship, planet));
                        dockedPlanets.add(planet);
                        break;
                    } else {
                        final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, planet, Constants.MAX_SPEED);
                        
                        if (newThrustMove != null) {
                            moveList.add(newThrustMove);
                            reservedPlanets.add(planet);
                            break;
                        }
                    }
                }
            }
            Networking.sendMoves(moveList);
            reservedPlanets.clear();
        }
    }
}

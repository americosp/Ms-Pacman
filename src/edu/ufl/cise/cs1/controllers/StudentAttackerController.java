package edu.ufl.cise.cs1.controllers;

import game.controllers.AttackerController;
import game.models.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public final class StudentAttackerController implements AttackerController {
	public void init(Game game) {
	}

	public void shutdown(Game game) {
	}

	public int update(Game game, long timeDue) {

		int action = -1;
		Attacker myGator = game.getAttacker();	// creates gator reference
		Maze maze = game.getCurMaze(); // creates maze reference

		//**Creates a reference for each ghost**

		Defender ghost1 = game.getDefender(0);
		Defender ghost2 = game.getDefender(1);
		Defender ghost3 = game.getDefender(2);
		Defender ghost4 = game.getDefender(3);



		//**Creates Lists that hold all of the Defenders and Pill Nodes**

		List<Defender> ghosts = game.getDefenders();
		List<Node> pills = game.getPillList();
		List<Node> powerPills = game.getPowerPillList();
		List<Node> powerPillNodes = maze.getPowerPillNodes();
		ArrayList<Node> ghostsNodes = new ArrayList<>();
		ArrayList<Node> vulnerableGhostsNodes = ghostsNodes;
		ArrayList<Node> ghostsNodesInPlay = ghostsNodes;

		for (int i = 0; i < 4; ++i) {	// Creates a List that holds all of the Defenders Nodes
			ghostsNodes.add(game.getDefender(i).getLocation());
			}
		for (int i = 0; i < ghostsNodes.size(); ++i) { //Creates a list that only holds ghosts that are vulnerable
			if (!ghosts.get(i).isVulnerable()) {
				vulnerableGhostsNodes.remove(i);
				}
			}
		for (int i = 0; i < ghostsNodes.size(); ++i) { //Creates list that only holds ghosts that aren't in the lair
			if (ghostsNodes.get(i).getPathDistance(myGator.getLocation()) == -1) {
				ghostsNodesInPlay.remove(i);
				}
			}


		//**Assigns Nodes with the closest Pill, Power Pill, and Ghost**

		Node closestPowerPill = myGator.getTargetNode(powerPills, true);
		Node closestPill = myGator.getTargetNode(pills, true);
		Node closestVulnerableGhost = myGator.getTargetNode(vulnerableGhostsNodes, true);
		Node closestGhost = myGator.getTargetNode(ghostsNodesInPlay, true);
		Node closestPowerPillNode = myGator.getTargetNode(powerPillNodes, true);
		int closestGhostDistance = closestGhost.getPathDistance(myGator.getLocation());

		Defender nearestGhost = null;	// Creates a reference to the closest ghost object
		for (int i =0; i < ghosts.size(); ++i){	//Finds the closest ghost object by matching it with the closest ghost node
			if (closestGhost == ghosts.get(i).getLocation()){
				nearestGhost = ghosts.get(i);
			}
		}

		/* GATOR INSTRUCTIONS */

		// **First priority instructions to dodge the closest ghost when it is in a close proximity (proximity is less than 50)

		if (closestGhostDistance < 50 && closestGhostDistance > 0) {

				if (closestGhostDistance < 10) {			// If ghost is in critical range
					if (nearestGhost.isVulnerable()) {	// Chase nearest ghost if vulnerable
						action = myGator.getNextDir(closestVulnerableGhost, true);
					}
					else {	// If not vulnerable, run away at all costs
						action = myGator.getNextDir(closestGhost, false);
					}

				}
				else if (ghost1.isVulnerable() || ghost2.isVulnerable() || ghost3.isVulnerable() || ghost4.isVulnerable()) { // If a ghost is not in critical range and one of the power pills have been eaten
					if (closestGhostDistance < 7) {		// If the closest ghost isn't vulnerable and is in a very close proximity, run away
						action = myGator.getNextDir(closestGhost, false);
						}
					else {	// If there is so threat of a ghost, chase the closest vulnerable ghost
						action = myGator.getNextDir(closestVulnerableGhost, true);
						}
				}
				else {
					 if (powerPills.size() > 0) {		// If there are no critically close ghosts and a power pill hasn't been eaten, chase closest power pill
						if (closestPowerPillNode.getPathDistance(myGator.getLocation()) == 3 && closestGhostDistance > 10)	// Once the gator is close to a power pill, wait next to it until a ghost gets in range
						action = -1;

						}
					 else if (powerPills.size() == 0) {	// If there are no critically close ghosts and no power pills left, chase the closest regular pill
						action = myGator.getNextDir(closestPill, true);
						}
					}
			}

		// **Second Priority Instructions for when no ghosts are in the gator's proximity**

		else {

				if (ghost1.isVulnerable() || ghost2.isVulnerable() || ghost3.isVulnerable() || ghost4.isVulnerable()) {	// Chase the closest vulnerable ghost after eating a pill
					action = myGator.getNextDir(closestVulnerableGhost, true);
					}
				else if (closestGhostDistance < 75 && closestGhostDistance > 0 && powerPills.size() > 0){	// When a ghost gets in a certain range and there are still power pills available, chase closest power pill
					action = myGator.getNextDir(closestPowerPill, true);
					}
				else if (powerPills.size() > 0) {	// If there are no ghosts in range at all and there are still power pills, avoid them until a ghost gets in range
					action = myGator.getNextDir(closestPowerPill, false);
					}
				else  {		// If there are no ghosts in range and there are no power pills left, chase closest regular pills
					action = myGator.getNextDir(closestPill, true);
					}
			}
		return action;
	}
}
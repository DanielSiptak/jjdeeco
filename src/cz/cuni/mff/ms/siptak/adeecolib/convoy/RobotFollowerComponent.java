/*******************************************************************************
 * Copyright 2012 Charles University in Prague
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package cz.cuni.mff.ms.siptak.adeecolib.convoy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.annotations.DEECoComponent;
import cz.cuni.mff.d3s.deeco.annotations.DEECoIn;
import cz.cuni.mff.d3s.deeco.annotations.DEECoInOut;
import cz.cuni.mff.d3s.deeco.annotations.DEECoInitialize;
import cz.cuni.mff.d3s.deeco.annotations.DEECoPeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.DEECoProcess;
import cz.cuni.mff.d3s.deeco.annotations.DEECoTrigger;
import cz.cuni.mff.d3s.deeco.knowledge.ComponentKnowledge;
import cz.cuni.mff.d3s.deeco.knowledge.OutWrapper;

@DEECoComponent
public class RobotFollowerComponent extends ComponentKnowledge {

	public Integer battery;
	public Path path;
	public String convoyRobot; // 0 if there is no robot ahead 1 otherwise
	public List<Path> crossingRobots;

	@DEECoInitialize
	public static ComponentKnowledge getInitialKnowledge() {
		RobotFollowerComponent k = new RobotFollowerComponent();
		k.id = "follower";
		k.battery = new Integer(100);
		k.path = new Path();
		k.path.currentPosition = new Integer(1);
		k.path.remainingPath = new ArrayList<Integer>(
				Arrays.asList(new Integer[] { new Integer(2), new Integer(3),
						new Integer(4), new Integer(5), new Integer(6),
						new Integer(7), new Integer(8), new Integer(9) }));
		k.convoyRobot = null;
		k.crossingRobots = null;
		return k;
	}

	@DEECoProcess
	@DEECoPeriodicScheduling(3000)
	public static void process(@DEECoInOut("path") Path path,
			@DEECoInOut("battery") OutWrapper<Integer> battery,
			@DEECoIn("convoyRobot") @DEECoTrigger String convoyRobot) {
		//System.out.println("[RobotFollowerComponent.process] convoyRobot = " + convoyRobot + ", remainingPath = " + path.remainingPath);
		if (path.remainingPath.size() > 0) {
			path.currentPosition = path.remainingPath.remove(0);
			battery.item = new Integer(battery.item - 1);
			System.out.println("Follower is moving: " + path.remainingPath);
		}
	}
}

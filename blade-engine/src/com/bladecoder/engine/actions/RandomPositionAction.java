/*******************************************************************************
 * Copyright 2014 Rafael Garcia Moreno.
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
package com.bladecoder.engine.actions;

import com.badlogic.gdx.math.Vector2;
import com.bladecoder.engine.assets.EngineAssetManager;
import com.bladecoder.engine.model.BaseActor;
import com.bladecoder.engine.model.InteractiveActor;
import com.bladecoder.engine.model.Scene;
import com.bladecoder.engine.model.VerbRunner;
import com.bladecoder.engine.model.World;

@ActionDescription("Sets actor position randomly.")
public class RandomPositionAction implements Action {
	@ActionProperty(required = true)
	@ActionPropertyDescription("The actor to change his position")
	private SceneActorRef actor;

	@ActionProperty
	@ActionPropertyDescription("Obtain the target position from this actor.")
	private SceneActorRef target;

	@ActionProperty(required = true)
	@ActionPropertyDescription("Maximum xy values. The absolute position to set if no target is selected. Relative if target is selected.")
	private Vector2 maxPosition;

	@ActionProperty(required = true)
	@ActionPropertyDescription("Minimum xy values. The absolute position to set if no target is selected. Relative if target is selected.")
	private Vector2 minPosition;

	private World w;

	@Override
	public void init(World w) {
		this.w = w;
	}

	@Override
	public boolean run(VerbRunner cb) {
		Scene s = actor.getScene(w);

		BaseActor a = s.getActor(actor.getActorId(), true);

		float x = a.getX();
		float y = a.getY();

		float rx = (float) (minPosition.x + Math.random() * (maxPosition.x - minPosition.x));
		float ry = (float) (minPosition.y + Math.random() * (maxPosition.y - minPosition.y));

		if (target != null) {
			Scene ts = target.getScene(w);
			BaseActor anchorActor = ts.getActor(target.getActorId(), false);

			x = anchorActor.getX();
			y = anchorActor.getY();

			if (anchorActor instanceof InteractiveActor && a != anchorActor) {
				Vector2 refPoint = ((InteractiveActor) anchorActor).getRefPoint();
				x += refPoint.x;
				y += refPoint.y;
			}

			float scale = EngineAssetManager.getInstance().getScale();

			x += rx * scale;
			y += ry * scale;
		} else {
			float scale = EngineAssetManager.getInstance().getScale();
			x = rx * scale;
			y = ry * scale;
		}

		a.setPosition(x, y);

		return false;
	}

}

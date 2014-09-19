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
package org.bladecoder.engine.actions;

import java.util.HashMap;

public interface Action {
	public void run();

	public void setParams(HashMap<String, String> params);

	/**
	 * If this method returns true, the verb must stops the execution and wait
	 * for the action to call the cb.resume()
	 * 
	 * @param cb
	 * @return
	 */
	public boolean waitForFinish(ActionCallback cb);

	public String getInfo();

	public Param[] getParams();
}
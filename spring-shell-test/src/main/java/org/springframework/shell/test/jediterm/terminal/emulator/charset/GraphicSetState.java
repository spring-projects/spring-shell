/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.test.jediterm.terminal.emulator.charset;

/**
 * @author jediterm authors
 */
public class GraphicSetState {
	private final GraphicSet[] myGraphicSets;

	//in-use table graphic left (GL)
	private GraphicSet myGL;
	//in-use table graphic right (GR)
	private GraphicSet myGR;

	//Override for next char (used by shift-in and shift-out)
	private GraphicSet myGlOverride;

	public GraphicSetState() {
		myGraphicSets = new GraphicSet[4];
		for (int i = 0; i < myGraphicSets.length; i++) {
			myGraphicSets[i] = new GraphicSet(i);
		}

		resetState();
	}

	/**
	 * Designates the given graphic set to the character set designator.
	 *
	 * @param graphicSet the graphic set to designate;
	 * @param designator the designator of the character set.
	 */
	public void designateGraphicSet( GraphicSet graphicSet, char designator) {
		graphicSet.setDesignation(CharacterSet.valueOf(designator));
	}


	public void designateGraphicSet(int num, CharacterSet characterSet) {
		getGraphicSet(num).setDesignation(characterSet);
	}

	/**
	 * Returns the (possibly overridden) GL graphic set.
	 */

	public GraphicSet getGL() {
		GraphicSet result = myGL;
		if (myGlOverride != null) {
			result = myGlOverride;
			myGlOverride = null;
		}
		return result;
	}

	/**
	 * Returns the GR graphic set.
	 */

	public GraphicSet getGR() {
		return myGR;
	}

	/**
	 * Returns the current graphic set (one of four).
	 *
	 * @param index the index of the graphic set, 0..3.
	 */

	public GraphicSet getGraphicSet(int index) {
		return myGraphicSets[index % 4];
	}

	/**
	 * Returns the mapping for the given character.
	 *
	 * @param ch the character to map.
	 * @return the mapped character.
	 */
	public char map(char ch) {
		return CharacterSets.getChar(ch, getGL(), getGR());
	}

	/**
	 * Overrides the GL graphic set for the next written character.
	 *
	 * @param index the graphic set index, {@literal >= 0 && < 3}.
	 */
	public void overrideGL(int index) {
		myGlOverride = getGraphicSet(index);
	}

	/**
	 * Resets the state to its initial values.
	 */
	public void resetState() {
		for (int i = 0; i < myGraphicSets.length; i++) {
			myGraphicSets[i].setDesignation(CharacterSet.valueOf((i == 1) ? '0' : 'B'));
		}
		myGL = myGraphicSets[0];
		myGR = myGraphicSets[1];
		myGlOverride = null;
	}

	/**
	 * Selects the graphic set for GL.
	 *
	 * @param index the graphic set index, {@literal >= 0 && <= 3}.
	 */
	public void setGL(int index) {
		myGL = getGraphicSet(index);
	}

	/**
	 * Selects the graphic set for GR.
	 *
	 * @param index the graphic set index, {@literal >= 0 && <= 3}.
	 */
	public void setGR(int index) {
		myGR = getGraphicSet(index);
	}


	public int getGLOverrideIndex() {
		return myGlOverride != null ? myGlOverride.getIndex() : -1;
	}
}
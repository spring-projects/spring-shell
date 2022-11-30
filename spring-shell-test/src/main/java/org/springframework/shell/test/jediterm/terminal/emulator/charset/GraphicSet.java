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
 * Denotes how a graphic set is designated.
 *
 * @author jediterm authors
 */
public class GraphicSet
{
	private final int myIndex; // 0..3
	private CharacterSet myDesignation;

	public GraphicSet( int index )
	{
		if ( index < 0 || index > 3 )
		{
			throw new IllegalArgumentException( "Invalid index!" );
		}
		myIndex = index;
		// The default mapping, based on XTerm...
		myDesignation = CharacterSet.valueOf( ( index == 1 ) ? '0' : 'B' );
	}

	/**
	 * @return the designation of this graphic set.
	 */
	public CharacterSet getDesignation()
	{
		return myDesignation;
	}

	/**
	 * @return the index of this graphics set.
	 */
	public int getIndex()
	{
		return myIndex;
	}

	/**
	 * Maps a given character index to a concrete character.
	 *
	 * @param original
	 *          the original character to map;
	 * @param index
	 *          the index of the character to map.
	 * @return the mapped character, or the given original if no mapping could
	 *         be made.
	 */
	public int map( char original, int index )
	{
		int result = myDesignation.map( index );
		if ( result < 0 )
		{
			// No mapping, simply return the given original one...
			result = original;
		}
		return result;
	}

	/**
	 * Sets the designation of this graphic set.
	 */
	public void setDesignation(CharacterSet designation )
	{
		myDesignation = designation;
	}
}
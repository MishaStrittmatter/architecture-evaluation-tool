/***************************************************************************
 * Copyright (C) 2016 Reiner Jung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/
package de.cau.cs.se.software.evaluation.state

/**
 * Factory for the transformation state of the size
 * transformation.
 */
class StateFactory {
	
	/**
	 * Create a row pattern table.
	 * 
	 * @param columns number of edges
	 * @param rows number of nodes
	 */
	def static createRowPatternTable(int columns, int rows) {
		return new RowPatternTable(columns, rows)
	}
	
	/**
	 * Create a single row pattern.
	 * 
	 * @param columns number of edges
	 */
	def static createRowPattern(int columns) {
		return new RowPattern(columns)
	}
	
}
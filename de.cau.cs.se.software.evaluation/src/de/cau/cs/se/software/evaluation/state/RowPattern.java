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
package de.cau.cs.se.software.evaluation.state;

import java.util.ArrayList;
import java.util.List;

import de.cau.cs.se.software.evaluation.hypergraph.Node;

/**
 * Stores a row pattern and the assoicated nodes.
 *
 * @author Reiner Jung
 *
 */
public class RowPattern {

	private final List<Node> nodes = new ArrayList<>();

	private final boolean[] pattern;

	/**
	 * Initialize pattern array.
	 *
	 * @param columns
	 *            number of edge columns
	 */
	public RowPattern(final int columns) {
		this.pattern = new boolean[columns];
	}

	public List<Node> getNodes() {
		return this.nodes;
	}

	public boolean[] getPattern() {
		return this.pattern;
	}

}

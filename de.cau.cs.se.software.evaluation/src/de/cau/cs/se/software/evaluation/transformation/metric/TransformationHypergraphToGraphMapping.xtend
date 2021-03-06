/***************************************************************************
 * Copyright (C) 2015 Reiner Jung
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
package de.cau.cs.se.software.evaluation.transformation.metric

import de.cau.cs.se.software.evaluation.hypergraph.ModularHypergraph
import de.cau.cs.se.software.evaluation.transformation.AbstractTransformation
import org.eclipse.core.runtime.IProgressMonitor
import de.cau.cs.se.software.evaluation.hypergraph.HypergraphFactory

import java.util.HashMap
import de.cau.cs.se.software.evaluation.hypergraph.Node
import de.cau.cs.se.software.evaluation.hypergraph.Module
import de.cau.cs.se.software.evaluation.views.LogModelProvider
import static extension de.cau.cs.se.software.evaluation.transformation.HypergraphCreationFactory.*

/**
 * Transform a hypergraph into a graph.
 * 
 * All hyperedges with more than two participant nodes are replaced by a node
 * and edges for each of the hyperedge participant nodes.
 */
class TransformationHypergraphToGraphMapping extends AbstractTransformation<ModularHypergraph, ModularHypergraph> {
	
	new(IProgressMonitor monitor) {
		super(monitor)
	}
	
	override generate(ModularHypergraph input) {
		result = HypergraphFactory.eINSTANCE.createModularHypergraph
		
		val nodeMap = new HashMap<Node,Node>()
		val moduleMap = new HashMap<Module,Module>()
		val nodeModuleMap = new HashMap<Node,Module>()
				
		input.modules.forEach[module | 
			val derivedModule = module.deriveModule
			moduleMap.put(module, derivedModule)
			module.nodes.forEach[nodeModuleMap.put(it, module)]
			result.modules += derivedModule
		]
		
		if (monitor.canceled)
			return null
		
		input.nodes.forEach[node | 
			val derivedNode = node.deriveNode
			nodeMap.put(node, derivedNode)
			result.nodes += derivedNode
		]
		
		if (monitor.canceled)
			return null
			
		input.edges.forEach[edge |
			if (!monitor.canceled) {
				val connectedNodes = input.nodes.filter[it.edges.contains(edge)]
				if (connectedNodes.size > 2) {
					val module = nodeModuleMap.get(connectedNodes.get(0))
					val derivedNode = result.createNode(moduleMap.get(module), edge.name, edge)
					connectedNodes.forEach[connectedNode |
						result.createEdge(nodeMap.get(connectedNode), derivedNode, connectedNode.name + "::" + derivedNode.name, edge)
					]
				} else if (connectedNodes.size == 2) {
					val derivedEdge = edge.deriveEdge
					nodeMap.get(connectedNodes.get(0)).edges.add(derivedEdge)
					nodeMap.get(connectedNodes.get(1)).edges.add(derivedEdge)
					
					result.edges += derivedEdge
				} else if (connectedNodes.size == 1) {
					val derivedEdge = edge.deriveEdge
					nodeMap.get(connectedNodes.get(0)).edges.add(derivedEdge)
					nodeMap.get(connectedNodes.get(0)).edges.add(derivedEdge)
					
					result.edges += derivedEdge
				} else {
					LogModelProvider.INSTANCE.addMessage("Edge Warning", "The edge " + edge.name + " is not used. Connected nodes are " + connectedNodes.size)
				}
			}
			
		]
		
		// TODO this is a very limited approach, better distribute
		monitor.worked(input.workEstimate)
						
		return result
	}
	
	override workEstimate(ModularHypergraph input) {
		input.modules.size + input.nodes.size +
		input.edges.size * (input.nodes.size + 2)
	}
	
}
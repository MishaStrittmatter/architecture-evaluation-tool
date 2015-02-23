package de.cau.cs.se.evaluation.architecture.transformation

import de.cau.cs.se.evaluation.architecture.hypergraph.Node
import de.cau.cs.se.evaluation.architecture.hypergraph.HypergraphFactory
import de.cau.cs.se.evaluation.architecture.hypergraph.Edge
import de.cau.cs.se.evaluation.architecture.hypergraph.Module
import org.eclipse.jdt.core.dom.Type
import java.util.List
import de.cau.cs.se.evaluation.architecture.hypergraph.ModularHypergraph
import de.cau.cs.se.evaluation.architecture.hypergraph.TypeTrace
import org.eclipse.jdt.core.IType
import org.eclipse.jdt.core.dom.SimpleType
import org.eclipse.jdt.core.dom.NameQualifiedType
import de.cau.cs.se.evaluation.architecture.hypergraph.MethodTrace
import org.eclipse.jdt.core.IMethod
import org.eclipse.jdt.core.dom.MethodDeclaration
import org.eclipse.jdt.core.dom.SingleVariableDeclaration
import org.eclipse.jdt.core.dom.Expression
import org.eclipse.jdt.internal.core.SourceMethod
import org.eclipse.jdt.core.ILocalVariable
import org.eclipse.jdt.core.dom.TypeDeclaration

class TransformationHelper {
	
	def static deriveNode(Node node) {
		val resultNode = HypergraphFactory.eINSTANCE.createNode
		resultNode.name = node.name
		val derivedFrom = HypergraphFactory.eINSTANCE.createNodeTrace
		derivedFrom.node = node
		resultNode.derivedFrom = derivedFrom
		
		return resultNode
	}
	
	def static deriveEdge(Edge edge) {
		val resultEdge = HypergraphFactory.eINSTANCE.createEdge
		resultEdge.name = edge.name
		val derivedFrom = HypergraphFactory.eINSTANCE.createEdgeTrace
		derivedFrom.edge = edge
		resultEdge.derivedFrom = derivedFrom
		
		return resultEdge
	}
	
	def static deriveModule(Module module) {
		val resultModule = HypergraphFactory.eINSTANCE.createModule
		resultModule.name = module.name
		val derivedFrom = HypergraphFactory.eINSTANCE.createModuleTrace
		derivedFrom.module = module
		resultModule.derivedFrom = derivedFrom
		
		return resultModule
	}
	
	def static findConstructorMethod(ModularHypergraph graph, Type type, List arguments) {
		val typeName = switch(type) {
			SimpleType : type.name
			NameQualifiedType : type.name
			default: return null
		}
		 
		val module = graph.modules.findFirst[module | 
			((module.derivedFrom as TypeTrace).type as IType).elementName.equals(typeName.fullyQualifiedName)
		]
		if (module != null) { // is null iff the type is an external type. External types must be ignored.
			val moduleName = ((module.derivedFrom as TypeTrace).type as IType).elementName
			val nodes = module.nodes.filter[node | 
				switch (node.derivedFrom) {
					MethodTrace: ((node.derivedFrom as MethodTrace).method as IMethod).elementName.equals(moduleName)
					TypeTrace: true // only implicit constructors are not derived from anything directly
					default: false
				} 
			]
			return matchArguments(nodes, arguments)
		} else
			return null
	}

	def static matchArguments(Iterable<Node> nodes, List arguments) {
		nodes.findFirst[node |
			switch(node.derivedFrom) {
				TypeTrace: return (arguments.size == 0) // name already matched. Implicit constructor has no parameters => only check argument size.
				MethodTrace: return matchArgumentsForExplicitMethods(node.derivedFrom as MethodTrace, arguments)
				default: throw new Exception("Unsupported derivedFrom type used for node " + node.derivedFrom.class)
			}
		]
	}
	
	private def static matchArgumentsForExplicitMethods(MethodTrace trace, List arguments) {
		switch(trace.method) {
			 MethodDeclaration: {
			 	val parameters = (trace.method as MethodDeclaration).parameters
				if (parameters.size == arguments.size)
					compareArgAndParam(parameters, arguments)
				else
					false
			 }
			 IMethod: {
			 	val parameters = (trace.method as IMethod).parameters
			 	if (parameters.size == arguments.size)
			 		compareArgAndParamIMethod(parameters, arguments)
			 	else
			 		false
			 }
			 default:
			 	throw new Exception("Implementation error. Method type " + trace.method.class + " not supported.")
		}
	}
	
	/** returns true if the arguments match the parameters. */
	private def static boolean compareArgAndParamIMethod(List<ILocalVariable> parameters, List arguments) {
		// arguments are Expression
		// parameters ILocalVariable
		// TODO this is not a satisfying implementation
		for(var i=0;i < parameters.size ; i++) {
			val pType = parameters.get(i).typeSignature
			val aType = (arguments.get(i) as Expression).resolveTypeBinding.binaryName
			
			if (pType.equals(aType))
				return false	
		}
		
		return true
	}
	
	/** returns true if the arguments match the parameters. */
	private def static boolean compareArgAndParam(List parameters, List arguments) {
		// arguments are Expression
		// parameters SingleVariableDeclaration
		for(var i=0;i < parameters.size ; i++) {
			val pType = (parameters.get(i) as SingleVariableDeclaration).type.resolveBinding.erasure
			val aType = (arguments.get(i) as Expression).resolveTypeBinding.erasure
			
			if (!pType.isAssignmentCompatible(aType))
				return false	
		}
		
		return true
	}
	
	def static createEdgeBetweenMethods(ModularHypergraph hypergraph, Node caller, Node callee) {
		val edgeSubset = caller.edges.filter[callerEdge | callee.edges.exists[calleeEdge | calleeEdge == callerEdge]]
		val edgeName = caller.name + "::" + callee.name
		val existingEdge = edgeSubset.findFirst[edge | edge.name.equals(edgeName)]
		if (existingEdge == null) {
			val edge = HypergraphFactory.eINSTANCE.createEdge
			edge.derivedFrom = null
			edge.name = edgeName
			hypergraph.edges.add(edge)
			callee.edges.add(edge)
			caller.edges.add(edge)
		}
	}
	
}
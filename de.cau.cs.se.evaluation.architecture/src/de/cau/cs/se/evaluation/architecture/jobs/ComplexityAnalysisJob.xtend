package de.cau.cs.se.evaluation.architecture.jobs

import org.eclipse.core.runtime.jobs.Job
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status
import org.eclipse.jface.viewers.ISelection
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.jface.viewers.ITreeSelection
import org.eclipse.jface.viewers.TreeSelection
import org.eclipse.core.resources.IProject
import org.eclipse.jdt.core.IPackageFragmentRoot
import org.eclipse.jdt.core.IPackageFragment
import org.eclipse.jdt.core.ICompilationUnit
import org.eclipse.jdt.core.IType
import java.util.ArrayList
import org.eclipse.jdt.core.IJavaProject
import de.cau.cs.se.evaluation.architecture.transformation.metrics.TransformationHypergraphMetrics
import de.cau.cs.se.evaluation.architecture.transformation.java.GlobalJavaScope
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.Flags
import org.eclipse.ui.PlatformUI
import org.eclipse.ui.PartInitException
import de.cau.cs.se.evaluation.architecture.views.AnalysisResultView
import de.cau.cs.se.evaluation.architecture.transformation.java.TransformationJavaMethodsToModularHypergraph
import de.cau.cs.se.evaluation.architecture.transformation.processing.TransformationMaximalInterconnectedGraph
import de.cau.cs.se.evaluation.architecture.transformation.processing.TransformationIntermoduleHyperedgesOnlyGraph
import de.cau.cs.se.evaluation.architecture.transformation.processing.TransformationHyperedgesOnlyGraph
import de.cau.cs.se.evaluation.architecture.transformation.processing.TransformationConnectedNodeHyperedgesOnlyGraph
import de.cau.cs.se.evaluation.architecture.hypergraph.Node
import de.cau.cs.se.evaluation.architecture.hypergraph.Hypergraph
import de.cau.cs.se.evaluation.architecture.transformation.metrics.ResultModelProvider
import de.cau.cs.se.evaluation.architecture.hypergraph.ModularHypergraph
import java.util.List
import de.cau.cs.se.evaluation.architecture.transformation.metrics.NamedValue

class ComplexityAnalysisJob extends Job {
	
	var types = new ArrayList<IType>()
	
	/**
	 * The constructor scans the selection for files.
	 * Compare to http://stackoverflow.com/questions/6892294/eclipse-plugin-how-to-get-the-path-to-the-currently-selected-project
	 */
	public new (ISelection selection) {
		super("Analysis Complexity")
		if (selection instanceof IStructuredSelection) {
			System.out.println("Got a structured selection");
			if (selection instanceof ITreeSelection) {
				val TreeSelection treeSelection = selection as TreeSelection
				
				treeSelection.iterator.forEach[element | element.scanForClasses]
			}
		}
	}
	
	protected override IStatus run(IProgressMonitor monitor) {
		// set total number of work units
			
		/** construct analysis. */
		val projects = new ArrayList<IJavaProject>()
		types.forEach[type | if (!projects.contains(type.javaProject)) projects.add(type.javaProject)]
		monitor.beginTask("Determine complexity of inter class dependency", 
			1 + // initialization 
			types.size*2 + // java to graph
			3 + types.size // graph analysis
		)
		monitor.worked(1)
				
		var scopes = new GlobalJavaScope(projects, null)
		
		val javaToModularHypergraph = new TransformationJavaMethodsToModularHypergraph(projects.get(0), scopes, types, monitor)
		javaToModularHypergraph.transform
				
		for (module : javaToModularHypergraph.modularSystem.modules) {
			System.out.println("module " + module.name)
			for (node : module.nodes) {
				System.out.println("  node " + node.name)
			}
		}
		for (node : javaToModularHypergraph.modularSystem.nodes) {
			System.out.println("node " + node.name)
			for (edge : node.edges) {
				System.out.println("  edge " + edge.name)
			}
		}
		
		/** Preparing different hypergraphs */
		
		// Transformation for MS^(n)
		val transformationMaximalInterconnectedGraph = new TransformationMaximalInterconnectedGraph(javaToModularHypergraph.modularSystem)
		// Transformation for MS^*
		val transformationIntermoduleHyperedgesOnlyGraph = new TransformationIntermoduleHyperedgesOnlyGraph(javaToModularHypergraph.modularSystem)
		
		transformationMaximalInterconnectedGraph.transform
		transformationIntermoduleHyperedgesOnlyGraph.transform
		
		/** calculating result metrics */
		
		// Calculation for S
		val metrics = new TransformationHypergraphMetrics(monitor)
		metrics.setSystem(javaToModularHypergraph.modularSystem)
		val systemSize = metrics.calculate
		
		// Calculation for S -> S^#, S^#_i
		val complexity = calculateComplexity(javaToModularHypergraph.modularSystem, monitor)	
		// Calculation for MS^(n) -> MS^(n)#, MS^(n)#_i
		val complexityMaximalInterconnected = calculateComplexity(transformationMaximalInterconnectedGraph.result, monitor)
		// Calculation for MS^* -> MS^*#, MS^*#_i
		val complexityIntermodule = calculateComplexity(transformationIntermoduleHyperedgesOnlyGraph.result, monitor)	
		
		/** display results */

		val result = ResultModelProvider.INSTANCE

		val projectName = projects.get(0).project.name

		result.getValues.add(new NamedValue(projectName + " Size", systemSize))
		result.getValues.add(new NamedValue(projectName + " Complexity", complexity))
		result.getValues.add(new NamedValue(projectName + " Cohesion", complexityIntermodule/complexityMaximalInterconnected))
		result.getValues.add(new NamedValue(projectName + " Coupling", complexityIntermodule))
				
		monitor.done()
		
		PlatformUI.getWorkbench.display.syncExec(new Runnable() {
       		public override void run() {
	           try { 
					val part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(AnalysisResultView.ID)
					(part as AnalysisResultView).update(ResultModelProvider.INSTANCE)
	           } catch (PartInitException e) {
	                e.printStackTrace()
	           }
	    	}
     	})
							
		return Status.OK_STATUS
	}
	
	/**
	 * Calculate for a given modular hyper graph:
	 * - hyperedges only graph
	 * - hyperedges only graphs for each node in the graph which is connected to the i-th node
	 * - calculate the size of all graphs
	 * - calculate the complexity
	 * 
	 * @param input a modular system
	 */
	private def calculateComplexity(ModularHypergraph input, IProgressMonitor monitor) {
		monitor?.subTask("Calculating metrics")
		// S^#
		val transformationHyperedgesOnlyGraph = new TransformationHyperedgesOnlyGraph(input)
		transformationHyperedgesOnlyGraph.transform
		// S^#_i
		val transformationConnectedNodeHyperedgesOnlyGraph = 
			new TransformationConnectedNodeHyperedgesOnlyGraph(transformationHyperedgesOnlyGraph.result)
		
		val resultConnectedNodeGraphs = new ArrayList<Hypergraph>() 
				
		for (Node node : transformationHyperedgesOnlyGraph.result.nodes) {
			transformationConnectedNodeHyperedgesOnlyGraph.node = node
			transformationConnectedNodeHyperedgesOnlyGraph.transform
			resultConnectedNodeGraphs.add(transformationConnectedNodeHyperedgesOnlyGraph.result)			
		}
		
		val metrics = new TransformationHypergraphMetrics(monitor)
		
		metrics.setSystem(transformationHyperedgesOnlyGraph.result)
		val complexity = calculateComplexity(transformationHyperedgesOnlyGraph.result, resultConnectedNodeGraphs, monitor)
		monitor?.worked(1)
		
		return complexity
	}
	
		/**
	 * Calculate complexity.
	 */
	private def double calculateComplexity(Hypergraph hypergraph, List<Hypergraph> subgraphs, IProgressMonitor monitor) {
		val metrics = new TransformationHypergraphMetrics(monitor)
		var double complexity = 0
		
		/** Can start at zero, as we ignore environment node by using system and not system graph. */
		for (var int i=0;i < hypergraph.nodes.size;i++) {
			metrics.setSystem(subgraphs.get(i))					
			complexity += metrics.calculate
		}
		
		metrics.setSystem(hypergraph)
		complexity -= metrics.calculate
		
		monitor?.worked(1)
			
		return complexity
	}
	
	
	private def dispatch scanForClasses(IProject object) {
		System.out.println("  IProject " + object.toString)
		if (object.hasNature(JavaCore.NATURE_ID)) {
			val IJavaProject project = JavaCore.create(object);
			project.allPackageFragmentRoots.forEach[root | root.checkForTypes]
		}
	}
	
	private def dispatch scanForClasses(IJavaProject object) {
		System.out.println("  IJavaProject " + object.elementName)
		object.allPackageFragmentRoots.forEach[root | root.checkForTypes]
	}
		
	private def dispatch scanForClasses(IPackageFragmentRoot object) {
		System.out.println("  IPackageFragmentRoot " + object.elementName)
		object.checkForTypes
	}
	
	private def dispatch scanForClasses(IPackageFragment object) {
		System.out.println("  IPackageFragment " + object.elementName)
		object.checkForTypes
	}
	
	private def dispatch scanForClasses(ICompilationUnit unit) {
		System.out.println("  ICompilationUnit " + unit.elementName)
		unit.checkForTypes()
	}
	
	private def dispatch scanForClasses(Object object) {
		System.out.println("  Selection=" + object.class.canonicalName + " " + object.toString)
	}
	
	/** check for types in the project hierarchy */
	
	/**
	 * in fragment roots 
	 */
	private def void checkForTypes(IPackageFragmentRoot root) {
		root.children.forEach[ element |
			if (element instanceof IPackageFragment) {
				(element as IPackageFragment).checkForTypes
			}
		]
	}
	
	/**
	 * in fragments
	 */
	private def void checkForTypes(IPackageFragment fragment) {
		fragment.children.forEach[ element |
			if (element instanceof IPackageFragment) {
				(element as IPackageFragment).checkForTypes
			} else if (element instanceof ICompilationUnit) {
				(element as ICompilationUnit).checkForTypes
			}
		]
	}
	
	/**
	 * in compilation units
	 */
	private def void checkForTypes(ICompilationUnit unit) {
		unit.allTypes.forEach[type | if (type instanceof IType) if (!Flags.isAbstract(type.flags)) types.add(type)]
	}
	
	
}
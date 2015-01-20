package de.cau.cs.se.evaluation.architecture.transformation.metrics;

import com.google.common.base.Objects;
import de.cau.cs.se.evaluation.architecture.hypergraph.Edge;
import de.cau.cs.se.evaluation.architecture.hypergraph.Hypergraph;
import de.cau.cs.se.evaluation.architecture.hypergraph.HypergraphFactory;
import de.cau.cs.se.evaluation.architecture.hypergraph.HypergraphSet;
import de.cau.cs.se.evaluation.architecture.hypergraph.Node;
import de.cau.cs.se.evaluation.architecture.state.RowPattern;
import de.cau.cs.se.evaluation.architecture.state.RowPatternTable;
import de.cau.cs.se.evaluation.architecture.state.StateFactory;
import de.cau.cs.se.evaluation.architecture.state.StateModel;
import de.cau.cs.se.evaluation.architecture.state.SystemSetup;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class TransformationHypergraphMetrics {
  private final HypergraphSet hypergraphSet;
  
  private final IProgressMonitor monitor;
  
  public TransformationHypergraphMetrics(final HypergraphSet hypergraphSet) {
    this.hypergraphSet = hypergraphSet;
    this.monitor = null;
  }
  
  public TransformationHypergraphMetrics(final HypergraphSet hypergraphSet, final IProgressMonitor monitor) {
    this.hypergraphSet = hypergraphSet;
    this.monitor = monitor;
  }
  
  public void transform(final Hypergraph system) {
    if (this.monitor!=null) {
      this.monitor.subTask("Calculating metrics");
    }
    final StateModel state = StateFactory.eINSTANCE.createStateModel();
    final Node environmentNode = HypergraphFactory.eINSTANCE.createNode();
    environmentNode.setName("_environment");
    SystemSetup _createSystemSetup = StateFactory.eINSTANCE.createSystemSetup();
    state.setMainsystem(_createSystemSetup);
    SystemSetup _mainsystem = state.getMainsystem();
    _mainsystem.setSystem(system);
    SystemSetup _mainsystem_1 = state.getMainsystem();
    Hypergraph _createSystemGraph = this.createSystemGraph(system, environmentNode);
    _mainsystem_1.setSystemGraph(_createSystemGraph);
    SystemSetup _mainsystem_2 = state.getMainsystem();
    SystemSetup _mainsystem_3 = state.getMainsystem();
    RowPatternTable _createRowPatternTable = this.createRowPatternTable(_mainsystem_3);
    _mainsystem_2.setRowPatternTable(_createRowPatternTable);
    if (this.monitor!=null) {
      this.monitor.worked(1);
    }
    EList<Node> _nodes = system.getNodes();
    final Procedure1<Node> _function = new Procedure1<Node>() {
      public void apply(final Node node) {
        if (TransformationHypergraphMetrics.this.monitor!=null) {
          String _name = node.getName();
          String _plus = ("Calculating metrics - subgraphs" + _name);
          TransformationHypergraphMetrics.this.monitor.subTask(_plus);
        }
        EList<SystemSetup> _subsystems = state.getSubsystems();
        SystemSetup _mainsystem = state.getMainsystem();
        SystemSetup _createSetup = TransformationHypergraphMetrics.this.createSetup(node, _mainsystem, environmentNode);
        _subsystems.add(_createSetup);
        if (TransformationHypergraphMetrics.this.monitor!=null) {
          TransformationHypergraphMetrics.this.monitor.worked(1);
        }
      }
    };
    IterableExtensions.<Node>forEach(_nodes, _function);
    SystemSetup _mainsystem_4 = state.getMainsystem();
    Hypergraph _system = _mainsystem_4.getSystem();
    SystemSetup _mainsystem_5 = state.getMainsystem();
    RowPatternTable _rowPatternTable = _mainsystem_5.getRowPatternTable();
    final double size = this.calculateSize(_system, _rowPatternTable);
    SystemSetup _mainsystem_6 = state.getMainsystem();
    EList<SystemSetup> _subsystems = state.getSubsystems();
    final double complexity = this.calculateComplexity(_mainsystem_6, _subsystems);
    System.out.println(((("Size " + Double.valueOf(size)) + "  Complexity ") + Double.valueOf(complexity)));
  }
  
  /**
   * Calculate complexity.
   */
  private double calculateComplexity(final SystemSetup setup, final EList<SystemSetup> list) {
    double complexity = 0;
    for (int i = 0; (i < setup.getSystem().getNodes().size()); i++) {
      {
        final SystemSetup subSystemSetup = list.get(i);
        double _complexity = complexity;
        Hypergraph _system = subSystemSetup.getSystem();
        RowPatternTable _rowPatternTable = subSystemSetup.getRowPatternTable();
        double _calculateSize = this.calculateSize(_system, _rowPatternTable);
        complexity = (_complexity + _calculateSize);
      }
    }
    double _complexity = complexity;
    Hypergraph _system = setup.getSystem();
    RowPatternTable _rowPatternTable = setup.getRowPatternTable();
    double _calculateSize = this.calculateSize(_system, _rowPatternTable);
    complexity = (_complexity - _calculateSize);
    if (this.monitor!=null) {
      this.monitor.worked(1);
    }
    return complexity;
  }
  
  /**
   * Calculate the size of given system.
   */
  private double calculateSize(final Hypergraph system, final RowPatternTable table) {
    double size = 0;
    for (int i = 0; (i < system.getNodes().size()); i++) {
      {
        EList<RowPattern> _patterns = table.getPatterns();
        EList<Node> _nodes = system.getNodes();
        Node _get = _nodes.get(i);
        final double probability = this.lookupProbability(_patterns, _get, system);
        if ((probability > 0.0d)) {
          double _size = size;
          double _log2 = this.log2(probability);
          double _minus = (-_log2);
          size = (_size + _minus);
        } else {
          System.out.println("Disconnected component. Result is tainted.");
        }
      }
    }
    if (this.monitor!=null) {
      this.monitor.worked(1);
    }
    return size;
  }
  
  /**
   * Logarithm for base 2.
   */
  private double log2(final double value) {
    double _log = Math.log(value);
    double _log_1 = Math.log(2);
    return (_log / _log_1);
  }
  
  /**
   * Find the row pattern for a given node and determine its probability. If no pattern
   * is found then the node is totally disconnected and the probability is 0.
   */
  private double lookupProbability(final EList<RowPattern> patternList, final Node node, final Hypergraph system) {
    final Function1<RowPattern, Boolean> _function = new Function1<RowPattern, Boolean>() {
      public Boolean apply(final RowPattern p) {
        EList<Node> _nodes = p.getNodes();
        return Boolean.valueOf(_nodes.contains(node));
      }
    };
    final Iterable<RowPattern> pattern = IterableExtensions.<RowPattern>filter(patternList, _function);
    double _xifexpression = (double) 0;
    int _size = IterableExtensions.size(pattern);
    boolean _greaterThan = (_size > 0);
    if (_greaterThan) {
      RowPattern _get = ((RowPattern[])Conversions.unwrapArray(pattern, RowPattern.class))[0];
      EList<Node> _nodes = _get.getNodes();
      int _size_1 = _nodes.size();
      _xifexpression = ((double) _size_1);
    } else {
      _xifexpression = 0;
    }
    final double count = _xifexpression;
    EList<Node> _nodes_1 = system.getNodes();
    int _size_2 = _nodes_1.size();
    int _plus = (_size_2 + 1);
    return (count / ((double) _plus));
  }
  
  /**
   * Create a system setup.
   */
  private SystemSetup createSetup(final Node node, final SystemSetup mainSetup, final Node environmentNode) {
    final SystemSetup setup = StateFactory.eINSTANCE.createSystemSetup();
    Hypergraph _system = mainSetup.getSystem();
    Hypergraph _createSubsystem = this.createSubsystem(node, _system);
    setup.setSystem(_createSubsystem);
    Hypergraph _system_1 = setup.getSystem();
    Hypergraph _createSystemGraph = this.createSystemGraph(_system_1, environmentNode);
    setup.setSystemGraph(_createSystemGraph);
    RowPatternTable _createRowPatternTable = this.createRowPatternTable(setup);
    setup.setRowPatternTable(_createRowPatternTable);
    return setup;
  }
  
  /**
   * Create a row pattern table for a system and a system graph.
   * First, register the edges in the pattern table as column definitions.
   * Second, calculate the pattern row for each node of the system graph.
   * Compact, rows with the same pattern
   */
  private RowPatternTable createRowPatternTable(final SystemSetup setup) {
    final RowPatternTable patternTable = StateFactory.eINSTANCE.createRowPatternTable();
    Hypergraph _system = setup.getSystem();
    EList<Edge> _edges = _system.getEdges();
    final Procedure1<Edge> _function = new Procedure1<Edge>() {
      public void apply(final Edge edge) {
        EList<Edge> _edges = patternTable.getEdges();
        _edges.add(edge);
      }
    };
    IterableExtensions.<Edge>forEach(_edges, _function);
    Hypergraph _systemGraph = setup.getSystemGraph();
    EList<Node> _nodes = _systemGraph.getNodes();
    final Procedure1<Node> _function_1 = new Procedure1<Node>() {
      public void apply(final Node node) {
        EList<RowPattern> _patterns = patternTable.getPatterns();
        EList<Edge> _edges = patternTable.getEdges();
        RowPattern _calculateRowPattern = TransformationHypergraphMetrics.this.calculateRowPattern(node, _edges);
        _patterns.add(_calculateRowPattern);
      }
    };
    IterableExtensions.<Node>forEach(_nodes, _function_1);
    this.compactPatternTable(patternTable);
    return patternTable;
  }
  
  /**
   * Calculate the row pattern of a node based on its edges.
   * 
   * @param node where the pattern is calculated for
   * @param edgeList sequence of edges which define the table wide order of edges
   * 
   * @returns the complete pattern
   */
  private RowPattern calculateRowPattern(final Node node, final EList<Edge> edgeList) {
    final RowPattern pattern = StateFactory.eINSTANCE.createRowPattern();
    EList<Node> _nodes = pattern.getNodes();
    _nodes.add(node);
    final Procedure1<Edge> _function = new Procedure1<Edge>() {
      public void apply(final Edge edge) {
        EList<Boolean> _pattern = pattern.getPattern();
        EList<Edge> _edges = node.getEdges();
        final Function1<Edge, Boolean> _function = new Function1<Edge, Boolean>() {
          public Boolean apply(final Edge it) {
            return Boolean.valueOf(Objects.equal(it, edge));
          }
        };
        boolean _exists = IterableExtensions.<Edge>exists(_edges, _function);
        _pattern.add(Boolean.valueOf(_exists));
      }
    };
    IterableExtensions.<Edge>forEach(edgeList, _function);
    return pattern;
  }
  
  /**
   * Find duplicate pattern in the pattern table and merge the pattern rows.
   */
  private void compactPatternTable(final RowPatternTable table) {
    for (int i = 0; (i < table.getPatterns().size()); i++) {
      for (int j = (i + 1); (j < table.getPatterns().size()); j++) {
        EList<RowPattern> _patterns = table.getPatterns();
        RowPattern _get = _patterns.get(j);
        EList<Boolean> _pattern = _get.getPattern();
        EList<RowPattern> _patterns_1 = table.getPatterns();
        RowPattern _get_1 = _patterns_1.get(i);
        EList<Boolean> _pattern_1 = _get_1.getPattern();
        boolean _matchPattern = this.matchPattern(_pattern, _pattern_1);
        if (_matchPattern) {
          EList<RowPattern> _patterns_2 = table.getPatterns();
          final RowPattern basePattern = _patterns_2.get(i);
          EList<RowPattern> _patterns_3 = table.getPatterns();
          RowPattern _get_2 = _patterns_3.get(j);
          EList<Node> _nodes = _get_2.getNodes();
          final Procedure1<Node> _function = new Procedure1<Node>() {
            public void apply(final Node node) {
              EList<Node> _nodes = basePattern.getNodes();
              _nodes.add(node);
            }
          };
          IterableExtensions.<Node>forEach(_nodes, _function);
          EList<RowPattern> _patterns_4 = table.getPatterns();
          _patterns_4.remove(j);
        }
      }
    }
  }
  
  /**
   * Return true if both lists contain the same values in the list.
   */
  private boolean matchPattern(final EList<Boolean> leftList, final EList<Boolean> rightList) {
    int _size = leftList.size();
    int _size_1 = rightList.size();
    boolean _notEquals = (_size != _size_1);
    if (_notEquals) {
      return false;
    }
    for (int i = 0; (i < leftList.size()); i++) {
      Boolean _get = leftList.get(i);
      Boolean _get_1 = rightList.get(i);
      boolean _equals = _get.equals(_get_1);
      boolean _not = (!_equals);
      if (_not) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Create an subgraph only containing those edges which are connected to the given start node.
   * 
   * @param node the start node
   * @param system the system graph of a system
   * 
   * @returns a proper subgraph
   */
  private Hypergraph createSubsystem(final Node node, final Hypergraph system) {
    final Hypergraph subgraph = HypergraphFactory.eINSTANCE.createHypergraph();
    EList<Hypergraph> _graphs = this.hypergraphSet.getGraphs();
    _graphs.add(subgraph);
    EList<Edge> _edges = node.getEdges();
    final Procedure1<Edge> _function = new Procedure1<Edge>() {
      public void apply(final Edge it) {
        EList<Edge> _edges = subgraph.getEdges();
        _edges.add(it);
      }
    };
    IterableExtensions.<Edge>forEach(_edges, _function);
    EList<Node> _nodes = system.getNodes();
    final Procedure1<Node> _function_1 = new Procedure1<Node>() {
      public void apply(final Node it) {
        EList<Node> _nodes = subgraph.getNodes();
        _nodes.add(it);
      }
    };
    IterableExtensions.<Node>forEach(_nodes, _function_1);
    return subgraph;
  }
  
  /**
   * Create a system graph from a hypergraph of a system by adding an additional not connected
   * node for the environment.
   * 
   * @param hypergraph the graph which is used as input
   * 
   * @returns the system graph (Note: the system graph and the system share nodes)
   */
  private Hypergraph createSystemGraph(final Hypergraph system, final Node environmentNode) {
    final Hypergraph systemGraph = HypergraphFactory.eINSTANCE.createHypergraph();
    EList<Hypergraph> _graphs = this.hypergraphSet.getGraphs();
    _graphs.add(systemGraph);
    EList<Node> _nodes = systemGraph.getNodes();
    _nodes.add(environmentNode);
    EList<Node> _nodes_1 = this.hypergraphSet.getNodes();
    _nodes_1.add(environmentNode);
    EList<Node> _nodes_2 = system.getNodes();
    final Procedure1<Node> _function = new Procedure1<Node>() {
      public void apply(final Node node) {
        EList<Node> _nodes = systemGraph.getNodes();
        _nodes.add(node);
      }
    };
    IterableExtensions.<Node>forEach(_nodes_2, _function);
    EList<Edge> _edges = system.getEdges();
    final Procedure1<Edge> _function_1 = new Procedure1<Edge>() {
      public void apply(final Edge edge) {
        EList<Edge> _edges = systemGraph.getEdges();
        _edges.add(edge);
      }
    };
    IterableExtensions.<Edge>forEach(_edges, _function_1);
    return systemGraph;
  }
}

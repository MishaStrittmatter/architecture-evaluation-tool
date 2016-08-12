/**
 * Copyright 2015
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
 */
package de.cau.cs.se.software.evaluation.graph;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import de.cau.cs.kieler.klighd.SynthesisOption;
import de.cau.cs.kieler.klighd.krendering.KColor;
import de.cau.cs.kieler.klighd.krendering.KContainerRendering;
import de.cau.cs.kieler.klighd.krendering.KEllipse;
import de.cau.cs.kieler.klighd.krendering.KGridPlacement;
import de.cau.cs.kieler.klighd.krendering.KPolyline;
import de.cau.cs.kieler.klighd.krendering.KRectangle;
import de.cau.cs.kieler.klighd.krendering.KRenderingFactory;
import de.cau.cs.kieler.klighd.krendering.KRoundedRectangle;
import de.cau.cs.kieler.klighd.krendering.KText;
import de.cau.cs.kieler.klighd.krendering.LineJoin;
import de.cau.cs.kieler.klighd.krendering.LineStyle;
import de.cau.cs.kieler.klighd.krendering.extensions.KColorExtensions;
import de.cau.cs.kieler.klighd.krendering.extensions.KContainerRenderingExtensions;
import de.cau.cs.kieler.klighd.krendering.extensions.KEdgeExtensions;
import de.cau.cs.kieler.klighd.krendering.extensions.KLabelExtensions;
import de.cau.cs.kieler.klighd.krendering.extensions.KNodeExtensions;
import de.cau.cs.kieler.klighd.krendering.extensions.KPolylineExtensions;
import de.cau.cs.kieler.klighd.krendering.extensions.KPortExtensions;
import de.cau.cs.kieler.klighd.krendering.extensions.KRenderingExtensions;
import de.cau.cs.kieler.klighd.syntheses.AbstractDiagramSynthesis;
import de.cau.cs.se.software.evaluation.graph.transformation.ManipulatePlanarGraph;
import de.cau.cs.se.software.evaluation.graph.transformation.PlanarEdge;
import de.cau.cs.se.software.evaluation.graph.transformation.PlanarNode;
import de.cau.cs.se.software.evaluation.graph.transformation.PlanarVisualizationGraph;
import de.cau.cs.se.software.evaluation.graph.transformation.VisualizationPlanarGraph;
import de.cau.cs.se.software.evaluation.hypergraph.EModuleKind;
import de.cau.cs.se.software.evaluation.hypergraph.Edge;
import de.cau.cs.se.software.evaluation.hypergraph.ModularHypergraph;
import de.cau.cs.se.software.evaluation.hypergraph.Module;
import de.cau.cs.se.software.evaluation.hypergraph.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.inject.Inject;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.klayoutdata.KInsets;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class ModularHypergraphDiagramSynthesis extends AbstractDiagramSynthesis<ModularHypergraph> {
  @Inject
  @Extension
  private KNodeExtensions _kNodeExtensions;
  
  @Inject
  @Extension
  private KEdgeExtensions _kEdgeExtensions;
  
  @Inject
  @Extension
  private KPortExtensions _kPortExtensions;
  
  @Inject
  @Extension
  private KLabelExtensions _kLabelExtensions;
  
  @Inject
  @Extension
  private KRenderingExtensions _kRenderingExtensions;
  
  @Inject
  @Extension
  private KContainerRenderingExtensions _kContainerRenderingExtensions;
  
  @Inject
  @Extension
  private KPolylineExtensions _kPolylineExtensions;
  
  @Inject
  @Extension
  private KColorExtensions _kColorExtensions;
  
  @Extension
  private KRenderingFactory _kRenderingFactory = KRenderingFactory.eINSTANCE;
  
  /**
   * changes in visualization nodes on off
   */
  private final static String VISIBLE_NODES_NAME = "Nodes Visible";
  
  private final static String VISIBLE_NODES_NO = "Modules only";
  
  private final static String VISIBLE_NODES_YES = "Show nodes in modules";
  
  /**
   * changes in visualization anonymous classes on off
   */
  private final static String VISIBLE_ANONYMOUS_NAME = "Anonymous Classes";
  
  private final static String VISIBLE_ANONYMOUS_NO = "hidden";
  
  private final static String VISIBLE_ANONYMOUS_YES = "visible";
  
  /**
   * changes in visualization anonymous classes on off
   */
  private final static String VISIBLE_FRAMEWORK_NAME = "Framework Classes";
  
  private final static String VISIBLE_FRAMEWORK_NO = "hidden";
  
  private final static String VISIBLE_FRAMEWORK_YES = "visible";
  
  /**
   * changes in layout direction
   */
  private final static String DIRECTION_NAME = "Layout Direction";
  
  private final static String DIRECTION_UP = "up";
  
  private final static String DIRECTION_DOWN = "down";
  
  private final static String DIRECTION_LEFT = "left";
  
  private final static String DIRECTION_RIGHT = "right";
  
  /**
   * changes in edge routing
   */
  private final static String ROUTING_NAME = "Edge Routing";
  
  private final static String ROUTING_POLYLINE = "polyline";
  
  private final static String ROUTING_ORTHOGONAL = "orthogonal";
  
  private final static String ROUTING_SPLINES = "splines";
  
  private final static String SPACING_NAME = "Spacing";
  
  /**
   * The filter option definition that allows users to customize the constructed class diagrams.
   */
  private final static SynthesisOption VISIBLE_NODES = SynthesisOption.createChoiceOption(ModularHypergraphDiagramSynthesis.VISIBLE_NODES_NAME, 
    ImmutableList.<String>of(ModularHypergraphDiagramSynthesis.VISIBLE_NODES_YES, ModularHypergraphDiagramSynthesis.VISIBLE_NODES_NO), ModularHypergraphDiagramSynthesis.VISIBLE_NODES_NO);
  
  private final static SynthesisOption VISIBLE_ANONYMOUS = SynthesisOption.createChoiceOption(ModularHypergraphDiagramSynthesis.VISIBLE_ANONYMOUS_NAME, 
    ImmutableList.<String>of(ModularHypergraphDiagramSynthesis.VISIBLE_ANONYMOUS_YES, ModularHypergraphDiagramSynthesis.VISIBLE_ANONYMOUS_NO), ModularHypergraphDiagramSynthesis.VISIBLE_ANONYMOUS_NO);
  
  private final static SynthesisOption VISIBLE_FRAMEWORK = SynthesisOption.createChoiceOption(ModularHypergraphDiagramSynthesis.VISIBLE_FRAMEWORK_NAME, 
    ImmutableList.<String>of(ModularHypergraphDiagramSynthesis.VISIBLE_FRAMEWORK_YES, ModularHypergraphDiagramSynthesis.VISIBLE_FRAMEWORK_NO), ModularHypergraphDiagramSynthesis.VISIBLE_FRAMEWORK_YES);
  
  private final static SynthesisOption DIRECTION = SynthesisOption.createChoiceOption(ModularHypergraphDiagramSynthesis.DIRECTION_NAME, 
    ImmutableList.<String>of(ModularHypergraphDiagramSynthesis.DIRECTION_UP, ModularHypergraphDiagramSynthesis.DIRECTION_DOWN, ModularHypergraphDiagramSynthesis.DIRECTION_LEFT, ModularHypergraphDiagramSynthesis.DIRECTION_RIGHT), ModularHypergraphDiagramSynthesis.DIRECTION_LEFT);
  
  private final static SynthesisOption ROUTING = SynthesisOption.createChoiceOption(ModularHypergraphDiagramSynthesis.ROUTING_NAME, 
    ImmutableList.<String>of(ModularHypergraphDiagramSynthesis.ROUTING_POLYLINE, ModularHypergraphDiagramSynthesis.ROUTING_ORTHOGONAL, ModularHypergraphDiagramSynthesis.ROUTING_SPLINES), ModularHypergraphDiagramSynthesis.ROUTING_ORTHOGONAL);
  
  private final static SynthesisOption SPACING = SynthesisOption.<Float>createRangeOption(ModularHypergraphDiagramSynthesis.SPACING_NAME, Float.valueOf(5f), Float.valueOf(200f), Float.valueOf(50f));
  
  private Map<Node, KNode> nodeMap = new HashMap<Node, KNode>();
  
  private Map<Module, KNode> moduleMap = new HashMap<Module, KNode>();
  
  private Map<String, KPort> portMap = new HashMap<String, KPort>();
  
  private ArrayList<Module> processedModules = new ArrayList<Module>();
  
  private Map<PlanarNode, KNode> planarNodeMap = new HashMap<PlanarNode, KNode>();
  
  /**
   * {@inheritDoc}<br>
   * <br>
   * Registers the diagram filter option declared above, which allow users to tailor the constructed diagrams.
   */
  @Override
  public List<SynthesisOption> getDisplayedSynthesisOptions() {
    return ImmutableList.<SynthesisOption>of(ModularHypergraphDiagramSynthesis.VISIBLE_NODES, ModularHypergraphDiagramSynthesis.VISIBLE_ANONYMOUS, ModularHypergraphDiagramSynthesis.VISIBLE_FRAMEWORK, ModularHypergraphDiagramSynthesis.DIRECTION, ModularHypergraphDiagramSynthesis.ROUTING, ModularHypergraphDiagramSynthesis.SPACING);
  }
  
  @Override
  public KNode transform(final ModularHypergraph hypergraph) {
    KNode _createNode = this._kNodeExtensions.createNode(hypergraph);
    final KNode root = this.<KNode>associateWith(_createNode, hypergraph);
    final Procedure1<KNode> _function = (KNode it) -> {
      this.<KNode, Boolean>setLayoutOption(it, LayeredOptions.LAYOUT_HIERARCHY, Boolean.valueOf(true));
      Object _objectValue = this.getObjectValue(ModularHypergraphDiagramSynthesis.SPACING);
      this.<KNode, Float>setLayoutOption(it, LayeredOptions.SPACING_NODE, ((Float) _objectValue));
      Direction _switchResult = null;
      Object _objectValue_1 = this.getObjectValue(ModularHypergraphDiagramSynthesis.DIRECTION);
      boolean _matched = false;
      if (Objects.equal(_objectValue_1, ModularHypergraphDiagramSynthesis.DIRECTION_UP)) {
        _matched=true;
        _switchResult = Direction.UP;
      }
      if (!_matched) {
        if (Objects.equal(_objectValue_1, ModularHypergraphDiagramSynthesis.DIRECTION_DOWN)) {
          _matched=true;
          _switchResult = Direction.DOWN;
        }
      }
      if (!_matched) {
        if (Objects.equal(_objectValue_1, ModularHypergraphDiagramSynthesis.DIRECTION_LEFT)) {
          _matched=true;
          _switchResult = Direction.LEFT;
        }
      }
      if (!_matched) {
        if (Objects.equal(_objectValue_1, ModularHypergraphDiagramSynthesis.DIRECTION_RIGHT)) {
          _matched=true;
          _switchResult = Direction.RIGHT;
        }
      }
      this.<KNode, Direction>setLayoutOption(it, LayeredOptions.DIRECTION, _switchResult);
      EdgeRouting _switchResult_1 = null;
      Object _objectValue_2 = this.getObjectValue(ModularHypergraphDiagramSynthesis.ROUTING);
      boolean _matched_1 = false;
      if (Objects.equal(_objectValue_2, ModularHypergraphDiagramSynthesis.ROUTING_POLYLINE)) {
        _matched_1=true;
        _switchResult_1 = EdgeRouting.POLYLINE;
      }
      if (!_matched_1) {
        if (Objects.equal(_objectValue_2, ModularHypergraphDiagramSynthesis.ROUTING_ORTHOGONAL)) {
          _matched_1=true;
          _switchResult_1 = EdgeRouting.ORTHOGONAL;
        }
      }
      if (!_matched_1) {
        if (Objects.equal(_objectValue_2, ModularHypergraphDiagramSynthesis.ROUTING_SPLINES)) {
          _matched_1=true;
          _switchResult_1 = EdgeRouting.SPLINES;
        }
      }
      this.<KNode, EdgeRouting>setLayoutOption(it, LayeredOptions.EDGE_ROUTING, _switchResult_1);
    };
    ObjectExtensions.<KNode>operator_doubleArrow(root, _function);
    Object _objectValue = this.getObjectValue(ModularHypergraphDiagramSynthesis.VISIBLE_NODES);
    boolean _equals = Objects.equal(_objectValue, ModularHypergraphDiagramSynthesis.VISIBLE_NODES_NO);
    if (_equals) {
      final VisualizationPlanarGraph generatePlanarGraph = new VisualizationPlanarGraph();
      Object _objectValue_1 = this.getObjectValue(ModularHypergraphDiagramSynthesis.VISIBLE_FRAMEWORK);
      boolean _equals_1 = Objects.equal(_objectValue_1, ModularHypergraphDiagramSynthesis.VISIBLE_FRAMEWORK_YES);
      Object _objectValue_2 = this.getObjectValue(ModularHypergraphDiagramSynthesis.VISIBLE_ANONYMOUS);
      boolean _equals_2 = Objects.equal(_objectValue_2, ModularHypergraphDiagramSynthesis.VISIBLE_ANONYMOUS_YES);
      final ManipulatePlanarGraph manipulateGraph = new ManipulatePlanarGraph(_equals_1, _equals_2, 
        true);
      PlanarVisualizationGraph _generate = generatePlanarGraph.generate(hypergraph);
      final PlanarVisualizationGraph planarGraph = manipulateGraph.generate(_generate);
      EList<PlanarNode> _nodes = planarGraph.getNodes();
      final Consumer<PlanarNode> _function_1 = (PlanarNode planarNode) -> {
        EList<KNode> _children = root.getChildren();
        KNode _createEmptyModule = this.createEmptyModule(planarNode);
        _children.add(_createEmptyModule);
      };
      _nodes.forEach(_function_1);
      EList<PlanarEdge> _edges = planarGraph.getEdges();
      final Consumer<PlanarEdge> _function_2 = (PlanarEdge planarEdge) -> {
        EList<PlanarNode> _nodes_1 = planarGraph.getNodes();
        this.createAggregatedModuleEdge(planarEdge, _nodes_1);
      };
      _edges.forEach(_function_2);
    } else {
      final Procedure1<KNode> _function_3 = (KNode it) -> {
        EList<Module> _modules = hypergraph.getModules();
        final Consumer<Module> _function_4 = (Module module) -> {
          EList<KNode> _children = root.getChildren();
          KNode _createModuleWithNodes = this.createModuleWithNodes(module);
          _children.add(_createModuleWithNodes);
        };
        _modules.forEach(_function_4);
        EList<Edge> _edges_1 = hypergraph.getEdges();
        final Consumer<Edge> _function_5 = (Edge edge) -> {
          EList<Node> _nodes_1 = hypergraph.getNodes();
          EList<KNode> _children = root.getChildren();
          this.createGraphEdge(edge, _nodes_1, _children);
        };
        _edges_1.forEach(_function_5);
      };
      ObjectExtensions.<KNode>operator_doubleArrow(root, _function_3);
    }
    return root;
  }
  
  /**
   * Return the correct color for a module.
   */
  private KColor getBackgroundColor(final EModuleKind kind) {
    KColor _switchResult = null;
    if (kind != null) {
      switch (kind) {
        case SYSTEM:
          _switchResult = this._kColorExtensions.getColor("LemonChiffon");
          break;
        case FRAMEWORK:
          _switchResult = this._kColorExtensions.getColor("Blue");
          break;
        case ANONYMOUS:
          _switchResult = this._kColorExtensions.getColor("Orange");
          break;
        case INTERFACE:
          _switchResult = this._kColorExtensions.getColor("White");
          break;
        default:
          break;
      }
    }
    return _switchResult;
  }
  
  /**
   * Create an edge in the correct width for an aggregated edge.
   */
  private KEdge createAggregatedModuleEdge(final PlanarEdge edge, final List<PlanarNode> nodes) {
    KEdge _xblockexpression = null;
    {
      PlanarNode _start = edge.getStart();
      final KNode sourceNode = this.planarNodeMap.get(_start);
      PlanarNode _end = edge.getEnd();
      final KNode targetNode = this.planarNodeMap.get(_end);
      int _xifexpression = (int) 0;
      int _count = edge.getCount();
      boolean _lessEqualsThan = (_count <= 1);
      if (_lessEqualsThan) {
        _xifexpression = 1;
      } else {
        int _xifexpression_1 = (int) 0;
        int _count_1 = edge.getCount();
        boolean _lessEqualsThan_1 = (_count_1 <= 3);
        if (_lessEqualsThan_1) {
          _xifexpression_1 = 2;
        } else {
          int _xifexpression_2 = (int) 0;
          int _count_2 = edge.getCount();
          boolean _lessEqualsThan_2 = (_count_2 <= 7);
          if (_lessEqualsThan_2) {
            _xifexpression_2 = 3;
          } else {
            int _xifexpression_3 = (int) 0;
            int _count_3 = edge.getCount();
            boolean _lessEqualsThan_3 = (_count_3 <= 10);
            if (_lessEqualsThan_3) {
              _xifexpression_3 = 4;
            } else {
              int _xifexpression_4 = (int) 0;
              int _count_4 = edge.getCount();
              boolean _lessEqualsThan_4 = (_count_4 <= 15);
              if (_lessEqualsThan_4) {
                _xifexpression_4 = 5;
              } else {
                int _xifexpression_5 = (int) 0;
                int _count_5 = edge.getCount();
                boolean _lessEqualsThan_5 = (_count_5 <= 20);
                if (_lessEqualsThan_5) {
                  _xifexpression_5 = 6;
                } else {
                  _xifexpression_5 = 7;
                }
                _xifexpression_4 = _xifexpression_5;
              }
              _xifexpression_3 = _xifexpression_4;
            }
            _xifexpression_2 = _xifexpression_3;
          }
          _xifexpression_1 = _xifexpression_2;
        }
        _xifexpression = _xifexpression_1;
      }
      final int lineWidth = _xifexpression;
      final int portSize = (lineWidth + 2);
      KPort _createPort = this._kPortExtensions.createPort();
      final Procedure1<KPort> _function = (KPort it) -> {
        this._kPortExtensions.setPortSize(it, portSize, portSize);
        KRectangle _addRectangle = this._kRenderingExtensions.addRectangle(it);
        KColor _color = this._kColorExtensions.getColor("black");
        KRectangle _setBackground = this._kRenderingExtensions.<KRectangle>setBackground(_addRectangle, _color);
        this._kRenderingExtensions.setLineJoin(_setBackground, LineJoin.JOIN_ROUND);
      };
      final KPort sourcePort = ObjectExtensions.<KPort>operator_doubleArrow(_createPort, _function);
      KPort _createPort_1 = this._kPortExtensions.createPort();
      final Procedure1<KPort> _function_1 = (KPort it) -> {
        this._kPortExtensions.setPortSize(it, portSize, portSize);
        KRectangle _addRectangle = this._kRenderingExtensions.addRectangle(it);
        KColor _color = this._kColorExtensions.getColor("black");
        KRectangle _setBackground = this._kRenderingExtensions.<KRectangle>setBackground(_addRectangle, _color);
        this._kRenderingExtensions.setLineJoin(_setBackground, LineJoin.JOIN_ROUND);
      };
      final KPort targetPort = ObjectExtensions.<KPort>operator_doubleArrow(_createPort_1, _function_1);
      EList<KPort> _ports = sourceNode.getPorts();
      _ports.add(sourcePort);
      EList<KPort> _ports_1 = targetNode.getPorts();
      _ports_1.add(targetPort);
      KEdge _createEdge = this._kEdgeExtensions.createEdge();
      final Procedure1<KEdge> _function_2 = (KEdge it) -> {
        it.setSource(sourceNode);
        it.setSourcePort(sourcePort);
        it.setTarget(targetNode);
        it.setTargetPort(targetPort);
        KPolyline _addPolyline = this._kEdgeExtensions.addPolyline(it);
        final Procedure1<KPolyline> _function_3 = (KPolyline it_1) -> {
          this._kRenderingExtensions.setLineWidth(it_1, lineWidth);
          this._kRenderingExtensions.setLineStyle(it_1, LineStyle.SOLID);
        };
        ObjectExtensions.<KPolyline>operator_doubleArrow(_addPolyline, _function_3);
      };
      _xblockexpression = ObjectExtensions.<KEdge>operator_doubleArrow(_createEdge, _function_2);
    }
    return _xblockexpression;
  }
  
  /**
   * Create module without nodes for simple display.
   */
  private KNode createEmptyModule(final PlanarNode planarNode) {
    KNode _xblockexpression = null;
    {
      final KNode moduleNode = this._kNodeExtensions.createNode(planarNode);
      this.planarNodeMap.put(planarNode, moduleNode);
      final Procedure1<KNode> _function = (KNode it) -> {
        this.<KNode, Float>setLayoutOption(it, LayeredOptions.PORT_BORDER_OFFSET, Float.valueOf(20f));
        KRoundedRectangle _addRoundedRectangle = this._kRenderingExtensions.addRoundedRectangle(it, 10, 10);
        final Procedure1<KRoundedRectangle> _function_1 = (KRoundedRectangle it_1) -> {
          this._kRenderingExtensions.setLineWidth(it_1, 2);
          KColor _color = this._kColorExtensions.getColor("white");
          EModuleKind _kind = planarNode.getKind();
          KColor _backgroundColor = this.getBackgroundColor(_kind);
          this._kRenderingExtensions.<KRoundedRectangle>setBackgroundGradient(it_1, _color, _backgroundColor, 0);
          KColor _color_1 = this._kColorExtensions.getColor("black");
          this._kRenderingExtensions.setShadow(it_1, _color_1);
          KGridPlacement _setGridPlacement = this._kContainerRenderingExtensions.setGridPlacement(it_1, 1);
          KGridPlacement _from = this._kRenderingExtensions.from(_setGridPlacement, this._kRenderingExtensions.LEFT, 10, 0, this._kRenderingExtensions.TOP, 20, 0);
          this._kRenderingExtensions.to(_from, this._kRenderingExtensions.RIGHT, 10, 0, this._kRenderingExtensions.BOTTOM, 20, 0);
          String _context = planarNode.getContext();
          KText _addText = this._kContainerRenderingExtensions.addText(it_1, _context);
          final Procedure1<KText> _function_2 = (KText it_2) -> {
            this._kRenderingExtensions.setFontBold(it_2, false);
            it_2.setCursorSelectable(false);
            this._kRenderingExtensions.<KText>setLeftTopAlignedPointPlacementData(it_2, 1, 1, 1, 1);
          };
          ObjectExtensions.<KText>operator_doubleArrow(_addText, _function_2);
          String _name = planarNode.getName();
          KText _addText_1 = this._kContainerRenderingExtensions.addText(it_1, _name);
          final Procedure1<KText> _function_3 = (KText it_2) -> {
            this._kRenderingExtensions.setFontBold(it_2, true);
            it_2.setCursorSelectable(true);
            this._kRenderingExtensions.<KText>setLeftTopAlignedPointPlacementData(it_2, 1, 1, 1, 1);
          };
          ObjectExtensions.<KText>operator_doubleArrow(_addText_1, _function_3);
        };
        ObjectExtensions.<KRoundedRectangle>operator_doubleArrow(_addRoundedRectangle, _function_1);
      };
      _xblockexpression = ObjectExtensions.<KNode>operator_doubleArrow(moduleNode, _function);
    }
    return _xblockexpression;
  }
  
  /**
   * Draw module as a rectangle with its nodes inside.
   * 
   * @param module the module to be rendered
   */
  private KNode createModuleWithNodes(final Module module) {
    KNode _xblockexpression = null;
    {
      KNode _createNode = this._kNodeExtensions.createNode(module);
      final KNode moduleNode = this.<KNode>associateWith(_createNode, module);
      KShapeLayout _data = moduleNode.<KShapeLayout>getData(KShapeLayout.class);
      KInsets _insets = _data.getInsets();
      _insets.setTop(15);
      final Procedure1<KNode> _function = (KNode it) -> {
        this.<KNode, PortConstraints>setLayoutOption(it, LayeredOptions.PORT_CONSTRAINTS, PortConstraints.FREE);
        this.<KNode, EdgeRouting>setLayoutOption(it, LayeredOptions.EDGE_ROUTING, EdgeRouting.POLYLINE);
        KRoundedRectangle _addRoundedRectangle = this._kRenderingExtensions.addRoundedRectangle(it, 10, 10);
        final Procedure1<KRoundedRectangle> _function_1 = (KRoundedRectangle it_1) -> {
          this._kRenderingExtensions.setLineWidth(it_1, 2);
          KColor _color = this._kColorExtensions.getColor("white");
          EModuleKind _kind = module.getKind();
          KColor _backgroundColor = this.getBackgroundColor(_kind);
          this._kRenderingExtensions.<KRoundedRectangle>setBackgroundGradient(it_1, _color, _backgroundColor, 0);
          KColor _color_1 = this._kColorExtensions.getColor("black");
          this._kRenderingExtensions.setShadow(it_1, _color_1);
          KGridPlacement _setGridPlacement = this._kContainerRenderingExtensions.setGridPlacement(it_1, 1);
          KGridPlacement _from = this._kRenderingExtensions.from(_setGridPlacement, this._kRenderingExtensions.LEFT, 10, 0, this._kRenderingExtensions.TOP, 10, 0);
          this._kRenderingExtensions.to(_from, this._kRenderingExtensions.RIGHT, 10, 0, this._kRenderingExtensions.BOTTOM, 10, 0);
          String _name = module.getName();
          KText _addText = this._kContainerRenderingExtensions.addText(it_1, _name);
          final Procedure1<KText> _function_2 = (KText it_2) -> {
            this._kRenderingExtensions.setFontBold(it_2, true);
            it_2.setCursorSelectable(true);
            this._kRenderingExtensions.<KText>setLeftTopAlignedPointPlacementData(it_2, 1, 1, 1, 1);
          };
          ObjectExtensions.<KText>operator_doubleArrow(_addText, _function_2);
          this._kContainerRenderingExtensions.addChildArea(it_1);
          EList<Node> _nodes = module.getNodes();
          final Consumer<Node> _function_3 = (Node node) -> {
            this.createGraphNode(node, it_1, module, moduleNode);
          };
          _nodes.forEach(_function_3);
        };
        ObjectExtensions.<KRoundedRectangle>operator_doubleArrow(_addRoundedRectangle, _function_1);
      };
      _xblockexpression = ObjectExtensions.<KNode>operator_doubleArrow(moduleNode, _function);
    }
    return _xblockexpression;
  }
  
  /**
   * Draw a single node as a circle with its name at the center.
   * 
   * @param node the node to be rendered
   */
  private KNode createGraphNode(final Node node, final KContainerRendering parent, final Module module, final KNode moduleNode) {
    KNode _xblockexpression = null;
    {
      KNode _createNode = this._kNodeExtensions.createNode(node);
      final KNode kNode = this.<KNode>associateWith(_createNode, node);
      this.nodeMap.put(node, kNode);
      EList<KNode> _children = moduleNode.getChildren();
      _children.add(kNode);
      final Procedure1<KNode> _function = (KNode it) -> {
        this.<KNode, PortConstraints>setLayoutOption(it, LayeredOptions.PORT_CONSTRAINTS, PortConstraints.FREE);
        KRoundedRectangle _addRoundedRectangle = this._kRenderingExtensions.addRoundedRectangle(it, 2, 2);
        final Procedure1<KRoundedRectangle> _function_1 = (KRoundedRectangle it_1) -> {
          this._kRenderingExtensions.setLineWidth(it_1, 2);
          KColor _color = this._kColorExtensions.getColor("white");
          this._kRenderingExtensions.setBackground(it_1, _color);
          this._kRenderingExtensions.<KRoundedRectangle>setSurroundingSpace(it_1, 1, 0, 1, 0);
          String _name = node.getName();
          String _name_1 = module.getName();
          int _length = _name_1.length();
          int _plus = (_length + 1);
          String _substring = _name.substring(_plus);
          KText _addText = this._kContainerRenderingExtensions.addText(it_1, _substring);
          final Procedure1<KText> _function_2 = (KText it_2) -> {
            this._kRenderingExtensions.<KText>setSurroundingSpace(it_2, 10, 0, 10, 0);
            it_2.setCursorSelectable(true);
          };
          ObjectExtensions.<KText>operator_doubleArrow(_addText, _function_2);
        };
        ObjectExtensions.<KRoundedRectangle>operator_doubleArrow(_addRoundedRectangle, _function_1);
      };
      _xblockexpression = ObjectExtensions.<KNode>operator_doubleArrow(kNode, _function);
    }
    return _xblockexpression;
  }
  
  private KPort getOrCreateEdgePort(final KNode kNode, final String label) {
    KPort _get = this.portMap.get(label);
    boolean _equals = Objects.equal(_get, null);
    if (_equals) {
      EList<KPort> _ports = kNode.getPorts();
      KPort _createPort = this._kPortExtensions.createPort();
      final Procedure1<KPort> _function = (KPort it) -> {
        this._kPortExtensions.setPortSize(it, 2, 2);
        KRectangle _addRectangle = this._kRenderingExtensions.addRectangle(it);
        KColor _color = this._kColorExtensions.getColor("black");
        KRectangle _setBackground = this._kRenderingExtensions.<KRectangle>setBackground(_addRectangle, _color);
        this._kRenderingExtensions.setLineJoin(_setBackground, LineJoin.JOIN_ROUND);
        this.portMap.put(label, it);
      };
      KPort _doubleArrow = ObjectExtensions.<KPort>operator_doubleArrow(_createPort, _function);
      _ports.add(_doubleArrow);
    }
    return this.portMap.get(label);
  }
  
  private Boolean createGraphEdge(final Edge edge, final EList<Node> nodes, final EList<KNode> siblings) {
    boolean _xblockexpression = false;
    {
      final Function1<Node, Boolean> _function = (Node node) -> {
        EList<Edge> _edges = node.getEdges();
        final Function1<Edge, Boolean> _function_1 = (Edge it) -> {
          return Boolean.valueOf(it.equals(edge));
        };
        return Boolean.valueOf(IterableExtensions.<Edge>exists(_edges, _function_1));
      };
      final Iterable<Node> referencedNodes = IterableExtensions.<Node>filter(nodes, _function);
      boolean _xifexpression = false;
      int _size = IterableExtensions.size(referencedNodes);
      boolean _greaterThan = (_size > 1);
      if (_greaterThan) {
        boolean _xifexpression_1 = false;
        int _size_1 = IterableExtensions.size(referencedNodes);
        boolean _equals = (_size_1 == 2);
        if (_equals) {
          Object _get = ((Object[])Conversions.unwrapArray(referencedNodes, Object.class))[0];
          KNode _get_1 = this.nodeMap.get(_get);
          Object _get_2 = ((Object[])Conversions.unwrapArray(referencedNodes, Object.class))[1];
          KNode _get_3 = this.nodeMap.get(_get_2);
          this.constructEdge(_get_1, _get_3, edge);
        } else {
          KNode _drawHyperEdge = this.drawHyperEdge(edge, referencedNodes);
          _xifexpression_1 = siblings.add(_drawHyperEdge);
        }
        _xifexpression = _xifexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return Boolean.valueOf(_xblockexpression);
  }
  
  private KNode drawHyperEdge(final Edge graphEdge, final Iterable<Node> nodes) {
    KNode _createNode = this._kNodeExtensions.createNode(graphEdge);
    final Procedure1<KNode> _function = (KNode it) -> {
      KEllipse _addEllipse = this._kRenderingExtensions.addEllipse(it);
      final Procedure1<KEllipse> _function_1 = (KEllipse it_1) -> {
        this._kRenderingExtensions.setLineWidth(it_1, 2);
      };
      ObjectExtensions.<KEllipse>operator_doubleArrow(_addEllipse, _function_1);
    };
    final KNode edgeNode = ObjectExtensions.<KNode>operator_doubleArrow(_createNode, _function);
    final Consumer<Node> _function_1 = (Node node) -> {
      KNode _get = this.nodeMap.get(node);
      this.constructEdge(edgeNode, _get, graphEdge);
    };
    nodes.forEach(_function_1);
    return edgeNode;
  }
  
  private void constructEdge(final KNode left, final KNode right, final Edge graphEdge) {
    KNode _parent = right.getParent();
    KNode _parent_1 = left.getParent();
    boolean _equals = _parent.equals(_parent_1);
    if (_equals) {
      String _string = left.toString();
      String _plus = (_string + "_to_");
      String _string_1 = right.toString();
      String _plus_1 = (_plus + _string_1);
      KPort _orCreateEdgePort = this.getOrCreateEdgePort(left, _plus_1);
      String _string_2 = right.toString();
      String _plus_2 = (_string_2 + "_to_");
      String _string_3 = left.toString();
      String _plus_3 = (_plus_2 + _string_3);
      KPort _orCreateEdgePort_1 = this.getOrCreateEdgePort(right, _plus_3);
      this.drawEdge(left, right, _orCreateEdgePort, _orCreateEdgePort_1);
    } else {
      KNode _parent_2 = left.getParent();
      boolean _notEquals = (!Objects.equal(_parent_2, null));
      if (_notEquals) {
        String _string_4 = left.toString();
        String _plus_4 = (_string_4 + "_to_");
        KNode _parent_3 = right.getParent();
        String _string_5 = _parent_3.toString();
        String _plus_5 = (_plus_4 + _string_5);
        KPort _get = this.portMap.get(_plus_5);
        boolean _equals_1 = Objects.equal(_get, null);
        if (_equals_1) {
          KNode _parent_4 = left.getParent();
          String _string_6 = left.toString();
          String _plus_6 = (_string_6 + "_to_");
          KNode _parent_5 = right.getParent();
          String _string_7 = _parent_5.toString();
          String _plus_7 = (_plus_6 + _string_7);
          KPort _orCreateEdgePort_2 = this.getOrCreateEdgePort(left, _plus_7);
          KNode _parent_6 = left.getParent();
          KNode _parent_7 = left.getParent();
          String _string_8 = _parent_7.toString();
          String _plus_8 = (_string_8 + "_to_");
          KNode _parent_8 = right.getParent();
          String _string_9 = _parent_8.toString();
          String _plus_9 = (_plus_8 + _string_9);
          KPort _orCreateEdgePort_3 = this.getOrCreateEdgePort(_parent_6, _plus_9);
          this.drawEdge(left, _parent_4, _orCreateEdgePort_2, _orCreateEdgePort_3);
        }
        KNode _parent_9 = right.getParent();
        String _string_10 = _parent_9.toString();
        String _plus_10 = (_string_10 + "_to_");
        KNode _parent_10 = left.getParent();
        String _string_11 = _parent_10.toString();
        String _plus_11 = (_plus_10 + _string_11);
        KPort _get_1 = this.portMap.get(_plus_11);
        boolean _equals_2 = Objects.equal(_get_1, null);
        if (_equals_2) {
          KNode _parent_11 = left.getParent();
          KNode _parent_12 = right.getParent();
          KNode _parent_13 = left.getParent();
          KNode _parent_14 = left.getParent();
          String _string_12 = _parent_14.toString();
          String _plus_12 = (_string_12 + "_to_");
          KNode _parent_15 = right.getParent();
          String _string_13 = _parent_15.toString();
          String _plus_13 = (_plus_12 + _string_13);
          KPort _orCreateEdgePort_4 = this.getOrCreateEdgePort(_parent_13, _plus_13);
          KNode _parent_16 = right.getParent();
          KNode _parent_17 = right.getParent();
          String _string_14 = _parent_17.toString();
          String _plus_14 = (_string_14 + "_to_");
          KNode _parent_18 = left.getParent();
          String _string_15 = _parent_18.toString();
          String _plus_15 = (_plus_14 + _string_15);
          KPort _orCreateEdgePort_5 = this.getOrCreateEdgePort(_parent_16, _plus_15);
          this.drawEdge(_parent_11, _parent_12, _orCreateEdgePort_4, _orCreateEdgePort_5);
        }
        String _string_16 = right.toString();
        String _plus_16 = (_string_16 + "_to_");
        KNode _parent_19 = left.getParent();
        String _string_17 = _parent_19.toString();
        String _plus_17 = (_plus_16 + _string_17);
        KPort _get_2 = this.portMap.get(_plus_17);
        boolean _equals_3 = Objects.equal(_get_2, null);
        if (_equals_3) {
          KNode _parent_20 = right.getParent();
          String _string_18 = right.toString();
          String _plus_18 = (_string_18 + "_to_");
          KNode _parent_21 = left.getParent();
          String _string_19 = _parent_21.toString();
          String _plus_19 = (_plus_18 + _string_19);
          KPort _orCreateEdgePort_6 = this.getOrCreateEdgePort(right, _plus_19);
          KNode _parent_22 = right.getParent();
          KNode _parent_23 = right.getParent();
          String _string_20 = _parent_23.toString();
          String _plus_20 = (_string_20 + "_to_");
          KNode _parent_24 = left.getParent();
          String _string_21 = _parent_24.toString();
          String _plus_21 = (_plus_20 + _string_21);
          KPort _orCreateEdgePort_7 = this.getOrCreateEdgePort(_parent_22, _plus_21);
          this.drawEdge(right, _parent_20, _orCreateEdgePort_6, _orCreateEdgePort_7);
        }
      } else {
        KNode _parent_25 = right.getParent();
        String _string_22 = left.toString();
        KPort _orCreateEdgePort_8 = this.getOrCreateEdgePort(left, _string_22);
        KNode _parent_26 = right.getParent();
        KNode _parent_27 = right.getParent();
        String _string_23 = _parent_27.toString();
        String _plus_22 = (_string_23 + "_to_");
        String _string_24 = left.toString();
        String _plus_23 = (_plus_22 + _string_24);
        KPort _orCreateEdgePort_9 = this.getOrCreateEdgePort(_parent_26, _plus_23);
        this.drawEdge(left, _parent_25, _orCreateEdgePort_8, _orCreateEdgePort_9);
        KNode _parent_28 = right.getParent();
        String _string_25 = right.toString();
        String _plus_24 = (_string_25 + "_to_");
        String _string_26 = left.toString();
        String _plus_25 = (_plus_24 + _string_26);
        KPort _orCreateEdgePort_10 = this.getOrCreateEdgePort(right, _plus_25);
        KNode _parent_29 = right.getParent();
        KNode _parent_30 = right.getParent();
        String _string_27 = _parent_30.toString();
        String _plus_26 = (_string_27 + "_to_");
        String _string_28 = left.toString();
        String _plus_27 = (_plus_26 + _string_28);
        KPort _orCreateEdgePort_11 = this.getOrCreateEdgePort(_parent_29, _plus_27);
        this.drawEdge(right, _parent_28, _orCreateEdgePort_10, _orCreateEdgePort_11);
      }
    }
  }
  
  /**
   * Draw a single edge.
   */
  private void drawEdge(final KNode left, final KNode right, final KPort leftPort, final KPort rightPort) {
    KEdge _createEdge = this._kEdgeExtensions.createEdge();
    final Procedure1<KEdge> _function = (KEdge it) -> {
      it.setSource(left);
      it.setTarget(right);
      it.setSourcePort(leftPort);
      it.setTargetPort(rightPort);
      KPolyline _addPolyline = this._kEdgeExtensions.addPolyline(it);
      final Procedure1<KPolyline> _function_1 = (KPolyline it_1) -> {
        this._kRenderingExtensions.setLineWidth(it_1, 2);
        this._kRenderingExtensions.setLineStyle(it_1, LineStyle.SOLID);
      };
      ObjectExtensions.<KPolyline>operator_doubleArrow(_addPolyline, _function_1);
    };
    ObjectExtensions.<KEdge>operator_doubleArrow(_createEdge, _function);
  }
}
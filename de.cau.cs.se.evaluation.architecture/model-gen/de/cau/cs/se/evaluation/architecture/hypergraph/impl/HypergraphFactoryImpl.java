/**
 */
package de.cau.cs.se.evaluation.architecture.hypergraph.impl;

import de.cau.cs.se.evaluation.architecture.hypergraph.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class HypergraphFactoryImpl extends EFactoryImpl implements HypergraphFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static HypergraphFactory init() {
		try {
			HypergraphFactory theHypergraphFactory = (HypergraphFactory)EPackage.Registry.INSTANCE.getEFactory(HypergraphPackage.eNS_URI);
			if (theHypergraphFactory != null) {
				return theHypergraphFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new HypergraphFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HypergraphFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case HypergraphPackage.HYPERGRAPH: return createHypergraph();
			case HypergraphPackage.MODULAR_HYPERGRAPH: return createModularHypergraph();
			case HypergraphPackage.MODULE: return createModule();
			case HypergraphPackage.NODE: return createNode();
			case HypergraphPackage.EDGE: return createEdge();
			case HypergraphPackage.NAMED_ELEMENT: return createNamedElement();
			case HypergraphPackage.NODE_TRACE: return createNodeTrace();
			case HypergraphPackage.EDGE_TRACE: return createEdgeTrace();
			case HypergraphPackage.GENERIC_TRACE: return createGenericTrace();
			case HypergraphPackage.NODE_REFERENCE: return createNodeReference();
			case HypergraphPackage.EDGE_REFERENCE: return createEdgeReference();
			case HypergraphPackage.MODULE_TRACE: return createModuleTrace();
			case HypergraphPackage.MODULE_REFERENCE: return createModuleReference();
			case HypergraphPackage.TYPE_TRACE: return createTypeTrace();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Hypergraph createHypergraph() {
		HypergraphImpl hypergraph = new HypergraphImpl();
		return hypergraph;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModularHypergraph createModularHypergraph() {
		ModularHypergraphImpl modularHypergraph = new ModularHypergraphImpl();
		return modularHypergraph;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Module createModule() {
		ModuleImpl module = new ModuleImpl();
		return module;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Node createNode() {
		NodeImpl node = new NodeImpl();
		return node;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Edge createEdge() {
		EdgeImpl edge = new EdgeImpl();
		return edge;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NamedElement createNamedElement() {
		NamedElementImpl namedElement = new NamedElementImpl();
		return namedElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NodeTrace createNodeTrace() {
		NodeTraceImpl nodeTrace = new NodeTraceImpl();
		return nodeTrace;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EdgeTrace createEdgeTrace() {
		EdgeTraceImpl edgeTrace = new EdgeTraceImpl();
		return edgeTrace;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GenericTrace createGenericTrace() {
		GenericTraceImpl genericTrace = new GenericTraceImpl();
		return genericTrace;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NodeReference createNodeReference() {
		NodeReferenceImpl nodeReference = new NodeReferenceImpl();
		return nodeReference;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EdgeReference createEdgeReference() {
		EdgeReferenceImpl edgeReference = new EdgeReferenceImpl();
		return edgeReference;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModuleTrace createModuleTrace() {
		ModuleTraceImpl moduleTrace = new ModuleTraceImpl();
		return moduleTrace;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModuleReference createModuleReference() {
		ModuleReferenceImpl moduleReference = new ModuleReferenceImpl();
		return moduleReference;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TypeTrace createTypeTrace() {
		TypeTraceImpl typeTrace = new TypeTraceImpl();
		return typeTrace;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HypergraphPackage getHypergraphPackage() {
		return (HypergraphPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static HypergraphPackage getPackage() {
		return HypergraphPackage.eINSTANCE;
	}

} //HypergraphFactoryImpl

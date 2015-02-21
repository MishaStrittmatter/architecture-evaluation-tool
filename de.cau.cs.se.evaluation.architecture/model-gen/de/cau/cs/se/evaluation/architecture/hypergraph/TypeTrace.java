/**
 */
package de.cau.cs.se.evaluation.architecture.hypergraph;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Type Trace</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link de.cau.cs.se.evaluation.architecture.hypergraph.TypeTrace#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see de.cau.cs.se.evaluation.architecture.hypergraph.HypergraphPackage#getTypeTrace()
 * @model
 * @generated
 */
public interface TypeTrace extends ModuleReference {
	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see #setType(Object)
	 * @see de.cau.cs.se.evaluation.architecture.hypergraph.HypergraphPackage#getTypeTrace_Type()
	 * @model required="true"
	 * @generated
	 */
	Object getType();

	/**
	 * Sets the value of the '{@link de.cau.cs.se.evaluation.architecture.hypergraph.TypeTrace#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(Object value);

} // TypeTrace

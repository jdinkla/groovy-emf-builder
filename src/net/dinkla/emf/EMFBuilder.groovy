/*
	Copyright (C) 2007 Joern Dinkla, www.dinkla.net, joern@dinkla.net

	Thanks to laszlo.suto@gmail.com for providing a patch to enable processing 
	of UML2 files.
	
	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
*/

package net.dinkla.emf

import java.lang.reflect.Method

import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EFactory
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.impl.EObjectImpl

import groovy.util.BuilderSupport

/**
*/

class EMFBuilder extends BuilderSupport {

	// a flag for debugging
	boolean debug = false
	
	// The EMF factory that this builder uses. All the objects are created by this factory.
	private final EFactory factory
	
	// A map of names to methods
    private def methodsMap = null
    
    // A map of parents to ?
    private def parentsMap = [:]

	// The name of a default feature, e.g. name
	String defaultFeature = 'name'
	
	// The last node created
	def currentNode
	
	/**
		Constructs an EMFBuilder from an EFactory instance.
	 */
    EMFBuilder(EFactory factory) {
    	super()
    	this.factory = factory
    }

	/**
		Constructs an EMFBuilder from an EFactory class.
	*/
    EMFBuilder(Class factoryClass) {
    	super()
        factory = factoryClass.eINSTANCE
    }

	/**
	 */
    def createNode(name) {
    	debug "createNode($name)"
    	def object
		Method method = getMethod(name) 
		if ( null != method ) {
			object = method.invoke(factory)
		} else {
            // If there is no method with this name, it has to be a containment reference
            // If it is not, we will find out in the method setParent()
            object = new Reference(name)
		}
    	debug "createNode($name) returns ${object}"
    	currentNode = object
        return object
    }
    
	/**
	 */
    def createNode(name, value){
    	debug "createNode(${name}, ${value})"
    	def object = createNode(name)
    	if (!(object instanceof EObject )) 
    		throw new ScriptException("There is no EObject for '${name}'")
		// get the class
    	EClass clazz = object.eClass()
    	// get the structural default feature
		def structFeat = clazz.getEStructuralFeature(defaultFeature)
		if ( null == structFeat ) 
			throw new ScriptException("The structural feature '${defaultFeature}' is not known.")
		debug "setting structural feature '${structFeat.name}' to '${value}'"
		object.eSet(structFeat, value)
        return object
    }
    
	/**
	 */
    def createNode(name, Map attributes){
    	debug "createNode(${name}, ${attributes})"	
    	def object = createNode(name)
    	if (!(object instanceof EObject)) 
    		throw new ScriptException("There is no EObject for '${name}'")
    	// get the class
    	EClass clazz = object.eClass()
    	// set the attributes
    	for ( entry in attributes ) {
			def structFeat = clazz.getEStructuralFeature(entry.key)
    		if ( null == structFeat ) 
    			throw new ScriptException("The structural feature '${entry.key}' is not known.")
    		if ( null != entry.value ) {
        		debug "setting structural feature '${structFeat.name}' (key '${entry.key}') to '${entry.value}'"
    			object.eSet(structFeat, entry.value)
    		}
    	}
    	debug "createNode($name, $attributes) returns ${object}"
        return object
    }
    
	/**
	 */
    def createNode(name, Map attributes, value){
    	debug "createNode($name, $attributes, $value)"
    	return createNode(name, attributes + [ 'defaultFeature': value])
    }    
    
	/**
	 */
    void setParent(def parent, def child){
    	debug "setParent($parent, $child)"
    	
    	if (child instanceof Reference) {
    		debug "setParent case 1"
			child.parent = parent		// set the parent
			child.calculateReference()	// calculate the reference target
			debug "setParent changed child ${child}"
    	} else if (parent instanceof Reference) {
    		debug "setParent case 2"
    		def structuralFeature = 
    				parent.parent.eClass().getEAllStructuralFeatures().find { 
    					it.name == parent.name 
					} 
			debug "The structural feature is: ${structuralFeature}"
    		// Get the reference and check its cardinality
			if ( parent.reference.isMany() ) {
				debug "The reference ${parent.reference} 0..*"
				parent.parent.eGet(structuralFeature).add(child)
			} else {
				debug "The reference ${parent.reference} is 0..1"
				parent.parent.eSet(structuralFeature, child)
			}
    		debug "setParent changed parent ${parent}"
    	} else {
	    	throw new ScriptException("Reference missing ? '${child}' can't be a child of '${parent}'")
    	}    
    }

	/**
	 */
    void nodeCompleted(parent, node) {
    	debug "nodeCompleted($parent, $node)"
    }
    
	/**
	 */
    Object invokeMethod(String name, Object args) {
    	debug "invokeMethod:${name}(${args*.toString()})", false
    	def result = super.invokeMethod(name, args)
    	debug "invokeMethod returns '${result}'", false
    	return result
    }

	/**
	 	Returns the createNAME method from the factory.
	 */
    def getMethod(name) {
    	if ( null == methodsMap ) initializeMethods()
    	return methodsMap["create" + name]
    }
	
	/**
		Put alle the createXXX methods from the factor into the methodsMap
	 */
	private void initializeMethods() {
		methodsMap = [:]
		def methods = factory.class.getDeclaredMethods().grep { it.name =~ /^create/ }
		for ( m in methods ) {
			debug("initializeMethods: added method ${m.name}")
    		methodsMap.put(m.name, m)
		}
	}
	
	/**
	 * Prints a message if the flag debug is set.
	 */
	void debug(msg, indent = true) {
		if ( debug ) println ((indent ? '\t' : '') + msg)
	}
}



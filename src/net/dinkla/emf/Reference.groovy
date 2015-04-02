/*
	Copyright (C) 2007 Joern Dinkla, www.dinkla.net, joern@dinkla.net

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

import org.eclipse.emf.ecore.EClass

/**
	This class is for holding structural features of type EReference.

	In the builder we need to know the parent of a reference. This can only be
	done in the setParent() method in the Groovy Builder class. 
	
	But the name of the reference is only known in the createNode method.
*/

class Reference {

	String name
	def parent
	def reference
	
	Reference(name) {
		this.name = name
		this.reference = null
		this.parent = null
	}
		
    void calculateReference() {
    	// it is necessary, that the parent is set
		assert null != parent
    	if ( null != reference ) return 
    	// get the class of the parent
    	EClass clazz = parent.eClass()
    	if ( null == clazz ) 
    		throw new ScriptException("eClass for parent ${parent} is not known")
		// find the reference with the name 'name'
    	reference = clazz.getEAllStructuralFeatures().find { it.name == name }
    	if ( null == reference ) {
    		throw new ScriptException("Reference for class '${parent}' with name '${name}' does not exist.")
    	}
    }
    
    String toString() {
    	return super.toString() + " ${name}, ${parent}, ${reference}"
    }

}
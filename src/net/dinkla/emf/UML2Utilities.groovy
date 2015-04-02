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

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl
import org.eclipse.uml2.uml.resource.UMLResource

import org.eclipse.uml2.uml.PrimitiveType
import org.eclipse.uml2.uml.Model
import org.eclipse.uml2.uml.Profile
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.uml2.uml.UMLPackage
import org.eclipse.emf.ecore.resource.URIConverter

/**
*/
class UML2Utilities {

	private static final ResourceSet resourceSet 
	private static boolean isUml2Registered

	/**
	 * Saves the object as an UML2 file.
	 */
	 static def save(String fileName, object) {
		save(URI.createURI(fileName), object)
	}

	/**
	 * Saves the object as an UML2 file.
	 */
	 static def save(URI uri, object) {
		_register()
		def resource = resourceSet.createResource(uri)
		assert resource != null
		resource.contents << object 			
		resource.save(null)
		return resource
	}

	/**
	 * Loads the UML2 file
     */
	static def load(String fileName, def literal = UMLPackage.Literals.PACKAGE) {
		load(URI.createURI(fileName), literal)
	}

	/**
	 * Loads the UML2 file
     */
	static def load(URI uri, def literal = UMLPackage.Literals.PACKAGE) {
		def pckg
		_register()
		def resource = resourceSet.getResource(uri, true)
		return EcoreUtil.getObjectByType(resource.getContents(), literal)
	}

	/**
	*/
	static PrimitiveType importPrimitiveType(org.eclipse.uml2.uml.Package pckg, String name) {
		Model umlLibrary = (Model) load(URI.createURI(UMLResource.UML_PRIMITIVE_TYPES_LIBRARY_URI));
		PrimitiveType primitiveType = (PrimitiveType) umlLibrary.getOwnedType(name);
		pckg.createElementImport(primitiveType);
		return primitiveType;
	}

	/**
	*/
	static org.eclipse.uml2.uml.Class referenceMetaclass(Profile profile, String name) {
		Model umlMetamodel = (Model) load(URI.createURI(UMLResource.UML_METAMODEL_URI))
		org.eclipse.uml2.uml.Class metaclass = (org.eclipse.uml2.uml.Class) umlMetamodel.getOwnedType(name)
		profile.createMetaclassReference(metaclass)
		return metaclass;
	}

	/**
	*/
	static void registerResourceFactories() {
		Resource.Factory.Registry.INSTANCE.extensionToFactoryMap[UMLResource.FILE_EXTENSION] = UMLResource.Factory.INSTANCE
		isUml2Registered = true
	}
	
	/**
	*/
	static void registerPathmaps(URI uri) {
		def register = { pathmap, segment -> 
			URIConverter.URI_MAP.put(URI.createURI(pathmap), uri.appendSegment(segment).appendSegment(""))
		}
		register(UMLResource.LIBRARIES_PATHMAP, "libraries")
		register(UMLResource.METAMODELS_PATHMAP, "metamodels")
		register(UMLResource.PROFILES_PATHMAP, "profiles")
	}
	
	/**
	*/
	private static _register() {
		if (!isUml2Registered) {
			if (!resourceSet) resourceSet = new ResourceSetImpl()
			registerResourceFactories()
			isUml2Registered = true
		}
	}
}
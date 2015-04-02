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
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl

/**
*/
class EMFUtilities {
	
	/**
	 * Saves the object to XML.
	 *
	 * @param filename
	 * @param object
	 * @return the resource
	 */
	static def saveXML(String fileName, object) {
		def resource = new XMLResourceImpl(URI.createURI(fileName))
		assert resource != null
		resource.contents << object
		resource.save(null)
		return resource
	}

	/**
	 * Saves the object to XMI.
	 *
	 * @param filename
	 * @param object
	 * @return the resource
	 */
	 static def save(String fileName, object) {
	 	def resource = new XMIResourceImpl(URI.createURI(fileName))
		assert resource != null
		resource.contents << object
		resource.save(null)
		return resource
	}

	/**
		Loads a model
	*/
	static def loadXML(String fileName) {
		def fileURI = URI.createURI(fileName)
		def resource = new XMLResourceImpl(fileURI)
		assert resource != null
		resource.load(null)
		return resource
	}

	/**
		Loads a model
	*/
	static def load(String fileName) {
		def fileURI = URI.createURI(fileName)
		def resource = new XMIResourceImpl(fileURI)
		assert resource != null
		resource.load(null)
		return resource
	}

}
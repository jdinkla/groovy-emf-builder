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

package net.dinkla.emf.examples;

import org.eclipse.emf.ecore.EcoreFactory
import org.eclipse.emf.ecore.EcorePackage
import net.dinkla.emf.EMFBuilder
import net.dinkla.emf.EMFUtilities

/**
	This class creates the library example from the Eclipse EMF documentation.
	See http://help.eclipse.org/help32/index.jsp?topic=/org.eclipse.emf.doc/tutorials/clibmod/clibmod.html.
*/
class Library {

	// the name of the file
	final String fileName = 'data/generated_library.ecore'
	
	// creates the library and saves it to a file
	void create() {
		def builder = new EMFBuilder(EcoreFactory)
		builder.debug = false
		// references to the model classes
		def book, library, writer, bookCat
		def pckg = builder.EPackage ( name: 'library', nsPrefix : 'net.dinkla.emf.library', nsURI: 'http://www.dinkla.net/library.ecore') {
			eClassifiers {
				book = EClass( name: 'Book' ) {
					eStructuralFeatures {
						EAttribute( name: 'title', eType: EcorePackage.Literals.ESTRING )
						EAttribute( name: 'pages', eType: EcorePackage.Literals.EINT, defaultValueLiteral: '100' )
						EAttribute( name: 'category' /* eType: XXX */ ) 
						// eType can't be set, because BookCategory is not defined yet
						EReference( name: 'author', lowerBound: 1 /* eType: XXX, eOpposite: YYY */) 
						// eType can't be set, because Writer is not defined yet
					}
				}
				library = EClass( name: 'Library' ) {
					eStructuralFeatures {
						EAttribute( name: 'name', eType: EcorePackage.Literals.ESTRING )
						EReference( name: 'writers', lowerBound: 0, upperBound: -1, containment: true /* eType: XXX */) 
						// eType can't be set here, because Writer is not defined yet						
						EReference( name: 'books', lowerBound: 0, upperBound: -1, containment: true, eType: book)
					}
				}
				writer = EClass( name: 'Writer' ) {
					eStructuralFeatures {
						EAttribute( name: 'name', eType: EcorePackage.Literals.ESTRING )
						EReference( name: 'books', lowerBound: 0, upperBound: -1, eType: book /* eOpposite: YYY */ )
					}
				}
				bookCat = EEnum( name: 'BookCategory' ) {
					eLiterals {
						EEnumLiteral( name: 'Mystery' )
						EEnumLiteral( name: 'ScienceFiction', value: 1 )
						EEnumLiteral( name: 'Biography', value: 2 )
					}
				}
			}
			// set the eTypes (see notes above)
			book.eStructuralFeatures.find { it.name == 'category' }.eType = bookCat
			book.eStructuralFeatures.find { it.name == 'author' }.eType = writer
			library.eStructuralFeatures.find { it.name == 'writers' }.eType = writer
			// set opposite references for Book.author and Writer.books
			def refAuthor = book.eStructuralFeatures.find { it.name == 'author' }
			def refBooks = writer.eStructuralFeatures.find { it.name == 'books' }
			refAuthor.eOpposite = refBooks
			refBooks.eOpposite = refAuthor
		}
		// save the model to a file
		EMFUtilities.save(fileName, pckg)
	}
	
	static void main(args) {
		def library = new Library()
		library.create()
	}
	
}
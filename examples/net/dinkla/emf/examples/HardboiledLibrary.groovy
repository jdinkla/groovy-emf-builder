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

import org.eclipse.example.library.BookCategory
import org.eclipse.example.library.Library
import org.eclipse.example.library.LibraryFactory
import org.eclipse.example.library.LibraryPackage

import net.dinkla.emf.EMFBuilder
import net.dinkla.emf.EMFUtilities

/**
	This is an example of the EMFBuilder.
*/
class HardboiledLibrary {

	// the name of the file
	final String fileName = 'data/generated_hardboiled_library.xml'
	
	// Example data.
	final Map testData = [ 
                  		    'Dashiell Hammet': 
                  		    	[ [ title: 'The Maltese Falcon', pages: 224 ],
                  		    	  [ title: 'Red Harvest', pages: 224 ] ], 
                  			'Raymond Chandler': 
                  				[ [ title: 'The Big Sleep', pages: 234 ],
                  	              [ title: 'The High Window', pages: 272 ] ],
                  			'Ross Macdonald': 
                  				[ [ title: 'Meet Me at the Morgue', pages: 241 ],
                  				  [ title: 'The Far Side of the Dollar', pages: 245] ]
                  		  ]	

	// creates the library and saves it to a file
	void create() {
		def builder = new EMFBuilder(LibraryFactory)
		builder.debug = false
		// a map to store the authors/writers
		Map myWriters = [:]		
		def library = builder.Library( name: 'Hardboiled Library' ) {
			// create and memoize the writers
			writers {			
				for ( writer in testData ) {
					myWriters[writer.key] = Writer( name: writer.key )
				}
			}
			// create the books, use the writers in myWriters
			books {				
				for (writer in testData) {
					for (book in writer.value) {
						Book( title: book.title, 
						 	  pages: book.pages, 
							  category: BookCategory.MYSTERY_LITERAL, 
							  author: myWriters[writer.key] )
					}
				}
			}
		}
		// save it to a file
		EMFUtilities.save(fileName, library)
	}
	
	void use() {
		// get the package (this is needed, so that the library model is known to the loader)
		LibraryPackage.eINSTANCE

		// load the file
		def resource = EMFUtilities.load(fileName)
		// get the library element
		Library library = (Library) resource.contents[0]

		println "Writers with each"
		library.writers.each { writer ->
			println "\t" + writer.name
		}

		println "Books"
		for ( book in library.books ) {
			println "\t" + book.title+ "\t" + book.category+ "\t" + book.pages 
		}

		println "Books and Authors"
		for ( book in library.books ) {
			println "\t" + book.author.name + "\t" + book.title+ "\t" + book.category+ "\t" + book.pages 
		}

		println "Books with less than 240 pages"
		for ( book in library.books.grep { it.pages < 240 } ) {
			println "\t" + book.author.name + "\t" + book.title+ "\t" + book.category+ "\t" + book.pages 
		}

		println "Title of books with less than 240 pages"
		println "\t" + library.books.grep { it.pages < 240 }.title.join(", ")

		println "All books of Raymond Chandler"
		println "\t" + library.books.grep { it.author.name == 'Raymond Chandler' }.title.join(", ")
		println "\t" + library.writers.grep { it.name == 'Raymond Chandler' }.books.title.join(", ")
	}
	
	static void main(args) {
		def library = new HardboiledLibrary()
		library.create()
		library.use()
	}
	
}
package net.dinkla.emf.examples;

import net.dinkla.emf.EMFBuilder
import net.dinkla.emf.EMFUtilities
//import org.eclipse.example.library.Library
//import org.eclipse.emf.examples.extlibrary.Library
import org.eclipse.emf.examples.extlibrary.EXTLibraryFactory
import org.eclipse.emf.examples.extlibrary.BookCategory as BC

class TransformLibraryToExtLibrary {

	final String fileA = 'data/generated_hardboiled.library'
	final String fileB = 'data/generated_hardboiled.extlibrary'
	
	void transform() {
		def builder = new EMFBuilder(EXTLibraryFactory)
		builder.debug = false
		
		org.eclipse.example.library.LibraryPackage.eINSTANCE
		def resource = EMFUtilities.load(fileA)
		def libA = (org.eclipse.example.library.Library) resource.contents[0]

		Map mapWriters = [:]
		Map mapBooks = [:]
		
		def libB = builder.Library ( name : libA.name ) {
			writers {
				for ( w in libA.writers ) {
					mapWriters[w] = Writer ( name: w.name )
				}
			}
			books {
				for ( b in libA.books ) {
					mapBooks[b] = Book ( 
							title: b.title, 
							pages: b.pages, 
							category: BC.getByName( b.category.name )
							)
				}
				
			}
		}
		
		for (b in libA.books) {
			mapBooks[b].author = mapWriters[b.author]
		}
		
		EMFUtilities.save(fileB, libB)
	}
	
  	static void main(args) {
    	def t = new TransformLibraryToExtLibrary()
    	t.transform()
  	}

}
/*******************************************************************************
 * Copyright (c) 2006-2013
 * Software Technology Group, Dresden University of Technology
 * DevBoost GmbH, Berlin, Amtsgericht Charlottenburg, HRB 140026
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Software Technology Group - TU Dresden, Germany;
 *   DevBoost GmbH - Berlin, Germany
 *      - initial API and implementation
 ******************************************************************************/
package de.devboost.onthefly_javac;
 
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import de.devboost.onthefly_javac.internal.ByteJavaFileManager;
import de.devboost.onthefly_javac.internal.DynamicJavaSourceCodeObject;
 
/**
 * The {@link OnTheFlyJavaCompiler} can be used to compile Java source code in
 * memory.
 */
// this code is from http://www.accordess.com/wpblog/an-overview-of-java-compilation-api-jsr-199/
// and from http://fivedots.coe.psu.ac.th/~ad/jg/javaArt1/onTheFlyArt1.pdf
public class OnTheFlyJavaCompiler {
	
    /**
     * Compiles a set of Java classes in one batch.
     */
    public CompilationResult compile(Map<String, String> qualifiedClassNameToSourceCodeMap) {
        return compile(qualifiedClassNameToSourceCodeMap, Collections.<Processor>emptySet());
    }

    /**
     * Compiles a set of Java classes in one batch and applies the given processors.
     */
    public CompilationResult compile(Map<String, String> qualifiedClassNameToSourceCodeMap, Collection<Processor> processors) {
    	CompilationResult result = new CompilationResult();
    	Set<String> classNames = qualifiedClassNameToSourceCodeMap.keySet();
    	JavaFileObject[] javaFileObjects = new JavaFileObject[classNames.size()];
    	int index = 0;
		for (String className : classNames) {
    		String sourceCode = qualifiedClassNameToSourceCodeMap.get(className);
    		
    		// Create dynamic java source code file object
            SimpleJavaFileObject fileObject = new DynamicJavaSourceCodeObject(className, sourceCode);
            javaFileObjects[index++] = fileObject;
		}
		
        doCompile(result, processors, javaFileObjects);
        return result;
    }

    /**
     * Compiles a single Java class in memory.
     */
    public CompilationResult compile(String qualifiedClassName, String sourceCode) {
        return compile(qualifiedClassName, sourceCode, Collections.<Processor>emptySet());
    }

    /**
     * Compiles a single Java class in memory and applies the given processors.
     */
    public CompilationResult compile(String qualifiedClassName, String sourceCode, Collection<Processor> processors) {
    	CompilationResult result = new CompilationResult();
        doCompile(qualifiedClassName, sourceCode, result, processors);
        return result;
    }

	private void doCompile(String qualifiedClassName, String sourceCode,
			CompilationResult result, Collection<Processor> processors) {
		
		// Creating dynamic java source code file object
        SimpleJavaFileObject fileObject = new DynamicJavaSourceCodeObject(qualifiedClassName, sourceCode);
        JavaFileObject javaFileObjects[] = new JavaFileObject[] {fileObject} ;
 
        doCompile(result, processors, javaFileObjects);
	}

	private void doCompile(CompilationResult result,
			Collection<Processor> processors, JavaFileObject[] javaFileObjects) {
		// Instantiating the java compiler
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
 
		// Retrieving the standard file manager from compiler object, which is
		// used to provide basic building block for customizing how a compiler
		// reads and writes to files.
		//
		// The same file manager can be reopened for another compiler task.
		// Thus we reduce the overhead of scanning through file system and jar
		// files each time
    	DiagnosticCollector<JavaFileObject> diagnostics = result.getDiagnosticsCollector();
    	StandardJavaFileManager fileMan =
        		compiler.getStandardFileManager(diagnostics, null, null);
        ByteJavaFileManager<StandardJavaFileManager> jfm =
        		new ByteJavaFileManager<StandardJavaFileManager>(fileMan, result.getStore());
        
        // Prepare a list of compilation units (java source code file objects) to input to compilation task
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(javaFileObjects);
 
        // Prepare any compilation options to be used during compilation.
        // In this example, we are asking the compiler to place the output files under bin folder.
        String[] compileOptions = new String[]{} ;
        Iterable<String> compilationOptions = Arrays.asList(compileOptions);
 
        // Create a compilation task from compiler by passing in the required input objects prepared above
        CompilationTask compilerTask = compiler.getTask(null, jfm, diagnostics, compilationOptions, null, compilationUnits) ;
 
        compilerTask.setProcessors(processors);
        // Perform the compilation by calling the call method on compilerTask object.
        boolean status = compilerTask.call();
        result.setSuccess(status);

    	// Close the file manager
        try {
            jfm.close();
        } catch (IOException e) {
        	// TODO Handle exception
            e.printStackTrace();
        }
	}
}

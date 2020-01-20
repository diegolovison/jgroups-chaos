package com.github.diegolovison.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class BasicXsl {
    // This method applies the xslFilename to inFilename and writes
    // the output to outFilename.
    public static void xsl(String inFilename, String outFilename, String xslFilename) {
        try {
            xslFilename = BasicXsl.class.getResource(xslFilename).getFile();
            // Create transformer factory
            TransformerFactory factory = TransformerFactory.newInstance();

            // Use the factory to create a template containing the xsl file
            Templates template = factory.newTemplates(new StreamSource(
                new FileInputStream(xslFilename)));

            // Use the template to create a transformer
            Transformer xformer = template.newTransformer();

            // Prepare the input and output files
            Source source = new StreamSource(new FileInputStream(inFilename));
            Result result = new StreamResult(new FileOutputStream(outFilename));

            // Apply the xsl file to the source file and write the result
            // to the output file
            xformer.transform(source, result);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (TransformerConfigurationException e) {
            // An error occurred in the XSL file
            throw new IllegalStateException(e);
        } catch (TransformerException e) {
            // An error occurred while applying the XSL file
            // Get location of error in input file
            SourceLocator locator = e.getLocator();
            int col = locator.getColumnNumber();
            int line = locator.getLineNumber();
            String publicId = locator.getPublicId();
            String systemId = locator.getSystemId();
            throw new IllegalStateException(String.format("col: %d, line: %d, publicId: %s, systemId: %s", col, line, publicId, systemId));
        }
    }
}
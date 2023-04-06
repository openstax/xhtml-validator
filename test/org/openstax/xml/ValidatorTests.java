package org.openstax.xml;

import static org.junit.jupiter.api.Assertions.*;
import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.junit.jupiter.api.Test;

class ValidatorTests {

    XMLVisitor parseFile(InputStream inputFileStream) {
        XMLVisitor visitor = new XMLVisitor();

        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();

            saxParser.parse(inputFileStream, visitor);
        } catch (Exception e) {
            fail("Failure parsing input file");
        }

        return visitor;
    }

    @Test
    void validatesSimpleFile() {
        InputStream inputFileStream = getClass().getClassLoader().getResourceAsStream("pass-simple.xhtml");
        XMLVisitor visitor = parseFile(inputFileStream);
        assertFalse(visitor.brokenLinks());
        assertFalse(visitor.duplicateIds());
        assertFalse(visitor.linksToDuplicateIds());
    }

    @Test
    void validatesPassingFile() {
        InputStream inputFileStream = getClass().getClassLoader().getResourceAsStream("pass.xhtml");
        XMLVisitor visitor = parseFile(inputFileStream);
        assertFalse(visitor.brokenLinks());
        assertFalse(visitor.duplicateIds());
        assertFalse(visitor.linksToDuplicateIds());
    }

    @Test
    void detectsDuplicateIds() {
        InputStream inputFileStream = getClass().getClassLoader().getResourceAsStream("fail-duplicate.xhtml");
        XMLVisitor visitor = parseFile(inputFileStream);
        assertFalse(visitor.brokenLinks());
        assertTrue(visitor.duplicateIds());
        assertFalse(visitor.linksToDuplicateIds());
    }

    @Test
    void detectsLinksToDuplicateIds() {
        InputStream inputFileStream = getClass().getClassLoader().getResourceAsStream("fail-link-to-duplicate-id.xhtml");
        XMLVisitor visitor = parseFile(inputFileStream);
        assertFalse(visitor.brokenLinks());
        assertTrue(visitor.duplicateIds());
        assertTrue(visitor.linksToDuplicateIds());
    }

    @Test
    void detectsLinksWithoutTargets() {
        InputStream inputFileStream = getClass().getClassLoader().getResourceAsStream("fail-no-link-target.xhtml");
        XMLVisitor visitor = parseFile(inputFileStream);
        assertTrue(visitor.brokenLinks());
        assertFalse(visitor.duplicateIds());
        assertFalse(visitor.linksToDuplicateIds());
    }

}

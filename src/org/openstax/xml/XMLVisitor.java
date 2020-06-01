package org.openstax.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLVisitor extends DefaultHandler {

    Set<String> ids = new HashSet<String>();
    Map<String, Integer> duplicates = new HashMap<String, Integer>();
    Set<String> linksToCheck = new HashSet<String>();

    @Override
    public void startElement(String uri, 
    String localName, String qName, Attributes attributes) throws SAXException {

        String id = attributes.getValue("id");
        String href = attributes.getValue("href");

        if (id != null) {
            if (ids.contains(id)) {
                int count = 0;
                if (duplicates.containsKey(id)) {
                    count = duplicates.get(id);
                }
                count++;
                duplicates.put(id, count);
            } else {
                ids.add(id);
            }
        }

        if (href != null) {
            if (href.length() > 1) {
                if (href.charAt(0) == '#') {
                    String hrefId = href.substring(1);
                    linksToCheck.add(hrefId);
                }
            } else {
                System.err.println("Found a link with an empty href");
            }
        }

    }

    public void checkLinks() {
        int total = 0;
        for (String s : linksToCheck) {
            if (!ids.contains(s)) {
                total++;
                System.err.println(String.format("Link points to nowhere: href='#%s'", s));
            }
        }
        if (total != 0) {
            throw new RuntimeException(String.format("Links that point to nowhere: %d", total));
        }

    }

    public void checkDuplicateIds() {
        int total = 0;
        for (Entry<String, Integer> d : duplicates.entrySet()) {
            total += d.getValue();
            System.err.println(String.format("Found %d duplicate(s) with id='%s'", d.getValue(), d.getKey()));
        }
        if (!duplicates.isEmpty()) {
            throw new RuntimeException(String.format("Number of duplicate-id elements: %d", total));
        }
    }

    public void checkLinksToDuplicateIds() {
        boolean found = false;
        for (String s : linksToCheck) {
            if (duplicates.containsKey(s)) {
                found = true;
                System.err.println(String.format("Link to duplicate id: href='#%s' duplicates: %d", s, duplicates.get(s)));
            }
        }
        if (found) {
            throw new RuntimeException("Found at least one link that points to multiple elements that contain the same id (a duplicate id breaks a book in this case");
        }
    }

}

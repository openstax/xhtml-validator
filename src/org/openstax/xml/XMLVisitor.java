package org.openstax.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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

    public boolean brokenLinks() {
        Set<String> tempLinksToCheck = new HashSet<String>(this.linksToCheck);
        tempLinksToCheck.removeAll(this.ids);
        tempLinksToCheck.forEach(s -> System.err.println(String.format("Link points to nowhere: href='#%s'", s)));
        if (!tempLinksToCheck.isEmpty()) {
            System.err.println(String.format("Links that point to nowhere: %d", tempLinksToCheck.size()));
            return true;
        }
        return false;
    }

    public boolean duplicateIds() {
        int total = 0;
        for (Entry<String, Integer> d : duplicates.entrySet()) {
            total += d.getValue();
            System.err.println(String.format("Found %d duplicate(s) with id='%s'", d.getValue(), d.getKey()));
        }
        if (!duplicates.isEmpty()) {
            System.err.println(String.format("Number of duplicate-id elements: %d", total));
            return true;
        }
        return false;
    }

    public boolean linksToDuplicateIds() {
        boolean found = false;
        for (String s : linksToCheck) {
            if (duplicates.containsKey(s)) {
                found = true;
                System.err.println(String.format("Link to duplicate id: href='#%s' duplicates: %d", s, duplicates.get(s)));
            }
        }
        if (found) {
            System.err.println("Found at least one link that points to multiple elements that contain the same id (a duplicate id breaks a book in this case");
            return true;
        }
        return false;
    }

}

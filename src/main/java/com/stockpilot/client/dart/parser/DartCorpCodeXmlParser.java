package com.stockpilot.client.dart.parser;

import com.stockpilot.client.dart.DartApiException;
import com.stockpilot.client.dart.DartCorpEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Component
public class DartCorpCodeXmlParser {

    private static final String CORP_CODE_FILE = "CORPCODE.xml";
    private static final DateTimeFormatter MODIFY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public List<DartCorpEntry> parse(byte[] zipBytes) {
        byte[] xmlBytes = extractXml(zipBytes);
        return parseXml(xmlBytes);
    }

    private byte[] extractXml(byte[] zipBytes) {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (CORP_CODE_FILE.equalsIgnoreCase(entry.getName())) {
                    return zis.readAllBytes();
                }
            }
            throw new DartApiException("CORPCODE.xml not found in zip archive", null, false);
        } catch (IOException e) {
            throw new DartApiException("Failed to read DART corp-code zip", e);
        }
    }

    private List<DartCorpEntry> parseXml(byte[] xmlBytes) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlBytes));

            NodeList listNodes = document.getElementsByTagName("list");
            List<DartCorpEntry> entries = new ArrayList<>(listNodes.getLength());
            for (int i = 0; i < listNodes.getLength(); i++) {
                Node node = listNodes.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) continue;
                Element element = (Element) node;

                String corpCode = textOf(element, "corp_code");
                String corpName = textOf(element, "corp_name");
                if (corpCode == null || corpCode.isBlank() || corpName == null) continue;

                String rawStockCode = textOf(element, "stock_code");
                String stockCode = (rawStockCode == null || rawStockCode.isBlank())
                        ? null : rawStockCode.trim();

                LocalDate modifyDate = parseModifyDate(textOf(element, "modify_date"));
                entries.add(new DartCorpEntry(corpCode.trim(), corpName.trim(), stockCode, modifyDate));
            }
            return entries;
        } catch (Exception e) {
            throw new DartApiException("Failed to parse DART corp-code XML", e);
        }
    }

    private String textOf(Element element, String tag) {
        NodeList children = element.getElementsByTagName(tag);
        if (children.getLength() == 0) return null;
        return children.item(0).getTextContent();
    }

    private LocalDate parseModifyDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return LocalDate.parse(raw.trim(), MODIFY_DATE_FORMAT);
        } catch (Exception e) {
            log.debug("Skipping unparsable modify_date: {}", raw);
            return null;
        }
    }
}

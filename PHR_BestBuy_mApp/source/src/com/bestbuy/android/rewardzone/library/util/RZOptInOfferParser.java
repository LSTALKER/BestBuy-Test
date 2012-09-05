package com.bestbuy.android.rewardzone.library.util;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class RZOptInOfferParser {

	public static String parseOptInResponse(String response) throws Exception {
		String parasableString = prepareStringToValidForm(response);
		return parseString(parasableString);
	}

	private static String prepareStringToValidForm(String response) {
		int startPosition = response.indexOf("?>");
		String preparedString = response.substring(startPosition);
		return preparedString;
	}

	public static String parseString(String parsableString) throws Exception {
		String status = null;
		Document doc = loadXMLFromString(parsableString);
		doc.getDocumentElement().normalize();
		/*
		 * System.out.println("Root element of the doc is " +
		 * doc.getDocumentElement().getNodeName());
		 */
		Node rootNode = doc.getDocumentElement();
		NodeList nodeList = rootNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			status = n.getTextContent();
			// System.out.println("n=" + n.getTextContent());
		}
		return status;

	}
	public static Document loadXMLFromString(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}

}

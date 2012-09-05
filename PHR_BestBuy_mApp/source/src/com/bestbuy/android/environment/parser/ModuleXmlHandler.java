package com.bestbuy.android.environment.parser;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.bestbuy.android.environment.dataobject.EnvironmentBean;
import com.bestbuy.android.environment.dataobject.Module;
import com.bestbuy.android.environment.dataobject.ProxyInfoBean;
import com.bestbuy.android.util.EnvironmentConstants;

public class ModuleXmlHandler extends DefaultHandler {

	private String currentNodeName;
	private Module currentModule;
	private EnvironmentBean currentFile;

	private ArrayList<Module> records = null;
	private ProxyInfoBean proxyInfo = null;
	private String elementValue;
	private boolean flagForProxy = false;

	public ArrayList<Module> getRecords() {
		return records;
	}

	public ProxyInfoBean getProxyInfo() {
		return proxyInfo;
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		this.records = new ArrayList<Module>();

	}

	@Override
	public void startElement(final String Uri, final String localName,
			final String qName, final Attributes att) throws SAXException {
		if (localName != null)
			currentNodeName = localName;
	}

	@Override
	public void characters(final char[] ch, final int start, final int length)
			throws SAXException {
		if (this.currentNodeName == null)
			return;
		this.elementValue = new String(ch, start, length).trim();
		if (this.currentNodeName
				.equalsIgnoreCase(EnvironmentConstants.TAG_MODULE))
			this.currentModule = new Module();
		else if (this.currentNodeName
				.equalsIgnoreCase(EnvironmentConstants.TAG_MODULENAME))
			this.currentModule.setModuleName(this.elementValue);
		else if (this.currentNodeName
				.equalsIgnoreCase(EnvironmentConstants.TAG_ISPROXY))
			this.currentModule.setIsProxy(this.elementValue);
		else if (this.currentNodeName
				.equalsIgnoreCase(EnvironmentConstants.TAG_TITLE))
			this.currentModule.setModuleTitle(this.elementValue);

		if (this.currentNodeName
				.equalsIgnoreCase(EnvironmentConstants.TAG_ENVIRONMENT))
			this.currentFile = new EnvironmentBean();
		else if (this.currentNodeName
				.equalsIgnoreCase(EnvironmentConstants.TAG_NAME))
			this.currentFile.setName(this.elementValue);
		else if (this.currentNodeName
				.equalsIgnoreCase(EnvironmentConstants.TAG_TITLE_ENV))
			this.currentFile.setTitle(this.elementValue);
		else if (this.currentNodeName
				.equalsIgnoreCase(EnvironmentConstants.TAG_PATH))
			this.currentFile.setPath(this.elementValue);
		else if (this.currentNodeName
				.equalsIgnoreCase(EnvironmentConstants.TAG_DOMAIN))
			if (flagForProxy) {
				this.proxyInfo.setDomain(this.elementValue);
			} else {
				this.currentFile.setDomain(this.elementValue);
			}

		else if (this.currentNodeName
				.equalsIgnoreCase(EnvironmentConstants.TAG_HOST))
			if (flagForProxy) {
				this.proxyInfo.setHost(this.elementValue);
			} else {
				this.currentFile.setHost(this.elementValue);
			}

		else if (this.currentNodeName
				.equalsIgnoreCase(EnvironmentConstants.TAG_SECUREHOST))
			this.currentFile.setSecureHost(this.elementValue);
		else if (this.currentNodeName
				.equalsIgnoreCase(EnvironmentConstants.TAG_SECURE_APIKEY))
			this.currentFile.setSecureApiKey(this.elementValue);
		else if (this.currentNodeName
				.equalsIgnoreCase(EnvironmentConstants.TAG_APIKEY))
			this.currentFile.setApiKey(this.elementValue);
		if (this.currentNodeName
				.equalsIgnoreCase(EnvironmentConstants.TAG_PROXY)) {
			this.proxyInfo = new ProxyInfoBean();
			flagForProxy = true;
		}
		if (this.currentNodeName
				.equalsIgnoreCase(EnvironmentConstants.TAG_USERNAME)) {
			this.proxyInfo.setUsername(this.elementValue);
		}
		if (this.currentNodeName
				.equalsIgnoreCase(EnvironmentConstants.TAG_PASSWORD)) {
			this.proxyInfo.setPassword(this.elementValue);
		}

	}

	@Override
	public void endElement(final String Uri, final String localName,
			final String qName) throws SAXException {
		if (localName.equalsIgnoreCase(EnvironmentConstants.TAG_MODULE)) {
			if (this.currentModule != null)
				this.records.add(this.currentModule);
		} else if (localName
				.equalsIgnoreCase(EnvironmentConstants.TAG_ENVIRONMENT)) {
			if ((this.currentModule != null) && (this.currentFile != null))
				this.currentModule.getFiles().add(this.currentFile);
		}
		if (localName.equalsIgnoreCase(EnvironmentConstants.TAG_PROXY)) {
			flagForProxy = false;
		}
		currentNodeName = null;
	}
}
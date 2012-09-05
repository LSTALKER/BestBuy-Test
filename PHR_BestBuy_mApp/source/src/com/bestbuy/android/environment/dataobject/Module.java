package com.bestbuy.android.environment.dataobject;

import java.util.ArrayList;

public class Module {
	private ArrayList<EnvironmentBean> environmentBean;
	private String moduleName, isProxy, moduleTitle;

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getIsProxy() {
		return isProxy;
	}

	public void setIsProxy(String isProxy) {
		this.isProxy = isProxy;
	}

	public String getModuleTitle() {
		return moduleTitle;
	}

	public void setModuleTitle(String moduleTitle) {
		this.moduleTitle = moduleTitle;
	}

	public ArrayList<EnvironmentBean> getFiles() {
		return environmentBean;
	}

	public void setFiles(ArrayList<EnvironmentBean> environmentBean) {
		this.environmentBean = environmentBean;
	}

	public Module() {
		this.environmentBean = new ArrayList<EnvironmentBean>();
	}

}

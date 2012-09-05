package com.bestbuy.android.data;

import java.io.File;
import java.io.Serializable;

import android.os.Environment;

/**
 * An IQEngines search object containing a photo file and description
 * @author Recursive Awesome
 *
 */
public class PhotoSearch implements Serializable {

	private static final long serialVersionUID = 6791621845883021643L;
	
	private String id;
	private File file;
	private String description;
	private boolean analyzing;
	private String qid;
	private File tmpFile;
	
	/**
	 * Constructor to use when needs to be filled in later.
	 * @param id the id of the photosearch in case you need to find it later
	 */
	public PhotoSearch(String id) {
		description = "Analyzing...";
		this.analyzing = true;
		this.id = id;
	}
	
	/**
	 * Constructor to use for create an object with a description, file and id
	 * @param id the id of the photo searh
	 * @param filename the filename
	 * @param description the description of this photosearch
	 */
	public PhotoSearch(String id, String qid, String filename, String description) {
		this.analyzing = false;
		File sdcard = Environment.getExternalStorageDirectory(); //Check if it is available
		File file = new File(sdcard.getAbsolutePath(), filename);
		this.id = id;
		this.qid = qid;
		this.file = file;
		this.description = description;
	}
	
	public String getId() {
		return id;
	}
	
	public String getQid() {
		return qid;
	}
	
	public void setQid(String qid) {
		this.qid = qid;
	}
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isAnalyzing() {
		return analyzing;
	}
	public void setAnalyzing(boolean analyzing) {
		this.analyzing = analyzing;
	}
	
	public int compareTo(Object another) {
		PhotoSearch other = (PhotoSearch)another;
		if (this.qid.equals(other.qid)) {
			return 0;
		} else {
			return this.qid.compareTo(other.qid);
		}
	}
	
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		if (this.hashCode() == obj.hashCode()) {
			return true;
		}
		return false;
	}
	
	public int hashCode() {
		return this.qid.hashCode();
	}
	public void setTmpFile(File tmpFile) {
		this.tmpFile = tmpFile;
	}
	public File getTmpFile() {
		return tmpFile;
	}
	
}

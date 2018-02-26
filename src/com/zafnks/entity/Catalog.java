package com.zafnks.entity;

public class Catalog {

	private String novelId;

	private String catalogUrl;

	public String getNovelId() {
		return novelId;
	}

	public void setNovelId(String novelId) {
		this.novelId = novelId;
	}

	public String getCatalogUrl() {
		return catalogUrl;
	}

	public void setCatalogUrl(String catalogUrl) {
		this.catalogUrl = catalogUrl;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (null == obj) {
			return false;
		}
		if (!obj.getClass().equals(Catalog.class)) {
			return false;
		}
		Catalog nobj = (Catalog) obj;
		if (this.getNovelId().equals(nobj.getNovelId())) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.getNovelId()).hashCode();
	}

}

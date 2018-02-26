package com.zafnks.entity;

import com.zafnks.utils.ArticalType;

public class Norvel {

	private String id;

	private String name;

	private String author;

	private ArticalType type;

	private String lastChapter;

	private String updateTime;

	private String briefDes;
	
	private String img;
	
	private String catalogUrl;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		if (null == name) {
			return "";
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		if (null == author) {
			return "";
		}
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public ArticalType getType() {
		return type;
	}

	public void setType(ArticalType type) {
		this.type = type;
	}

	public String getLastChapter() {
		if (null == lastChapter) {
			return "";
		}
		return lastChapter;
	}

	public void setLastChapter(String lastChapter) {
		this.lastChapter = lastChapter;
	}

	public String getUpdateTime() {
		if (null == updateTime) {
			return "";
		}
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getBriefDes() {
		if (null == briefDes) {
			return "";
		}
		return briefDes;
	}

	public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setBriefDes(String briefDes) {
		this.briefDes = briefDes;
	}

	public String getCatalogUrl() {
		return catalogUrl;
	}

	public void setCatalogUrl(String catalogUrl) {
		this.catalogUrl = catalogUrl;
	}

	@Override
	public String toString() {
		return "【书名】" + name + " 【作者】" + author + " 【类型】" + type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (null == obj) {
			return false;
		}
		if (!obj.getClass().equals(Norvel.class)) {
			return false;
		}
		Norvel nobj = (Norvel) obj;
		if ((this.getName() + "-" + this.getAuthor()).equals(nobj.getName()
				+ "-" + nobj.getAuthor())) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.getName() + "-" + this.getAuthor()).hashCode();
	}

}

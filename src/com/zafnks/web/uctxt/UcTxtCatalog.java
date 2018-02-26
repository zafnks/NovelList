package com.zafnks.web.uctxt;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.zafnks.entity.Norvel;
import com.zafnks.utils.ArticalType;
import com.zafnks.web.AbstractCatalog;

public class UcTxtCatalog extends AbstractCatalog {
	
	private final static String TABLENAME = "novel_uctxt";

	public UcTxtCatalog(ArticalType type, String baseURL) {
		super(type, baseURL, "GBK");
	}
	
	@Override
	protected String catalogTableName() {
		return TABLENAME;
	}

	@Override
	protected String getNextPage(Document doc) {
		Elements next = doc.getElementsByClass("next");
		if(null == next || next.size()==0){
			return null;
		}
		return rootURL + next.get(0).attr("href");
	}

	@Override
	protected List<Norvel> getNorvelList(Document doc) {
		List<Norvel> result = new ArrayList<Norvel>(30);
		Elements booklist = doc.getElementsByClass("clrfix");
		if (1 <= booklist.size()) {
			Elements lis = booklist.get(0).getElementsByTag("li");
			for (int i = 0, size = lis.size(); i < size; i++) {
				Element li = lis.get(i);
				Norvel norvel = new Norvel();
				norvel.setType(type);
				norvel.setName(li.getElementsByTag("span").get(1).getElementsByTag("a").get(0).html());
				norvel.setAuthor(li.getElementsByTag("span").get(2).ownText());
				norvel.setLastChapter(li.getElementsByTag("small").get(0).getElementsByTag("a").html());
				norvel.setUpdateTime("20" + li.getElementsByTag("small").get(1).html().substring(0, 8));
				norvel.setCatalogUrl(rootURL + li.getElementsByTag("span").get(1).getElementsByTag("a").attr("href"));
				result.add(norvel);
			}
		}
		return result;
	}

    @Override
    protected void setBriefDes(String url, Document doc, Norvel norvel) {
        try {
            String des = doc.getElementsByClass("intro").get(0).ownText().trim();
            des = des.replaceAll("[;|&][a-z;&/]+[;|&]", "");
            norvel.setBriefDes(des);
            norvel.setImg("no_img");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

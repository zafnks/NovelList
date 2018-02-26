package com.zafnks.web.bbdushu;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.zafnks.entity.Norvel;
import com.zafnks.utils.ArticalType;
import com.zafnks.web.AbstractCatalog;

public class BbDuShuCatalog extends AbstractCatalog {

    private final static String TABLENAME = "novel_bbdushu";

    public BbDuShuCatalog(ArticalType type, String baseURL) {
        super(type, baseURL, "GBK");
    }

    @Override
    protected String catalogTableName() {
        return TABLENAME;
    }

    @Override
    protected String getNextPage(Document doc) {
        Elements next = doc.getElementsByClass("next");
        if (0 == next.size()) {
            return null;
        }
        return next.attr("href");
    }

    @Override
    protected List<Norvel> getNorvelList(Document doc) {
        List<Norvel> result = new ArrayList<Norvel>(30);
        Elements booklist = doc.getElementsByClass("booklist");
        if (1 == booklist.size()) {
            Element ul = booklist.get(0).getElementsByTag("ul").get(0);
            Elements lis = ul.getElementsByTag("li");
            for (int i = 1, size = lis.size(); i < size; i++) {
                Element li = lis.get(i);
                Norvel norvel = new Norvel();
                norvel.setType(type);
                norvel.setName(li.getElementsByTag("b").get(0).html());
                norvel.setAuthor(li.getElementsByClass("zz").html());
                norvel.setLastChapter(li.getElementsByTag("a").get(1).html());
                norvel.setUpdateTime("20" + li.getElementsByClass("sj").html());
                norvel.setCatalogUrl(rootURL + li.getElementsByTag("a").get(0).attr("href"));
                result.add(norvel);
            }
        }
        return result;
    }

    @Override
    protected void setBriefDes(String url, Document doc, Norvel norvel) {
        try {
            String des = doc.getElementsByClass("intro").get(0).html().trim();
            des = des.replaceAll("[;|&][a-z;&/]+[;|&]", "");
            String img = doc.getElementsByClass("jieshao").get(0).getElementsByTag("img").get(0).attr("src");
            norvel.setBriefDes(des);
            norvel.setImg(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

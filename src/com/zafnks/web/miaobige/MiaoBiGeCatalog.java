package com.zafnks.web.miaobige;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.zafnks.entity.Norvel;
import com.zafnks.utils.ArticalType;
import com.zafnks.web.AbstractCatalog;

public class MiaoBiGeCatalog extends AbstractCatalog {

    private final static String TABLENAME = "novel_miaobige";

    private Pattern pattern = Pattern.compile("http.?://[0-9a-zA-Z.?#]+/");

    public MiaoBiGeCatalog(ArticalType type, String baseURL) {
        super(type, baseURL, "GBK");
    }

    @Override
    protected String catalogTableName() {
        return TABLENAME;
    }

    @Override
    protected String getNextPage(Document doc) {
        Element page = doc.getElementsByClass("pages").get(0);
        Element next = page.getElementsByTag("strong").get(0).nextElementSibling();
        if (null == next) {
            return null;
        }
        return rootURL + next.attr("href");
    }

    @Override
    protected List<Norvel> getNorvelList(Document doc) {
        List<Norvel> result = new ArrayList<Norvel>(30);
        Elements booklist = doc.getElementsByAttributeValue("id", "sitebox");
        if (1 == booklist.size()) {
            Elements dls = booklist.get(0).getElementsByTag("dl");
            for (int i = 0, size = dls.size(); i < size; i++) {
                Element dl = dls.get(i);
                Elements dd = dl.getElementsByTag("dd");
                Norvel norvel = new Norvel();
                norvel.setType(type);
                norvel.setName(dd.get(0).getElementsByTag("a").html());
                norvel.setAuthor(dd.get(1).getElementsByTag("a").html());
                norvel.setLastChapter(dd.get(3).getElementsByTag("a").html());
                norvel.setUpdateTime(dd.get(0).getElementsByTag("span").html().substring(0, 10));
                norvel.setCatalogUrl(rootURL + dd.get(0).getElementsByTag("a").attr("href"));
                result.add(norvel);
            }
        }
        return result;
    }

    @Override
    protected void setBriefDes(String url, Document doc, Norvel norvel) {
        try {
            Elements a = doc.getElementsByClass("tabstit").get(0).getElementsByTag("a");
            String href = a.get(a.size() - 1).attr("href");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                href = matcher.group(0) + href;
            }
            String html = httpUtil.sendGet(href, "GBK");
            Document inDoc = Jsoup.parse(html);
            String des = inDoc.getElementsByAttributeValue("id", "bookintro").get(0).getElementsByTag("p").html();
            String img = inDoc.getElementsByAttributeValue("id", "bookimg").get(0).getElementsByTag("img").attr("src");
            norvel.setBriefDes(des);
            norvel.setImg(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package com.zafnks.web;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.zafnks.db.DbPoolConnection;
import com.zafnks.entity.Catalog;
import com.zafnks.entity.Norvel;
import com.zafnks.server.NorvelListServer;
import com.zafnks.utils.ArticalType;
import com.zafnks.utils.HttpGetUtils;

public abstract class AbstractCatalog implements CatalogService {

    private final static Logger log = Logger.getLogger(AbstractCatalog.class);

    private final static String QUERY_SQL = "select novel_id, catalog_url from novel_main.%s";

    private final static String ADD_SQL = "INSERT INTO novel_main.%s (novel_id, catalog_url) VALUES ('%s', '%s')";

    private Set<Catalog> catalogSet = null;

    private NorvelListServer norvelListServer = NorvelListServer.getInstance();

    protected ArticalType type;

    protected String baseURL;

    protected String rootURL = "";

    protected String encoding;

    protected HttpGetUtils httpUtil = new HttpGetUtils();

    public AbstractCatalog(ArticalType type, String baseURL, String encoding) {
        this.type = type;
        this.baseURL = baseURL;
        this.encoding = encoding;

        Pattern pattern = Pattern.compile("http.?://[0-9a-zA-Z.?#]+/");
        Matcher matcher = pattern.matcher(baseURL);
        if (matcher.find()) {
            this.rootURL = matcher.group(0);
        }
    }

    @Override
    public void catching() {
        try {
            int i = 1;
            while (null != baseURL) {
                log.info("rootURL=" + rootURL + "【" + type + "】" + " 【第" + i++ + "页】");
                String html = httpUtil.sendGet(baseURL, encoding);
                Document doc = Jsoup.parse(html);
                List<Norvel> norList = getNorvelList(doc.clone());
                for (Norvel norvel : norList) {
                    Catalog catalog = new Catalog();
                    String id = norvelListServer.getIdFromNameAuthor(norvel);
                    catalog.setNovelId(id);
                    catalog.setCatalogUrl(norvel.getCatalogUrl());
                    Norvel oldNorvel = norvelListServer.getNorvelById(id);
                    if (StringUtils.isBlank(oldNorvel.getBriefDes()) || StringUtils.isBlank(oldNorvel.getImg())) {
                        String inHtml = httpUtil.sendGet(norvel.getCatalogUrl(), encoding);
                        Document inDoc = Jsoup.parse(inHtml);
                        setBriefDes(norvel.getCatalogUrl(), inDoc.clone(), oldNorvel);
                        if(null != oldNorvel.getBriefDes() && oldNorvel.getBriefDes().length()>510){
                            oldNorvel.setBriefDes(oldNorvel.getBriefDes().trim().substring(0, 500) + "...");
                        }
                        norvelListServer.updateDesc(oldNorvel);
                        norvelListServer.setNorvel(oldNorvel);
                    }

                    if (catalogSet.contains(catalog)) {
                        if (!checkSameDetail(oldNorvel, norvel)) {
                            String newTime = norvel.getUpdateTime();
                            newTime = (newTime.length() > 10 ? newTime.substring(0, 10) : newTime).replaceAll("-", "");
                            String oldTime = oldNorvel.getUpdateTime();
                            oldTime = (oldTime.length() > 10 ? oldTime.substring(0, 10) : oldTime).replaceAll("-", "");
                            if (StringUtils.isNotBlank(newTime) && StringUtils.isNotBlank(oldTime)) {
                                if (Integer.valueOf(newTime) < Integer.valueOf(oldTime)) {
                                    norvel.setUpdateTime(oldNorvel.getUpdateTime());
                                    norvel.setLastChapter(oldNorvel.getLastChapter());
                                }
                            }
                            norvelListServer.updateNorvel(norvel);
                        }
                    } else {
                        DbPoolConnection.getInstance().updateSQL(String.format(ADD_SQL, catalogTableName(),
                                catalog.getNovelId(), catalog.getCatalogUrl()));
                        catalogSet.add(catalog);
                    }
                }
                baseURL = getNextPage(doc);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        String sql = String.format(QUERY_SQL, catalogTableName());
        catalogSet = (Set<Catalog>) DbPoolConnection.getInstance().querySQL(sql, this::getCatalogList);
    }

    private Object getCatalogList(ResultSet rs) {
        Set<Catalog> catalogSet = new HashSet<Catalog>(50000);
        ResultSetMetaData m;
        try {
            m = rs.getMetaData();
            int columns = m.getColumnCount();
            while (rs.next()) {
                Catalog cat = new Catalog();
                for (int i = 1; i <= columns; i++) {
                    if ("novel_id".equals(m.getColumnName(i))) {
                        cat.setNovelId(rs.getString(i));
                    } else if ("catalog_url".equals(m.getColumnName(i))) {
                        cat.setCatalogUrl(rs.getString(i));
                    }
                }
                catalogSet.add(cat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return catalogSet;
    }

    private boolean checkSameDetail(Norvel arg0, Norvel arg1) {
        boolean result = true;
        if (StringUtils.isNotBlank(arg0.getLastChapter()) && StringUtils.isNotBlank(arg1.getLastChapter())
                && !arg0.getLastChapter().equals(arg1.getLastChapter())) {
            result = false;
        }
        if (StringUtils.isNotBlank(arg0.getUpdateTime()) && StringUtils.isNotBlank(arg1.getUpdateTime())
                && !arg0.getUpdateTime().equals(arg1.getUpdateTime())) {
            result = false;
        }
        return result;
    }

    protected abstract String getNextPage(Document doc);

    protected abstract List<Norvel> getNorvelList(Document doc);

    protected abstract String catalogTableName();

    protected abstract void setBriefDes(String url, Document doc, Norvel norvel);

}

package com.zafnks.server;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.collections.bidimap.DualHashBidiMap;

import com.zafnks.db.DbPoolConnection;
import com.zafnks.entity.Norvel;
import com.zafnks.utils.ArticalType;

public class NorvelListServer {

    private final static String QUERY_SQL = "SELECT id, name, author, type, last_chapter, update_time, brief_des, img from novel_main.novel";

    private final static String QUERY_ID_SQL = "SELECT id from novel_main.novel where `name` = ? and `author` = ?";

    private final static String ADD_SQL = "INSERT INTO novel_main.novel (name, author, type, last_chapter, update_time, brief_des) VALUES (?,?,?,?,?,?)";

    private final static String UPDATE_LAST_SQL = "UPDATE novel_main.novel set last_chapter = ?, update_time = ? where id = ?";

    private final static String UPDATE_DESC_SQL = "UPDATE novel_main.novel set brief_des = ?, img = ? where id = ?";

    private static NorvelListServer server = new NorvelListServer();

    private DualHashBidiMap id2NameMap = new DualHashBidiMap();

    private NorvelListServer() {
    }

    public static NorvelListServer getInstance() {
        return server;
    }

    public DualHashBidiMap getMap() {
        return id2NameMap;
    }

    public String getIdFromNameAuthor(Norvel norvel) {
        String id = (String) id2NameMap.getKey(norvel);
        if (null == id) {
            DbPoolConnection.getInstance().updateSQL(ADD_SQL,
                    new Object[] { norvel.getName(), norvel.getAuthor(), norvel.getType().toString(),
                            norvel.getLastChapter(), norvel.getUpdateTime(), norvel.getBriefDes() });
            id = (String) DbPoolConnection.getInstance().querySQL(QUERY_ID_SQL, server::getNewId,
                    new Object[] { norvel.getName(), norvel.getAuthor() });
            LogServer.getInstance().addCount(1);
            if (null != id) {
                norvel.setId(id);
                id2NameMap.put(norvel.getId(), norvel);
            }
        } else {
            norvel.setId(id);
        }
        return (String) id;
    }

    public Norvel getNorvelById(String id) {
        return (Norvel) id2NameMap.get(id);
    }
    
    public void setNorvel(Norvel norvel) {
        id2NameMap.put(norvel.getId(), norvel);
    }

    public void readDataFromDB() {
        id2NameMap = (DualHashBidiMap) DbPoolConnection.getInstance().querySQL(QUERY_SQL, server::transData2Map);
    }

    public void updateNorvel(Norvel norvel) {
        DbPoolConnection.getInstance().updateSQL(UPDATE_LAST_SQL,
                new Object[] { norvel.getLastChapter(), norvel.getUpdateTime(), norvel.getId() });
    }

    public void updateDesc(Norvel norvel) {
        DbPoolConnection.getInstance().updateSQL(UPDATE_DESC_SQL,
                new Object[] { norvel.getBriefDes(), norvel.getImg(), norvel.getId() });
    }

    private Object transData2Map(ResultSet rs) {
        DualHashBidiMap id2NameMap = new DualHashBidiMap();
        ResultSetMetaData m;
        try {
            m = rs.getMetaData();
            int columns = m.getColumnCount();
            while (rs.next()) {
                Norvel norvel = new Norvel();
                for (int i = 1; i <= columns; i++) {
                    if ("id".equals(m.getColumnName(i))) {
                        norvel.setId(rs.getString(i));
                    } else if ("name".equals(m.getColumnName(i))) {
                        norvel.setName(rs.getString(i));
                    } else if ("author".equals(m.getColumnName(i))) {
                        norvel.setAuthor(rs.getString(i));
                    } else if ("type".equals(m.getColumnName(i))) {
                        norvel.setType(ArticalType.getType(rs.getString(i)));
                    } else if ("last_chapter".equals(m.getColumnName(i))) {
                        norvel.setLastChapter(rs.getString(i));
                    } else if ("update_time".equals(m.getColumnName(i))) {
                        norvel.setUpdateTime(rs.getString(i));
                    } else if ("brief_des".equals(m.getColumnName(i))) {
                        norvel.setBriefDes(rs.getString(i));
                    } else if ("img".equals(m.getColumnName(i))) {
                        norvel.setImg(rs.getString(i));
                    }
                }
                id2NameMap.put(norvel.getId(), norvel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id2NameMap;
    }

    private Object getNewId(ResultSet rs) {
        Object id = null;
        try {
            while (rs.next()) {
                id = rs.getString(1);
                break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
}

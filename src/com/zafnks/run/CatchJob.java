package com.zafnks.run;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.zafnks.server.LogServer;
import com.zafnks.server.NorvelListServer;
import com.zafnks.utils.ArticalType;
import com.zafnks.utils.PathContants;
import com.zafnks.web.CatalogService;

public class CatchJob implements Job {

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        NorvelListServer.getInstance().readDataFromDB();
        List<CatalogService> servers = readXML();
        if(null != servers){
            LogServer.getInstance().startCount("更新书籍列表");
            for(CatalogService server: servers){
                server.init();
                server.catching();
            }
            LogServer.getInstance().finishEvent();
        }
    }

    public List<CatalogService> readXML() {
        List<CatalogService> list = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(PathContants.getBinPath() + "server.xml");
            NodeList serviceList = document.getElementsByTagName("service");
            list = new ArrayList<CatalogService>(serviceList.getLength());
            // 遍历每一个service节点
            for (int i = 0; i < serviceList.getLength(); i++) {
                Node node = serviceList.item(i);
                String cls = node.getAttributes().getNamedItem("class").getNodeValue();
                ArticalType type = ArticalType.getType(node.getAttributes().getNamedItem("type").getNodeValue());
                String baseUrl = node.getAttributes().getNamedItem("baseUrl").getNodeValue();
                if (StringUtils.isBlank(cls) || null == type || StringUtils.isBlank(baseUrl)) {
                    continue;
                }
                try {
                    Constructor<?> Ct = Class.forName(cls)
                            .getConstructor(new Class[] { ArticalType.class, String.class });
                    list.add((CatalogService) Ct.newInstance(new Object[] { type, baseUrl }));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}

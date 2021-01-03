package com.xyz.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlUtil {

    /**
     * @param xmlPath
     * @return org.dom4j.Document
     * @desc 读取xml文件
     * @author cxs
     * @date 2021-01-01 07:07:36
     **/
    public static Document readDocument(String xmlPath) {
        Path path = Paths.get(xmlPath);
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(path.toFile());
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * 将Element对象转为Map（String→Document→Element→Map）
     *
     * @return
     */
    @SuppressWarnings({"unchecked", "unchecked", "unchecked", "rawtypes"})
    public static Map Dom2Map(Element e) {
        Map map = new HashMap();
        List list = e.elements();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Element iter = (Element) list.get(i);
                List mapList = new ArrayList();
                if (iter.elements().size() > 0) {
                    Map m = Dom2Map(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = (List) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else {
                        map.put(iter.getName(), m);
                    }
                } else {
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(iter.getText());
                        }
                        if (obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = (List) obj;
                            mapList.add(iter.getText());
                        }
                        map.put(iter.getName(), mapList);
                    } else {
                        map.put(iter.getName(), iter.getText());//公共map resultCode=0
                    }
                }
            }
        } else {
            map.put(e.getName(), e.getText());
        }
        return map;
    }//调用上面的xml转map方法：
    //xml转map            SAXReader saxReader = new SAXReader();            Document document = saxReader.read(new ByteArrayInputStream(respXml.getBytes()));            Element incomingForm = document.getRootElement();            Map map =  Dom2Map(incomingForm);            System.out.println("map >>> "+ map);
    /*             * {resultMsg=成功, orderOffer={offer={offerSpecName=980010992)商务助手（OCS）30元, endDt=3000-1-1 0:00:00, startDt=2012-11-27 16:48:12, offerSpecId=980010992, params={param=[{offerParamId=101000048281, itemSpecName=计费区分, itemSpecId=5030, value=CDMA预付费}, {offerParamId=101000048282, itemSpecName=经分区分, itemSpecId=5031, value=天翼商话}]}, offerId=105000808918}}, resultCode=0, saleOffer={categoryNode={offer=[{summary=待定..., id=980001995, name=(980001995)商务助手（OCS）201209版-30元}, {summary=待定..., id=980001996, name=(980001996)商务助手（OCS）201209版-50元}, {summary=待定..., id=980001997, name=(980001997)商务助手（OCS）201209版-80元}, {summary=待定..., id=980010993, name=(980010993)商务助手（OCS）50元}]}}}             */

    /**
     * 将多结点多层级的Map转为多包体的list集合
     *
     * @param respStr
     * @param listNode
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, List<String>> map2ListbyHandler(Map respStr, String listNode) {
        List<String> resultList = new ArrayList<String>();

        Map<String, List<String>> resultMap = new HashMap<>();

        Map map = respStr;
        String[] strArray = listNode.split(">");
        String key = null;
        Object value = null;
        for (int i = 0; i < strArray.length; i++) {
            if (map.get(strArray[i]) instanceof List) {
                if (i < strArray.length && null != map.get(strArray[i]) && "" != map.get(strArray[i])) {
                    Map<String, Object> mapList = map;
                    if (((List<Map<String, Object>>) mapList.get(strArray[i])).size() > 0) {

//                            Map mapj = new HashMap();

//                            List<Map<String,Object>> list = ((List<Map<String,Object>>)mapList.get(strArray[i]));

                        resultList = ((List<String>) mapList.get(strArray[i]));
                        if (resultList.size() > 0) {
                            resultMap.put(strArray[i], resultList);
                        }
                        return resultMap;
                    }
                }
                break;
            } else {
                if (i < strArray.length - 1 && null != map.get(strArray[i]) && "" != map.get(strArray[i])) {
                    map = (Map) map.get(strArray[i]);
                } else {
                    //说明没有此节点只有一个值，直接从map里把值取出放resultList中
                    resultList.add(map.get(strArray[i]).toString());
                    resultMap.put(strArray[i], resultList);//listNode
                }
            }
        }
        return resultMap;
    }

    /**
     * @param configFile
     * @return java.lang.String
     * @desc 根据配置文件读取项目编译输出路径
     * @author cxs
     * @date 2021-01-01 07:21:09
     **/
    public static String findOutputPath(String configFile) {
        Path path = Paths.get(configFile);
        if (!Files.exists(path) || Files.isDirectory(path)) {
            return null;
        }
        Document document = readDocument(path.toString());
        List elements = document.getRootElement().elements();
        //idea配置文件
        if (path.toString().endsWith(".xml")) {
            for (int i = 0; i < elements.size(); i++) {
                Element iter = (Element) elements.get(i);
                Element element = iter.element("output-path");
                String text = element.getText();
                return text;
            }
        } else {
            //eclipse配置文件
            for (int i = 0; i < elements.size(); i++) {
                Element iter = (Element) elements.get(i);
                String kind = iter.attributeValue("kind");
                if ("output".equals(kind)) {
                    String _path = iter.attributeValue("path");
                    return _path;
                }
            }
        }
        return null;
    }

    public static void main(String[] srgs) throws DocumentException, IOException {
        String pathxml = "D:\\Work\\Idea\\AM\\.idea\\artifacts\\AM.xml";
        Document document = readDocument(pathxml);
        List elements = document.getRootElement().elements();

    	/*String pathxml = "D:\\Work\\Idea\\AM\\.classpath";
        Document document = readDocument(pathxml);
        List elements = document.getRootElement().elements();
        for (int i = 0; i < elements.size(); i++) {
            Element iter = (Element) elements.get(i);
            String kind = iter.attributeValue("kind");
            if("output".equals(kind)){
                String path = iter.attributeValue("path");
                System.out.println(path);
                break;
            }
        }*/


    }


}

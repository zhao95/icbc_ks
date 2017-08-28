//package com.rh.core.plug.search;
//
//import java.io.File;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.junit.Test;
//
//import com.hg.xdoc.XDoc;
//import com.hg.xdoc.XDocIO;
//import com.rh.core.TestEnv;
//import com.rh.core.base.Bean;
//import com.rh.core.plug.search.util.SearchContext;
//
//public class TestDoc extends TestEnv {
//    /** log */
//    private static Log log = LogFactory.getLog(TestIndex.class);
//    @Test
//    public void textXdoc() {
//        try {
//            File file = new File("d://test//template");
//            if (file.exists()) {
//                Collection<File> files = FileUtils.listFiles(file, new String[] { "doc","pdf" }, false);
//                Map<String, String> map = new HashMap<String, String>();
//                map.put(XDocIO.READ_PARAM_FIRST_PAGE, "true");
//                for (File f : files) {
//                    try {
//                        XDoc doc = XDocIO.read(f);
//                        File file1 = new File(file, f.getName() + ".jpg");
//                        XDocIO.write(doc, file1);
//                    } catch(Exception e) {
//                        log.debug(f.getName() + "   " + e.getMessage());
//                    }
//                }
//            }
//
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//    }
//    
////    @Test
////    public void textYZgif() {
////        try {
////            String path = "d://test//doc";
////            File file = new File(path);
////            if (file.exists()) {
////                Collection<File> files = FileUtils.listFiles(file, new String[] {"pdf"}, false);
////                ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
////                for (File f : files) {
////                    try {
////                        IPICConvertor picc;
////                        if (f.getName().toLowerCase().endsWith(".pdf")) {
////                            picc = convertobj.getConvertor().convertPDFtoPic(f.getAbsolutePath());
////                        } else {
////                            picc = convertobj.getConvertor().convertMStoPic(f.getAbsolutePath());
////                        }
////                        int resultcode = picc.resultCode();
////                        int page = picc.getPageCount();
////                        System.out.println("page==" + page);
////                        System.out.println("resultcode==" + resultcode);
////                        if(resultcode == 0) {
////                            picc.convertToGIF(0, -1, 1f, path, f.getName());
////                        } 
////                        picc.close();
////                    } catch(Exception e) {
////                        log.debug(f.getName() + "   " + e.getMessage());
////                    }
////                }
////                ConvertorPool.getInstance().returnConvertor(convertobj);
////            }
////
////        } catch(Exception e) {
////            e.printStackTrace();
////        }
////    }
////    
////    @Test
////    public void textYZHtml() {
////        try {
////            String path = "d://test//doc";
////            File file = new File(path);
////            if (file.exists()) {
////                Collection<File> files = FileUtils.listFiles(file, new String[] { "doc", "ppt","pdf"}, false);
////                ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
////                for (File f : files) {
////                    try {
////                        String fileName = f.getName();
////                        String outName = f.getParent() + "//" + fileName + ".html";
////                        convertobj.getConvertor().convertMStoHTML(f.getAbsolutePath(), outName);
////                    } catch(Exception e) {
////                        log.debug(f.getName() + "   " + e.getMessage());
////                    }
////                }
////                ConvertorPool.getInstance().returnConvertor(convertobj);
////            }
////            
////        } catch(Exception e) {
////            e.printStackTrace();
////        }
////    }
////    
////    @Test
////    public void textYZPdf() {
////        try {
////            String path = "d://test//doc";
////            File file = new File(path);
////            if (file.exists()) {
////                Collection<File> files = FileUtils.listFiles(file, new String[] { "doc","ppt" }, false);
////                ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
////                for (File f : files) {
////                    try {
////                        String outName = f.getParent() + "//" + f.getName() + ".pdf";
////                        convertobj.getConvertor().convertMStoPDF(f.getAbsolutePath(), outName);
////                    } catch(Exception e) {
////                        log.debug(f.getName() + "   " + e.getMessage());
////                    }
////                }
////                ConvertorPool.getInstance().returnConvertor(convertobj);
////            }
////            
////        } catch(Exception e) {
////            e.printStackTrace();
////        }
////    }
//    @Test
//    public void testImgUrl() {
//        Bean data = new Bean();
//        data.set("NEWS_BODY", "adfasdfsf<div class=mbArticleShareBtn><span>转播到腾讯微博</span></div>"
//                + "<img alt=\"武汉银行爆炸案一审宣判 王海剑被判死刑\""
//                + " src=\"http://img1.gtimg.com/news/pics/hv1/117/238/1038/67556757.jpg\" ></div>sdfsfasdf");
//        SearchContext sc = new SearchContext("NS_NEWS", data);
//        log.info(sc.getHtmlImgUrl("NEWS_BODY", 1, false));
//    }
//}

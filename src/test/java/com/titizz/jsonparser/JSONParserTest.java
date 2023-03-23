package com.titizz.jsonparser;

import com.sargeraswang.util.ExcelUtil.ExcelUtil;
import com.titizz.jsonparser.model.JsonArray;
import com.titizz.jsonparser.model.JsonObject;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by code4wt on 17/9/1.
 */
public class JSONParserTest {

    @Test
    public void fromJSON() throws Exception {
//        String path = this.getClass().getResource("/music.json").getFile();
        String path = "E:\\dev\\JSONParser-master\\src\\test\\resources\\data.json";
        String json = new String(Files.readAllBytes(Paths.get(path)), "utf-8");
//       String json = getJSON();

        JSONParser jsonParser = new JSONParser();
        JsonObject jsonObject = (JsonObject) jsonParser.fromJSON(json);
//        System.out.println(jsonObject);

        JsonArray values = jsonObject.getJsonObject("tableVO").getJsonArray("values");

        Map<String,String> head=new LinkedHashMap<>();

        JsonArray header = values.getJsonArray(0);
        for(int i=0;i<header.list.size();i++){
            Map<String, Object> map = ((JsonObject) header.list.get(i)).map;
            String value=(String) map.get("value");
            if (value==null){
                value="null"+i;
            }
            head.put(value,value);
        }

        List<Map<String,Object>> list = new ArrayList<>();
        for(int i=1;i<values.list.size();i++){

            JsonArray headerTemp = values.getJsonArray(i);
            Map<String,Object> headTemp=new LinkedHashMap<>();
            Iterator<Map.Entry<String, String>> iterator = head.entrySet().iterator();
            for(int j=0;j<headerTemp.list.size();j++){
                Map.Entry<String, String> next = iterator.next();
                if(headerTemp.list.get(j)==null){
                    headTemp.put(next.getKey(),"");
                } else{
                    Map<String, Object> mapTemp = ((JsonObject) headerTemp.list.get(j)).map;
                    Object value=mapTemp.get("value");
                    if (value==null){
                        value="";
                    }
                    headTemp.put(next.getKey(),value);
                }

                iterator.hasNext();
            }
            list.add(headTemp);
        }


        File f= new File("test.xls");
        OutputStream out = new FileOutputStream(f);
        ExcelUtil.exportExcel(head,list, out );
        out.close();

    }

    @Test
    public void fromJSON1() throws Exception {
        String json = "{\"a\": 1, \"b\": \"b\", \"c\": {\"a\": 1, \"b\": null, \"d\": [0.1, \"a\", 1,2, 123, 1.23e+10, true, false, null]}}";
        JSONParser jsonParser = new JSONParser();
        JsonObject jsonObject = (JsonObject) jsonParser.fromJSON(json);
        System.out.println(jsonObject);

        assertEquals(1, jsonObject.get("a"));
        assertEquals("b", jsonObject.get("b"));

        JsonObject c = jsonObject.getJsonObject("c");
        assertEquals(null, c.get("b"));

        JsonArray d = c.getJsonArray("d");
        assertEquals(0.1, d.get(0));
        assertEquals("a", d.get(1));
        assertEquals(123, d.get(4));
        assertEquals(1.23e+10, d.get(5));
        assertTrue((Boolean) d.get(6));
        assertFalse((Boolean) d.get(7));
        assertEquals(null, d.get(8));
    }

    @Test
    public void fromJSON2() throws Exception {
        String json = "[[1,2,3,\"\u4e2d\"]]";
        JSONParser jsonParser = new JSONParser();
        JsonArray jsonArray = (JsonArray) jsonParser.fromJSON(json);
        System.out.println(jsonArray);
    }

    @Test
    public void beautifyJSON() throws Exception {
        String json = "{\"name\": \"狄仁杰\", \"type\": \"射手\", \"ability\":[\"六令追凶\",\"逃脱\",\"王朝密令\"],\"history\":{\"DOB\":630,\"DOD\":700,\"position\":\"宰相\",\"dynasty\":\"唐朝\"}}";
        System.out.println("原 JSON 字符串：");
        System.out.println(json);
        System.out.println("\n");
        System.out.println("美化后的 JSON 字符串：");
        JSONParser jsonParser = new JSONParser();
        JsonObject drj = (JsonObject) jsonParser.fromJSON(json);
        System.out.println(drj);
    }

    private String getJSON() throws IOException {
        String url = "http://music.163.com/weapi/v3/playlist/detail";
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("params", "kJMudgZJvK8p8STuuxRpkUvO71Enw4C9y91PBkVTv2SMVnWG30eDKK1iAPcXnEah"));
        params.add(new BasicNameValuePair("encSecKey", "d09b0b95b7d5b4e68aa7a16d6177d3f00a78bfa013ba59f309d41f18a2b4ea066cdea7863866b6283f403ddcd3bfb51f73f8ad3c6818269ceabff934a645196faf7a9aae0edde6e232b279fd495140e6252503291cf819eabbd9f3373648775201a70f179b7981d627257d3bba5a5e1b99d0732ce3e898db3614d82bcbe1a6a8"));
        Response response = Request.Post(url)
                .bodyForm(params)
                .execute();

        return response.returnContent().asString(Charset.forName("utf-8"));
    }


    @Test
    public void genExcel() throws IOException {
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map =new LinkedHashMap<>();
        map.put("name", "");
        map.put("age", "");
        map.put("birthday","");
        map.put("sex","");
        Map<String,Object> map2 =new LinkedHashMap<String, Object>();
        map2.put("name", "测试是否是中文长度不能自动宽度.测试是否是中文长度不能自动宽度.");
        map2.put("age", null);
        map2.put("sex", null);
        map.put("birthday",null);
        Map<String,Object> map3 =new LinkedHashMap<String, Object>();
        map3.put("name", "张三");
        map3.put("age", 12);
        map3.put("sex", "男");
        map3.put("birthday",new Date());
        list.add(map);
        list.add(map2);
        list.add(map3);
        Map<String,String> map1 = new LinkedHashMap<>();
        map1.put("name","姓名");
        map1.put("age","年龄");
        map1.put("birthday","出生日期");
        map1.put("sex","性别");
        File f= new File("test.xls");
        OutputStream out = new FileOutputStream(f);
        ExcelUtil.exportExcel(map1,list, out );
        out.close();
    }

}
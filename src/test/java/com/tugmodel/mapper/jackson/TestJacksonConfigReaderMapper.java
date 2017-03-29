/*
 * Copyright (c) 2017- Cristian Donoiu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tugmodel.mapper.jackson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.tugmodel.client.mapper.Mapper;
import com.tugmodel.client.model.Model;


/**
 * NOTE: This suite of tests should become a test suite for any new Mapper implementation.
 */
public class TestJacksonConfigReaderMapper extends TestJacksonMapper {
    @Before    
    public void init() {
        mapper = JacksonMappers.getConfigReaderMapper();
    }
    
    @Test
    public void prettyPrintTest() {
        Model m = new Model().set("x", 1).set("y", "1").set("z", null);
        
        String s = (String)mapper.serialize(m);
        String indent = "  ", lf = System.getProperty("line.separator");
        String expected = "{" + lf + 
                //indent + "\"" + JacksonMapper.KEY_CLASS + "\" : \"" + Model.class.getCanonicalName() + "\"," + lf +
                indent + "\"id\" : \"" + m.getId() + "\"," + lf +
                indent + "\"x\" : 1," + lf +
                indent + "\"y\" : \"1\"," + lf +
                indent + "\"z\" : null" + lf +
                "}";
        
        StringBuilder sb = new StringBuilder();
        if (expected.length() != s.length()) {
            for (int i = 0; i < s.length(); i++) {
                if (expected.length() > i) {
                    sb.append(s.charAt(i));
                    if (s.charAt(i) != expected.charAt(i)) {                        
                        System.out.println("After '" + sb.toString() + "' found '" + (int)s.charAt(i) + 
                                "' expected '" + (int)expected.charAt(i) + "'");
                        break;
                    }
                }
            }
            throw new RuntimeException("Expected '" + expected + "' but got '" + s + "'");
        }
        
        assertTrue(s.equals(expected));
    }

    @Test
    public void test2wayUsingPrettyMapper() {
        super.test2wayUsingPrettyMapper();
    }
    

    public static class XModel extends Model<XModel>{        
    }
    public static class YModel extends Model<YModel>{        
    }
    @Test    
    public void twoWayWithChild() {
        // The config mapper does not retain type in embedded attributes that did not have dedicated getters/setters.
        //super.twoWayWithChild();
            
        Model m = new Model().set("x", 1).set("y", "1").set("z", null);
        Model child = new Model().set("c", 3);
        m.set("child", child);
        ArrayList l = new ArrayList();
        l.add("aaaa");
        l.add(child);
        m.set("list", l);

        String s = (String) mapper.serialize(m);
        Model m2 = mapper.deserialize(s);  // The other way around. m2.get("child")

        // assertTrue(m2.get("child").getClass().equals(LinkedHashMap.class)); // Type info must not be lost.
        assertTrue(m2.get("child").getClass().equals(LinkedHashMap.class));
    }
   
    
    @Test
    public void testGettersWithConfigMapper() {
        Mapper<ZModel> zMapper = mapper;
        ZModel z = new ZModel();
        z.setZ(1);
        String s = (String) zMapper.serialize(z);
        // Config reader does not uses type information for model ZModel.
        Model m2 = (Model) zMapper.deserialize(s);
        // assertEquals(m2.getZ() - 1, z.getZ()); // -1 because the "setZ" method adds 1.
        assertEquals(m2.asInt("z"), z.getZ()); // -1 because the "setZ" method adds 1.
    }

    @Test
    public void testConversion() {
        Map map = new HashMap();
        map.put("id", "1");
        Model model = ((Mapper<Model>) mapper).convert(map, Model.class);
        assertEquals(model.getId(), "1");

        // Map map2 = new HashMap();
        // map2.put("id", "1");
        // map2.put("@c", "com.tugmodel.client.model.config.Config");
        // Config config = ((Mapper<Model>) mapper).convert(map2, Config.class);
        // assertTrue(model.getClass() == Config.class);

    }
}
















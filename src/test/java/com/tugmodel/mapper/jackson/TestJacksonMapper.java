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
import java.util.List;

import com.tugmodel.client.mapper.Mapper;
import com.tugmodel.client.model.Model;

/**
 * NOTE: This suite of tests should become a test suite for any new Mapper implementation.
 */
public class TestJacksonMapper {
    protected Mapper mapper;
    
    public void pretyPrintTest() {
        Model m = new Model().set("x", 1).set("y", "1").set("z", null);
                
        String s = (String)mapper.serialize(m);
        String indent = "  ", lf = System.getProperty("line.separator");
        String expected = "{" + lf + 
                indent + "\"" + JacksonMapper.KEY_CLASS + "\" : \"" + Model.class.getCanonicalName() + "\"," + lf +
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

    public void test2wayUsingPrettyMapper() {        
        Model m = new Model().set("x", 1).set("y", "1").set("z", null);
        
       
        String s = (String)mapper.serialize(m);
        Model m2 = mapper.deserialize(s);  // The other way around.
        String s2 = (String)mapper.serialize(m2);
        
        assertTrue(s.equals(s2));
    }
    
    
   
    public static class XModel extends Model<XModel>{        
    }
    public static class YModel extends Model<YModel>{        
    }
    public void twoWayWithChild() {
        Model m = new Model().set("x", 1).set("y", "1").set("z", null);
        Model child = new Model().set("c", 3);
        m.set("child", child);
        ArrayList l = new ArrayList();
        l.add("aaaa");
        l.add(child);
        m.set("list", l);
       
        String s = (String)mapper.serialize(m);
        Model m2 = mapper.deserialize(s);  // The other way around.  m2.get("child")
        
        assertTrue(m2.get("child").getClass().equals(Model.class));  // Type info must not be lost.
        
        String s2 = (String)mapper.serialize(m2);
        assertTrue(s.equals(s2));   // Test that serializing again the deserialized model makes the same value.
        
        
        // Stores collections and maps.
        XModel mx = new XModel();
        mx.set("a", 1);
        HashMap map = new HashMap();
        map.put("k1", "v1");
        map.put("km", new Model().set("base model", 1));
        map.put("kmx", new XModel().set("mx model", 1));
        
        List list = new ArrayList();
        list.add(mx);
        list.add(new YModel().set("ymodel", true));
        m = new Model().set("mx", mx).set("mxSingleton", list).set("mxMap", map);
        s = (String)mapper.serialize(m);
        m2 = mapper.deserialize(s);
        s2 = (String)mapper.serialize(m2);
        assertTrue(s.equals(s2));
        
    }
    
    public static class ZModel extends Model<ZModel>{
        public int getZ() {
            return asInteger("z");
        }
        public ZModel setZ(int value) {
            return set("z", value + 1);
        }
    }
    
    // Test that getters and setters are used when serializing.    
    public void testGettersWithConfigMapper() {
        Mapper<ZModel> zMapper = mapper; 
        ZModel z = new ZModel();
        z.setZ(1);
        String s = (String)zMapper.serialize(z);
        ZModel m2 = zMapper.deserialize(s);
        assertEquals(m2.getZ() - 1, z.getZ()); // -1 because the "setZ" method adds 1.
        
    }
}
















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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.junit.Before;
import org.junit.Test;

import com.tugmodel.client.model.Model;


/**
 * NOTE: This suite of tests should become a test suite for any new Mapper implementation.
 */
public class TestJacksonPrettyPrintMapper extends TestJacksonMapper {
    @Before    
    public void init() {
        mapper = JacksonMappers.getPrettyPrintMapper();
    }
    
    @Test
    public void prettyPrintTest() {
        super.prettyPrintTest();
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

        assertTrue(m2.get("child").getClass().equals(LinkedHashMap.class));  // Type info must not be lost.
    }
   
    
    @Test
    public void testGettersWithConfigMapper() {
        super.testGettersWithConfigMapper();        
    }
}
















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

import org.junit.Before;
import org.junit.Test;

/**
 * NOTE: This suite of tests should become a test suite for any new Mapper implementation.
 */
public class TestJacksonTypedMapper extends TestJacksonMapper {
    @Before    
    public void init() {
        mapper = JacksonMappers.getTypedMapper();
    }
    
    @Test
    public void pretyPrintTest() {
        super.pretyPrintTest();
    }

    @Test
    public void test2wayUsingPrettyMapper() {        
        super.test2wayUsingPrettyMapper();
    }
    
    
    @Test
    public void twoWayWithChild() {
        super.twoWayWithChild();
    }
   
    
    @Test
    public void testGettersWithConfigMapper() {
        super.testGettersWithConfigMapper();        
    }
}
















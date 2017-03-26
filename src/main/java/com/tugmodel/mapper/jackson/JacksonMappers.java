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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tugmodel.client.mapper.Mapper;
import com.tugmodel.client.model.Model;
import com.tugmodel.client.model.config.Config;
import com.tugmodel.client.model.meta.Attribute;
import com.tugmodel.client.model.meta.Meta;

/**
 * 
 *
 */
public class JacksonMappers {
	
	private static Mapper configReaderMapper = null;
	// Does basic json config reading, no metadata involved.
	public static Mapper getConfigReaderMapper() {		
		if (configReaderMapper == null) {
			configReaderMapper = new JacksonMapper<Model>() {
				public ObjectMapper initMapper() {
					ObjectMapper mapper = new ObjectMapper();
	
					// TODO: All these should come from config-defaults.json.
			        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Don't include nulls.
			        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
			        mapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
			        mapper.configure(SerializationFeature.INDENT_OUTPUT, true); 
			        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
			        //mapper.enableDefaultTyping();   // Will add type information.
			        mapper.registerModule(new SimpleModule("ConfigMixins") {
			        	@Override
			        	public void setupModule(SetupContext context) {
			        		context.setMixInAnnotations(Model.class, MixinsGenerator.BootstrapMixin.class);			        		
			        	}
			        	
			        });
			        return mapper;
				}
			};
		}
		return configReaderMapper;
	}
	

	public static Mapper getMapper(Object config) {
		return new JacksonMapper<Model>() {
			public ObjectMapper initMapper() {
				ObjectMapper mapper = new ObjectMapper();

				// TODO: All these should come from config-defaults.json.
				mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Don't include nulls.
				mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
				mapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
				mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
				// mapper.enableDefaultTyping(); // Will add type information.
				mapper.registerModule(new MixinsGenerator("Mixins"));
				return mapper;
			}
		};

	}
	
	
}


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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tugmodel.client.mapper.Mapper;
import com.tugmodel.client.model.Model;

/**
 * 
 *
 */
public class JacksonMappers {

    // Used for pretty printing.
    public static JacksonMapper getPrettyPrintMapper() {
        return new JacksonMapper<Model>() {
            public ObjectMapper initMapper() {
                ObjectMapper mapper = new ObjectMapper();
                // TODO: All these should come from config-defaults.json.
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Don't include nulls.
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
                mapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
                mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
                mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
                mapper.registerModule(new SimpleModule("ConfigMixins") {
                    @Override
                    public void setupModule(SetupContext context) {
                        context.setMixInAnnotations(Model.class, MixinsGenerator.WithClassAnnotationsModelMixin.class);
                    }

                });
                return mapper;
            }
        };

    }


    // Does basic json config reading, no metadata involved.
    public static Mapper getConfigReaderMapper() {      
        return new JacksonMapper<Model>() {
            public ObjectMapper initMapper() {
                ObjectMapper mapper = new ObjectMapper();

                // TODO: All these should come from config-defaults.json.
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Don't include nulls.
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
                mapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
                mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
                mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
                // mapper.configure(JsonParser.Feature.ALLOW_MISSING_VALUES, true); // Allows trailing comma
                // mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL); // Will add type information.
                mapper.registerModule(new SimpleModule("ConfigMixins") {
                    @Override
                    public void setupModule(SetupContext context) {
                        // Should I allow custom types inside Config?. If yes then use "@c" aware mixin and also
                        // add "@c" to ALL elements within json file which makes the JSON heavy.
                        // context.setMixInAnnotations(Model.class,
                        // MixinsGenerator.WithClassAnnotationsModelMixin.class);
                        // A BETTER solution would be to not use annotation "@c" at Model class level and use
                        // instead the meta and put "@c" only for the types that can have childs.
                        // Also this implies a 2 step solution. First read the json without mixins to get meta and
                        // then use another one based on meta.
                        context.setMixInAnnotations(Model.class, MixinsGenerator.NoTypeInfoModelMixin.class);
                    }
                });

                // mapper.registerModule(new SimpleModule("ConfigMixins") {
                // @Override
                // public void setupModule(SetupContext context) {
                // // The alternative to inline "@c" would be default typing at mapper level but that is ugly.
                // // To prevent security holes the user entered content needs to forbid "@c".
                // // Object mixin has same effect as "enableDefaultTyping(DefaultTyping.NON_FINAL);"
                // // with the exception that everything else except collections and maps have the $c added
                // // inline as a property and not as a element in a new array like for .
                // context.setMixInAnnotations(Object.class, MixinsGenerator.BootstrapMixinObject.class);
                // context.setMixInAnnotations(Model.class,
                // MixinsGenerator.BootstrapModelMixinNoClassAnnotations.class);
                // }
                // });
                return mapper;
            }
        };

    }
    

    // Does basic json config reading, no metadata involved.
    public static Mapper getTypedMapper() {
        return new JacksonMapper<Model>() {
            public ObjectMapper initMapper() {
                ObjectMapper mapper = new ObjectMapper();

                // TODO: All these should come from config-defaults.json.
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Don't include nulls.
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
                mapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
                mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
                // mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
                // mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL); // Will add type information.
                mapper.registerModule(new SimpleModule("ConfigMixins") {
                    @Override
                    public void setupModule(SetupContext context) {
                        // The alternative to inline "@c" would be default typing at mapper level but that is ugly.
                        // To prevent security holes the user entered content needs to forbid "@c".
                        // Object mixin has same effect as "enableDefaultTyping(DefaultTyping.NON_FINAL);"
                        // with the exception that everything else except collections and maps have the $c added
                        // inline as a property and not as a element in a new array like for .
                        context.setMixInAnnotations(Object.class, MixinsGenerator.BootstrapMixinObject.class);
                        context.setMixInAnnotations(Model.class, MixinsGenerator.NoClassAnnotationsModelMixin.class);
                    }

                });
                return mapper;
            }
        };

    }

	public static Mapper getMetaBasedMapper(Object config) {
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


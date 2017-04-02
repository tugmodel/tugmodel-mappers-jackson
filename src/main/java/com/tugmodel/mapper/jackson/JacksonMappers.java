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

    private static ObjectMapper prettyPrintObjectMapper;

    private static ObjectMapper getPrettyPrintObjectMapper() {
        if (prettyPrintObjectMapper == null) {
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
                    /**
                     * Mixin on Object adds "@c" also on arrays and maps which is ugly. On the other hand it creates
                     * the @c for the generic childs contained in model. The alternative solution would be
                     * adding @JsonSubTypes in at the Model level mixin or better add a mixin for each Model subclass.
                     * For the moment let's leave it as it is.
                     */
                    // On the other hand helps in providing type information in children contained in model.
                    // context.setMixInAnnotations(Object.class, MixinsGenerator.BootstrapMixinObject.class);
                    context.setMixInAnnotations(Model.class, MixinsGenerator.WithClassAnnotationsModelMixin.class);
                }

            });
            prettyPrintObjectMapper = mapper;
        }
        return prettyPrintObjectMapper;

    }
    // Used for pretty printing.
    public static JacksonMapper getPrettyPrintMapper() {
        return new JacksonMapper<Model>() {
            public ObjectMapper initMapper() {
                return getPrettyPrintObjectMapper();
            }
        };

    }

    private static ObjectMapper configReaderObjectMapper;

    private static ObjectMapper getConfigReaderObjectMapper() {
        if (configReaderObjectMapper == null) {
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

                    // context.setMixInAnnotations(Model.class,
                    // MixinsGenerator.WithClassAnnotationsModelMixinDefaultImpl.class);
                    context.setMixInAnnotations(Model.class, MixinsGenerator.NoTypeInfoModelMixin.class);

                }
            });

            configReaderObjectMapper = mapper;
        }
        return configReaderObjectMapper;

    }

    // Does basic json config reading, no metadata involved.
    public static Mapper getConfigReaderMapper() {      
        return new JacksonMapper<Model>() {
            public ObjectMapper initMapper() {
                return getConfigReaderObjectMapper();
            }
        };
    }
    
    private static ObjectMapper typedObjectMapper;

    private static ObjectMapper getTypedObjectMapper() {
        if (typedObjectMapper == null) {
            ObjectMapper mapper = new ObjectMapper();

            // TODO: All these should come from config-defaults.json.
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Don't include nulls.
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
            mapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
            // mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            // mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
            mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL); // Will add type information.
            mapper.registerModule(new SimpleModule("ConfigMixins") {
                @Override
                public void setupModule(SetupContext context) {
                    // The alternative to inline "@c" would be default typing at mapper level but that is ugly.
                    // To prevent security holes the user entered content needs to forbid "@c".
                    // Object mixin has same effect as "enableDefaultTyping(DefaultTyping.NON_FINAL);"
                    // with the exception that everything else except collections and maps have the $c added
                    // inline as a property and not as a element in a new array like for .

                    // Because of bug https://github.com/FasterXML/jackson-databind/issues/901 :
                    // Disable mapper level typing and instead use these in 2.8.6:
                    // context.setMixInAnnotations(Object.class, MixinsGenerator.BootstrapMixinObject.class);
                    // context.setMixInAnnotations(Model.class, MixinsGenerator.NoClassAnnotationsModelMixin.class);

                    context.setMixInAnnotations(Model.class, MixinsGenerator.TypedMapper.class);

                }

            });
            typedObjectMapper = mapper;
        }
        return typedObjectMapper;

    }

    // Does basic json config reading, no metadata involved.
    public static Mapper getTypedMapper() {
        return new JacksonMapper<Model>() {
            public ObjectMapper initMapper() {
                return getTypedObjectMapper();
            }
        };
    }

}
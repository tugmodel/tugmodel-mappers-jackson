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

import java.io.FileInputStream;
import java.lang.reflect.Field;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugmodel.client.mapper.BaseMapper;
import com.tugmodel.client.model.Model;

/**
 * Mapper implementation using Jackson. TODO: Configure Mapper via a
 * tm-mapper-defaults.json file under resources/config/.
 * 
 * For recursive dependencies check @JsonFilter : http://www.theotherian.com/2014/02/jackson-mixins-and-modules-external-classes.html
 * 
 * NOTE: getters and setters are used when serializing/deserializing a model. Also for attributes set using the generic
 *       set/get methods jsonanygetter and jsonanysetter annotations are used.
 */
public abstract class JacksonMapper<M extends Model> extends BaseMapper<M> {
    public static final String KEY_CLASS = "@c";
    protected ObjectMapper mapper;

    public abstract ObjectMapper initMapper();

    public ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = initMapper();
        }
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Object serialize(M src) {
        try {
            return getMapper().writeValueAsString(src);
        } catch (JsonProcessingException e) {
            return reThrow(e);
        }
    }

    @Override
    public M deserialize(Object src) {
        // new TypeReference<Map<String, Object>
        // http://stackoverflow.com/questions/11936620/jackson-deserialising-json-string-typereference-vs-typefactory-constructcoll
        // http://stackoverflow.com/questions/14362247/jackson-adding-extra-fields-to-an-object-in-serialization
        try {
            // TODO: Cache model class to avoid forName().
            // return (M)getMapper().readValue(src.toString(), Model.class);
            Class modelClass = Model.class;
            if (getTugConfig() != null) {
                modelClass = Class.forName((String) getTugConfig().getModel().get("class"));
            }
            //
            return (M) getMapper().readValue(src.toString(), modelClass);
        } catch (Exception e) {
            return (M) reThrow(e);
        }
    }

    @Override
    public Object serialize(Object src) {
        try {
            return getMapper().writeValueAsString(src);
        } catch (JsonProcessingException e) {
            return reThrow(e);
        }
    }

    @Override
    public <T> T deserialize(Object src, Class<T> destClass) {
        try {
            return getMapper().readValue(src.toString(), destClass);
        } catch (Exception e) {
            return (T) reThrow(e);
        }
    }

    public void updateModel(Object src, M dest) {
        // https://www.google.ro/search?q=screw+him&oq=screw+him&aqs=chrome..69i57j0l5.3257j0j4&sourceid=chrome&ie=UTF-8#q=jackson+serialize+on+existing+object&*
        // ATTENTION: Always specify the base Model.class on which the annotations are added.
        // https://www.google.com/webhp?sourceid=chrome-instant&ion=1&espv=2&ie=UTF-8#q=jackson+polymorphic+not+subtype+of&*
        // getMapper().readValue(src.toString(), Config.class); // Works if you remove defaultImpl = Model.class
        // ALTERNATIVE: use Model#merge and Mapper#deserialize.
        // Model srcModel = deserialize(src);
        // dest.merge(srcModel);
        try {
            // getMapper().readerForUpdating(dest).forType(Model.class).readValue((String) src);
            if (src instanceof String) {
                getMapper().readerForUpdating(dest).readValue((String) src);
            } else if (src instanceof Model) {
                updateModel(serialize((M) src), dest);
            }
        } catch (Exception e) {
            reThrow(e);
        }

    }

    @Override
    public <T> T convert(Object src, Class<T> destClass) {
        return (T) getMapper().convertValue(src, destClass); // getMapper().writeValueAsString(src)
                                                             // getMapper().convertValue(src, Object.class)
    }

    /**
     * Used in debug/development mode to have access to a pretty print of the actual model or object.
     */
    public String toPrettyString(Object src) {
        try {
            // return getMapper().writeValueAsString(fromValue);
            return JacksonMappers.getPrettyPrintMapper().getMapper().writeValueAsString(src);
        } catch (JsonProcessingException e) {
            return (String) reThrow(e);
        }
    }

    public Object reThrow(Exception e) {
        if (e instanceof JsonProcessingException) {
            StringBuilder mes = new StringBuilder();
            JsonProcessingException jpe = (JsonProcessingException) e;
            if (jpe.getLocation() != null) {
                mes.append("********** JSON ERROR ON LINE: " + jpe.getLocation().getLineNr() + " **********. ");
                Object source = jpe.getLocation().getSourceRef();
                if (source instanceof FileInputStream) {
                    try {
                        Field path = FileInputStream.class.getDeclaredField("path");
                        path.setAccessible(true);
                        mes.append("In file " + (String) path.get(source));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
            mes.append(e.getMessage());
            throw new RuntimeException(mes.toString(), e);
        }
        throw new RuntimeException(e);
    }
}
package org.raml.jaxrs.codegen.maven.annotators;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import org.jsonschema2pojo.Annotator;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.EnumSet;
import java.util.Iterator;

/**
 * Created by yf on 2017/2/17.
 */
public class MongoAnnotator implements Annotator{

    private boolean processed;
    private String currentClazz;

    @Override
    public void propertyOrder(JDefinedClass clazz, JsonNode propertiesNode) {

    }

    @Override
    public void propertyInclusion(JDefinedClass clazz, JsonNode schema) {
        clazz.annotate(Entity.class);
    }

    @Override
    public void propertyField(JFieldVar field, JDefinedClass clazz, String propertyName, JsonNode propertyNode) {
        String clazzName = clazz.name();
        if (!clazzName.equals(currentClazz)) {
            processed = false;
            if (propertyName.equals("id")) {
                field.annotate(Id.class);
                processed = true;
                currentClazz = clazzName;
                return;
            }
        }

        String typeName = field.type().fullName();
        if (!PrimitiveBoxedEnum.isContain(typeName)) {
            if((propertyNode.has("items") && propertyNode.findValue("items").has("$ref")) || propertyNode.has("$schema")) {
                field.annotate(Reference.class);
                System.out.println("@Reference :" + field.name());
            }else{
                field.annotate(Embedded.class);
                System.out.println("@Embedded :" + field.name());
            }
        }
    }

    @Override
    public void propertyGetter(JMethod getter, String propertyName) {

    }

    @Override
    public void propertySetter(JMethod setter, String propertyName) {

    }

    @Override
    public void anyGetter(JMethod getter) {

    }

    @Override
    public void anySetter(JMethod setter) {

    }

    @Override
    public void enumCreatorMethod(JMethod creatorMethod) {

    }

    @Override
    public void enumValueMethod(JMethod valueMethod) {

    }

    @Override
    public void enumConstant(JEnumConstant constant, String value) {

    }

    @Override
    public boolean isAdditionalPropertiesSupported() {
        return true;
    }

    @Override
    public void additionalPropertiesField(JFieldVar field, JDefinedClass clazz, String propertyName) {

    }

    enum PrimitiveBoxedEnum {

        Integer("java.lang.Integer"), Long("java.lang.Long"), Short("java.lang.Short"), Byte("java.lang.Byte"), Double(
                "java.lang.Double"), Boolean("java.lang.Boolean"), Float("java.lang.Float"), Character(
                "java.lang.Character"), String("java.lang.String");

        private String tail;

        private PrimitiveBoxedEnum(String val) {
            tail = val;
        }

        private static EnumSet<PrimitiveBoxedEnum> enumSet = EnumSet.allOf(PrimitiveBoxedEnum.class);

        public static EnumSet<PrimitiveBoxedEnum> callEnumSet() {
            return enumSet;
        }

        public static boolean isContain(String sapling) {

            Iterator<PrimitiveBoxedEnum> it = enumSet.iterator();
            while (it.hasNext()) {
                PrimitiveBoxedEnum forest = it.next();
                if (forest.tail.equals(sapling)) {
                    return true;
                }
            }
            return false;
        }
    }
}

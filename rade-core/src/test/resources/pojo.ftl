package ${packageName};

public class ${className} {
<#list fields?keys as fieldName>
    <#assign fieldType = fields[fieldName]>
    <#if fieldType?is_map>
        private ${className}.${fieldName} ${fieldName};
    <#else>
        private ${fieldType} ${fieldName};
    </#if>
</#list>

<#--<#list fields?keys as fieldName>
    <#assign fieldType = fields[fieldName]>
    <#if fieldType?is_map>
        public ${className}.${fieldName} get${fieldName?cap_first}() {
        return this.${fieldName};
        }

        public void set${fieldName?cap_first}(${className}.${fieldName} ${fieldName}) {
        this.${fieldName} = ${fieldName};
        }
    <#else>
        public ${fieldType} get${fieldName?cap_first}() {
        return this.${fieldName};
        }

        public void set${fieldName?cap_first}(${fieldType} ${fieldName}) {
        this.${fieldName} = ${fieldName};
        }
    </#if>
</#list>
-->
<#list fields?keys as fieldName>
    <#assign fieldType = fields[fieldName]>
    <#if fieldType?is_map>
        public class ${fieldName} {
        <#list fieldType?keys as nestedFieldName>
            <#assign nestedFieldType = fieldType[nestedFieldName]>
            private ${nestedFieldType} ${nestedFieldName};
        </#list>

        <#list fieldType?keys as nestedFieldName>
            <#assign nestedFieldType = fieldType[nestedFieldName]>
            public ${nestedFieldType} get${nestedFieldName?cap_first}() {
            return this.${nestedFieldName};
            }

            public void set${nestedFieldName?cap_first}(${nestedFieldType} ${nestedFieldName}) {
            this.${nestedFieldName} = ${nestedFieldName};
            }
        </#list>
        }
    </#if>
</#list>
}
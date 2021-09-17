    <resultMap id="BaseResultMap" type="${entityPackage}.${entity.entityName}">
[#list entity.fields as field]
    [#if field.columnName == 'id']
        <id column="id" property="id"/>
    [#else ]
        <result column="${field.columnName}" property="${field.lowerCamelName}"/>
    [/#if]
[/#list]
    </resultMap>

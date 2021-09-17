    <sql id="BaseColumnList">
        [#list entity.fields as field]`${field.columnName}`[#if field_has_next],[/#if][/#list]
    </sql>

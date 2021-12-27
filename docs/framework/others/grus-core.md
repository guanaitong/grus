## Bean 复制工具

bean 复制使用 cglib 库的 API 实现，使用方法如下：

```java
BeanDTO dto = new BeanDTO();
BeanBO bo = BeanCopyUtil.copy(dto, BeanBO.class);
```

```java
List<BeanDTO> dtoList = new ArrayList();
List<BeanBO> bo = BeanCopyUtil.copyList(dtoList, BeanBO.class);
```

有时我们会遇到两个对象大部分字段一致，一两个字段的命名不同，这种情况可以声明一个额外的转换方法。在相同字段拷贝后，会执行该转换方法，设置需要的字段。

```java
public class TestExtraConverter implements BeanExtraConverter<BeanDTO, BeanBO> {
        @Override
        public void afterProcess(BeanDTO dto, BeanBO bo) {
            bo.setName(dto.getPersonName());
        }
 }

public void copyList() {
        TestExtraConverter converter = new TestExtraConverter();
        List<BeanDTO> dtoList = new ArrayList();
        List<BeanBO> bo = BeanCopyUtil.copyList(dtoList, BeanBO.class, converter);
}
```

额外转换方法在 List 转换时尤为有用，不需要增加额外的 for 循环即能完成列表的额外转换。

## Bean 和 Map 互转工具

bean 和 map 的互转也常常被用到，cglib 也有 API 支持，封装后的使用方法如下：

```java
Bean bean = new Bean();
Map<String, Object> beanMap = BeanMapUtil.bean2Map(bean);
```

```java
Map<String, Object> beanMap = new HashMap<>();
Bean bean = BeanMapUtil.map2Bean(beanMap, Bean.class);
```

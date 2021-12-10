import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

/*
 * Available context bindings:
 *   SELECTION   Iterable<DasObject>
 *   PROJECT     project
 *   FILES       files helper
 */

packageName = "com.ciicgat.thirdpayment.domain.entity;"
typeMapping = [
  (~/(?i)tinyint|smallint/)         : "Integer",
  (~/(?i)int/)                      : "Long",
  (~/(?i)float|double|decimal|real/): "BigDecimal",
  (~/(?i)datetime|timestamp/)       : "Date",
  (~/(?i)date/)                     : "Date",
  (~/(?i)time/)                     : "Date",
  (~/(?i)/)                         : "String"
]

FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
  SELECTION.filter { it instanceof DasTable && it.getKind() == ObjectKind.TABLE }.each { generate(it, dir) }
}

def generate(table, dir) {
  def className = javaName(table.getName() ,true) + "DO"
  def imports = calcImports(table)
  def fields = calcFields(table)
  new File(dir, className + ".java").withPrintWriter { out -> generate(out, className, imports, fields) }
}

def generate(out, className, imports, fields) {
  out.println "package $packageName"
  out.println ""

  imports.each() {
    out.println "$it"
  }

  out.println ""
  out.println "public class $className {"
  out.println ""

  for(i = 0; i < fields.size; i++){
    it = fields.get(i)
    if (it.annos != "") out.println "  ${it.annos}"
    out.println "  private ${it.type} ${it.name};"
    if(i != fields.size - 1) {
      out.println ""
    }
  }

  fields.each() {
    out.println ""
    out.println "  public ${it.type} get${it.name.capitalize()}() {"
    out.println "    return ${it.name};"
    out.println "  }"
    out.println ""
    out.println "  public void set${it.name.capitalize()}(${it.type} ${it.name}) {"
    out.println "    this.${it.name} = ${it.name};"
    out.println "  }"
  }
  out.println "}"
}

def calcImports(table) {
  imports = []
  DasUtil.getColumns(table).each() {
      def spec = Case.LOWER.apply(it.getDataType().getSpecification())
      def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
      if ("Date".equals(typeStr)){
        imports += ["import java.util.Date;"]
      }else if ("BigDecimal".equals(typeStr)){
        imports += ["import java.math.BigDecimal;"]
      }
  }
  imports = imports.unique();
}

def calcFields(table) {
  DasUtil.getColumns(table).reduce([]) { fields, col ->
    def spec = Case.LOWER.apply(col.getDataType().getSpecification())
    def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
    fields += [[
                 name : javaName(col.getName(), false),
                 type : typeStr,
                 annos: ""]]
  }
}

def javaName(str, capitalize) {
  def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
    .collect { Case.LOWER.apply(it).capitalize() }
    .join("")
    .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
  capitalize || s.length() == 1? s : Case.LOWER.apply(s[0]) + s[1..-1]
}

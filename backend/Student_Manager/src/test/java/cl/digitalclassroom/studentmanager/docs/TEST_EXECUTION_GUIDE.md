# Guía de Ejecución de Pruebas - Student Manager

## 📋 Requisitos Previos

- Java 17 o superior
- Maven 3.8+
- IDE (VS Code, IntelliJ IDEA, Eclipse, etc.)

## 🏃 Ejecutar Pruebas

### 1. Ejecutar TODAS las Pruebas
```bash
cd backend/Student_Manager
mvn clean test
```

### 2. Ejecutar Pruebas Específicas por Clase

```bash
# Pruebas Unitarias del Controller
mvn test -Dtest=StudentControllerTest

# Pruebas de Integración del Controller
mvn test -Dtest=StudentControllerIntegrationTest

# Pruebas E2E/Escenarios
mvn test -Dtest=StudentControllerE2ETest

# Pruebas Unitarias del Servicio
mvn test -Dtest=StudentServiceUnitTest

# Pruebas del Repositorio
mvn test -Dtest=StudentRepositoryTest

# Pruebas del Validador RUT
mvn test -Dtest=RUTValidatorTest

# Pruebas del Validador Phone
mvn test -Dtest=PhoneValidatorTest
```

### 3. Ejecutar Pruebas Específicas por Método

```bash
# Un método específico
mvn test -Dtest=StudentControllerTest#testCreateStudentSuccess

# Múltiples métodos
mvn test -Dtest=StudentControllerTest#testCreateStudentSuccess+testGetAllStudentsSuccess
```

### 4. Ejecutar Pruebas de Integración Solamente

```bash
mvn test -Dtest=*IntegrationTest,*E2ETest
```

### 5. Ejecutar Pruebas Unitarias Solamente

```bash
mvn test -Dtest=*Test -Dgroups=unit
```

## 📊 Generar Reporte de Cobertura

### Con JaCoCo

```bash
# Generar reporte de cobertura
mvn clean test jacoco:report

# Reporte estará en: target/site/jacoco/index.html
```

### Verificar Cobertura Mínima

```bash
mvn clean test jacoco:report jacoco:check \
  -Djacoco.rules={'element':'PACKAGE','includes':'cl.digitalclassroom.studentmanager.*','limit':{'counter':'LINE','value':'COVEREDRATIO','minimum':'0.80'}}
```

## 🧪 Ejecutar Pruebas en Modo Watch (Continuo)

### Con Maven (cada cambio)
```bash
mvn clean test -Dorg.slf4j.simpleLogger.defaultLogLevel=INFO -X
```

## 📈 Reportes y Métricas

### Generar Reporte HTML Completo
```bash
mvn clean test site -DskipTests=false
# Reporte en: target/site/index.html
```

### Reporte de Surefire
```bash
mvn surefire-report:report
# Reporte en: target/site/surefire-report.html
```

## 🔍 Debug de Pruebas

### Ejecutar en Modo Debug
```bash
mvn -Dmaven.surefire.debug test
```

Luego conectar el debugger de tu IDE a puerto 5005.

### Ver Logs Detallados
```bash
mvn test -X -e
```

### Ver SQL Ejecutado
En `src/test/resources/application-test.properties`, cambiar:
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## 🎯 Filtrar Pruebas por Anotaciones

### Solo pruebas de "Integración"
```bash
mvn test -Dgroups=integration
```

### Solo pruebas "Rápidas"
```bash
mvn test -Dgroups=fast
```

## 🚀 Ejecutar en CI/CD

### GitHub Actions Ejemplo
```yaml
name: Tests
on: [push]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '17'
      - run: cd backend/Student_Manager && mvn clean test jacoco:report
```

## 📋 Matriz de Ejecución de Pruebas

| Objetivo | Comando | Tiempo Aproximado |
|----------|---------|------------------|
| Verificación rápida | `mvn test -Dtest=StudentControllerTest` | 10s |
| Todas pruebas unitarias | `mvn test -Dtest=*Test -Dgroups=unit` | 30s |
| Pruebas integración | `mvn test -Dtest=*IntegrationTest` | 45s |
| Cobertura completa | `mvn clean test jacoco:report` | 60s |
| CI/CD completo | `mvn clean verify jacoco:report` | 90s |

## ✅ Verificación de Salud del Proyecto

```bash
# Ejecutar todas las pruebas y generar reportes
mvn clean test jacoco:report && \
mvn surefire-report:report && \
echo "✅ Todas las pruebas completadas"
```

## 🐛 Solucionar Problemas Comunes

### Error: "No tests found"
```bash
# Asegurarse que los tests están en src/test/java
# y nombrados correctamente (*Test.java o *Tests.java)
mvn test -X
```

### Error: "Database connection refused"
```bash
# Verificar que H2 esté disponible en el pom.xml para test
# Ver: application-test.properties debe estar en src/test/resources
```

### Error: "Port already in use"
```bash
# Las pruebas usan H2 in-memory, no debería pasar
# Si pasa, revisar si hay servidor corriendo
lsof -i :8080  # Buscar procesos en puerto 8080
```

### Tests muy lentos
```bash
# 1. Revisar que no haya muchas queries
mvn test -Dtest=StudentRepositoryTest -X 2>&1 | grep "select\|insert\|update"

# 2. Usar perfiles de prueba más rápidos
mvn test -Dspring.test.mockmvc.print=true
```

## 📊 Interpretar Resultados

### Salida Normal de Éxito
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running cl.digitalclassroom.studentmanager.controller.StudentControllerTest
[INFO] Tests run: 27, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.345 s
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 160, Failures: 0, Errors: 0, Skipped: 0
[INFO] ✅ BUILD SUCCESS
```

### Salida con Fallos
```
[ERROR] Tests run: 160, Failures: 3, Errors: 1, Skipped: 2
[ERROR] ❌ BUILD FAILURE

[ERROR] FAILURE at cl.digitalclassroom.studentmanager.controller.StudentControllerTest
[ERROR] testCreateStudentSuccess
[ERROR] Expected: Carlos García López
[ERROR] Actual:   Carlos Garcia Lopez
```

## 💡 Tips y Mejores Prácticas

1. **Ejecutar pruebas antes de commit**
   ```bash
   # Añadir a pre-commit hook
   mvn clean test jacoco:report
   ```

2. **Mantener pruebas independientes**
   - No depender del orden de ejecución
   - Cada test limpia su estado

3. **Usar perfiles específicos**
   ```bash
   mvn test -Ptest-fast -Dspring.jpa.show-sql=false
   ```

4. **Monitorear cobertura**
   - Objetivo: >80% cobertura
   - Crítico: >90% en lógica de negocio

5. **Documentar cambios en tests**
   ```java
   @DisplayName("Crear - debe validar RUT chileno")
   // Documentación clara del test
   void testCreateValidatesRut() { ... }
   ```

## 🔗 Recursos Útiles

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/assertj-core-features-highlight.html)
- [Spring Boot Testing Guide](https://spring.io/guides/gs/testing-web/)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)

## 📞 Soporte

Si encuentras problemas:
1. Revisar `target/surefire-reports/` para detalles de fallos
2. Ejecutar con flag `-X` para debug
3. Revisar logs en `target/surefire-reports/*.txt`

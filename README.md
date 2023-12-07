
# Transformaciones de consultas SQL


## Introducción

_Este informe documenta el desarrollo de una herramienta para detectar "bad smells" en consultas SQL utilizando ANTLR y Java. El objetivo es identificar prácticas ineficientes o problemáticas en el código SQL para optimizar el rendimiento y la mantenibilidad de las bases de datos.
Este trabajo se desarrolla para la materia Orientación a Objetos II (UNLP)._

*Ezequiel Carletti*

## Índice

1. [Introducción](#introducción)
2. [Herramientas y Tecnologías Utilizadas](#herramientas-y-tecnologías-utilizadas)
    1. [Instalación y Configuración](#instalación-y-configuración)
        1. [Instalación de Eclipse](#instalación-de-eclipse)
        2. [Configuración de Eclipse para ANTLR y JUnit](#configuración-de-eclipse-para-antlr-y-junit)
        3. [Configuración del Proyecto](#configuración-del-proyecto)
3. [Estructura del Proyecto](#estructura-del-proyecto)
    1. [Módulos y Clases Principales](#módulos-y-clases-principales)
    2. [Integración de Componentes](#integración-de-componentes)
    3. [Contribuciones de los Desarrolladores](#contribuciones-de-los-desarrolladores)
    4. [Uso del Proyecto](#uso-del-proyecto)
4. [Detección de Bad Smells](#detección-de-bad-smells)
    1. [Subconsultas en SELECT](#subconsultas-en-select)
    2. [Uso de COUNT(*)](#uso-de-count*)
    3. [Uso de SELECT DISTINCT](#uso-de-select-distinct)
    4. [Uso de OR](#uso-de-or)
    5. [Uso de Comodines Líderes en LIKE](#uso-de-comodines-líderes-en-like)
    6. [Uso de ORDER BY sin LIMIT](#uso-de-order-by-sin-limit)
    7. [SELECT *](#SELECT-*)
5. [Pruebas Unitarias](#pruebas-unitarias)
    1. [DetectSubqueriesInSelectTest](#detectsubqueriesinselecttest)
    2. [DetectBadSmellCountTest](#detectbadsmellcounttest)
    3. [DetectBadSmellDistinctTest](#detectbadsmelldistincttest)
    4. [DetectBadSmellOrInTest](#detectbadsmellorintest)
    5. [DetectLikeWithLeadingWildcardTest](#detectlikewithleadingwildcardtest)
    6. [DetectOrderByWithoutLimitTest](#detectorderbywithoutlimittest)
    7. [DetectSelectStarTest](#detectselectstartest)
6. [Conclusiones](#conclusiones)
7. [Desafíos y Aprendizajes](#desafíos-y-aprendizajes)
    1. [Aprendizaje del Patrón Visitor](#aprendizaje-del-patrón-visitor)
    2. [Aprendizaje de SQL](#aprendizaje-de-sql)
 8. [Referencias](#referencias)


## Herramientas y Tecnologías Utilizadas

- **ANTLR**: Utilizado para generar el parser de SQL.
- **Java**: Lenguaje de programación utilizado para desarrollar la lógica de detección.
- **JUnit**: Para pruebas unitarias.
- **Eclipse**: Editor de codigo.

### Instalación y Configuración

Para llevar a cabo este proyecto, se utilizó el entorno de desarrollo Eclipse, que ofreció un soporte robusto y herramientas necesarias para la implementación y prueba del proyecto. A continuación se detallan los pasos para la instalación y configuración del entorno:

#### Instalación de Eclipse

1. **Descargar Eclipse:** Primero, se debe descargar e instalar Eclipse IDE desde [el sitio oficial de Eclipse](https://www.eclipse.org/downloads/).

#### Configuración de Eclipse para ANTLR y JUnit

Para configurar Eclipse con las herramientas necesarias para nuestro proyecto, se siguieron los siguientes pasos:

1. **Instalación de ANTLR 4 IDE 0.3.6:**
   - En Eclipse, se accede al `Eclipse Marketplace` desde el menú `Help`.
   - Se busca `ANTLR 4 IDE` y se selecciona la versión `0.3.6`.
   - Se sigue el proceso de instalación, aceptando los términos y reiniciando Eclipse si es necesario.

2. **Instalación de JUnit-Tools 1.1.0:**
   - Siguiendo un proceso similar, se accede al `Eclipse Marketplace`.
   - Se busca `JUnit-Tools` y se selecciona la versión `1.1.0`.
   - Se completa la instalación y se reinicia Eclipse si es necesario.

#### Configuración del Proyecto

Una vez instaladas estas herramientas, se procede a la configuración del proyecto:

1. **Creación de un Nuevo Proyecto:**
   - Se crea un nuevo proyecto Java en Eclipse.
   - Se configura el proyecto para utilizar las librerías de ANTLR y JUnit.

2. **Importación de la Gramática de SQL:**
   - Se importa el archivo de gramática `sqlite.g4` al proyecto.
   - Utilizando ANTLR 4 IDE, se generan automáticamente el parser, lexer y clases de visitor/listener necesarias.



## Estructura del Proyecto

El proyecto `antlrCodeSmellDetector` está diseñado para identificar y reportar "bad smells" en consultas SQL, utilizando una gramática SQLite definida por ANTLR 4. El proyecto se estructura en torno a la generación automática de clases de análisis de código y una serie de clases de detección y pruebas diseñadas específicamente para la detección de malas prácticas en SQL.

### Módulos y Clases Principales

#### Generados por ANTLR 4

-   **`SQLiteBaseListener.java`**: Clase base para la implementación de listeners generada por ANTLR 4 a partir de la gramática SQLite.
-   **`SQLiteLexer.java`**: Lexer ANTLR 4 generado que convierte la entrada de SQL en tokens.
-   **`SQLiteListener.java`**: Interfaz listener para reaccionar a los eventos del parseo del SQL.
-   **`SQLiteParser.java`**: Parser ANTLR 4 generado que construye el árbol de análisis sintáctico a partir de tokens.
-   **`SQLiteVisitor.java`**: Interfaz visitor generada que permite operaciones personalizadas en el árbol de análisis sintáctico.

#### Desarrolladas para Detección de Bad Smells

-   **`SQLCodeSmellDetector.java`**: Clase principal que ejecuta el análisis de la consulta SQL proporcionada.
-   **`SQLSmellDetectorVisitor.java`**: Clase que extiende de `SQLiteVisitor`, sobrescribe métodos para detectar bad smells específicos.
-   **`ExpressionSmellDetector.java`**: Clase auxiliar para encapsular la lógica de detección de bad smells relacionados con expresiones SQL.

#### Utilidades y Clases de Ayuda

-   **`SQLUtilityExpression.java`**: Clase que proporciona métodos de utilidad para encontrar contextos específicos dentro del árbol de análisis sintáctico.

#### Clases de Pruebas Unitarias

-   **`DetectBadSmellCountTest.java`**
-   **`DetectBadSmellDistinctTest.java`**
-   **`DetectBadSmellOrInTest.java`**
-   **`DetectLikeWithLeadingWildcardTest.java`**
-   **`DetectOrderByWithoutLimitTest.java`**
-   **`DetectSelectStarTest.java`**
-   **`DetectSubqueriesInSelectTest.java`**

Estas clases contienen pruebas unitarias para cada tipo de bad smell detectado, asegurando que la lógica de detección funcione como se espera.

### Integración de Componentes

El proyecto utiliza la gramática SQLite proporcionada por el repositorio `sqlite-parser` para generar el lexer, parser, listener y visitor necesarios para analizar consultas SQL. La clase `SQLCodeSmellDetector` inicia el proceso de análisis tomando una consulta SQL como entrada, la cual se procesa a través de `SQLiteLexer` y `SQLiteParser` para construir el árbol de análisis sintáctico.

El árbol resultante es recorrido por `SQLSmellDetectorVisitor`, que implementa la lógica de detección de bad smells. Para ciertos tipos de expresiones, delega en la clase `ExpressionSmellDetector`, que aplica reglas específicas de detección.

Las pruebas unitarias en la carpeta `test` cubren diversos casos de uso y escenarios para asegurar la detección efectiva y confiable de bad smells en el código SQL.

### Contribuciones de los Desarrolladores

Este proyecto integra contribuciones de los siguientes desarrolladores y fuentes:

-   Bart Kiers, principal autor de la gramática SQLite ANTLR 4.
-   Martin Mirchev y Mike Lische, colaboradores adicionales en la gramática SQLite ANTLR 4.
-   [Link](https://github.com/bkiers/sqlite-parser)

### Uso del Proyecto

Para utilizar el detector de bad smells, se debe proporcionar una consulta SQL a `SQLCodeSmellDetector`, que invocará al `SQLSmellDetectorVisitor` para analizar la consulta y reportar los bad smells detectados. La modularización en `ExpressionSmellDetector` permite una mayor flexibilidad y mantenibilidad al agregar o modificar reglas de detección. Las clases de prueba unitaria garantizan la confiabilidad del sistema a través de pruebas exhaustivas de cada regla de detección implementada.

## Detección de Bad Smells

La detección de "bad smells" en consultas SQL es una parte crucial del mantenimiento y optimización de bases de datos. Estos "bad smells" son prácticas que, aunque no incorrectas sintácticamente, pueden llevar a un rendimiento subóptimo o a una mala legibilidad del código. A continuación, se detallan los "bad smells" detectados en el proyecto `antlrCodeSmellDetector`.

### 1 - Subconsultas en SELECT

**Impacto:** Las subconsultas en la cláusula SELECT pueden ser menos eficientes que las operaciones de JOIN, especialmente cuando se invocan por cada fila de la consulta principal. 
**Detección:** Se detectan mediante la iteración a través de todos los hijos de `ExprContext` buscando instancias de `Select_stmtContext`. La clase `ExpressionSmellDetector` realiza esta comprobación y devuelve `true` si se encuentra una subconsulta.



### 2 - Uso de COUNT(*)

**Impacto:** Aunque `COUNT(*)` es válido, el uso de `COUNT(column)` puede ser más claro y, en algunos casos, más eficiente si la columna no admite valores NULL. 
**Detección:** Se revisa si la expresión es una llamada a la función COUNT y si el argumento es `*`. La clase `ExpressionSmellDetector` se encarga de identificar y reportar esta situación.


### 3 - Uso de SELECT DISTINCT

**Impacto:** El uso de `SELECT DISTINCT` en consultas SQL, tanto con como sin JOINs, puede tener diferentes impactos. Con JOINs, puede llevar a una sobrecarga innecesaria y a resultados confusos si no se gestiona adecuadamente, debido a la combinación de filas de múltiples tablas. Sin JOINs, aunque menos problemático, `DISTINCT` puede ser un indicativo de diseño de datos subóptimo, como la presencia de duplicados que podrían evitarse con una mejor normalización de la base de datos.

**Detección:**

-   **Con JOINs:** Se detecta cuando la cláusula `DISTINCT` se utiliza en consultas que incluyen JOINs. La presencia simultánea de `DISTINCT` y JOINs en `Select_coreContext` se informa como potencialmente problemática y se marca en la variable `detectedDistinct`.
-   **Sin JOINs:** Se detecta el uso de `DISTINCT` en consultas sin JOINs. Aunque no es necesariamente un "bad smell" en todos los contextos, puede ser un indicador de que la consulta y/o el diseño de la base de datos podrían optimizarse. La detección se enfoca en identificar usos de `DISTINCT` que podrían ser innecesarios o indicativos de problemas subyacentes en la estructura de datos.

### 4 - Uso de OR

**Impacto:** El uso excesivo de la palabra clave OR en las cláusulas WHERE puede ser ineficiente y a menudo puede reemplazarse por el operador IN para una mejor rendimiento.
 **Detección:** Se examina si `ExprContext` contiene el operador OR y si se compara la misma columna en ambos lados del OR. Esto se identifica y reporta como una mala práctica.
 
###  5 - Uso de Comodines Líderes en LIKE

**Impacto:** El uso de comodines líderes en las cláusulas `LIKE` de SQL, específicamente el carácter `%` al inicio de un patrón de búsqueda, puede degradar significativamente el rendimiento de las consultas. Esta práctica impide que la base de datos utilice índices de manera eficiente, lo que resulta en una búsqueda más lenta y costosa, particularmente en grandes conjuntos de datos.

**Detección:** La clase `ExpressionSmellDetector` identifica y señala el uso de comodines líderes en las cláusulas `LIKE`. Se busca específicamente si el patrón de búsqueda comienza con `%`, lo cual es un indicativo de esta práctica ineficiente.

### 6 - Uso de ORDER BY sin LIMIT

**Impacto:** Usar ORDER BY sin LIMIT puede causar una carga excesiva si hay muchos resultados, ya que ordena todos los datos recuperados. 
**Detección:** La clase `SQLSmellDetectorVisitor` busca cláusulas ORDER BY sin un LIMIT asociado y marca esto como un "bad smell".


### 7 -  SELECT *

**Impacto:** Utilizar `SELECT *` puede reducir la eficiencia al recuperar más datos de los necesarios. Afecta el rendimiento y puede incluir cambios inesperados si la estructura de la tabla cambia. 
**Detección:** Se identifica cuando el `Result_columnContext` es igual a `*`. La clase `SQLSmellDetectorVisitor` marca esto como una práctica desaconsejada y actualiza la variable `detectedSelectStar`.

## Pruebas Unitarias

Las pruebas unitarias son fundamentales en cualquier proyecto de desarrollo de software, ya que permiten verificar que cada unidad de código funcione correctamente de forma aislada. En el caso del proyecto `antlrCodeSmellDetector`, se implementan pruebas unitarias para cada "bad smell" detectable, incluyendo la detección de subconsultas en la cláusula SELECT de las consultas SQL.

## Pruebas Unitarias para detectar Subconsultas 

### Clase `DetectSubqueriesInSelectTest`

Esta clase está diseñada para ejecutar pruebas unitarias sobre el método `detectSubqueriesInSelect` de la clase `ExpressionSmellDetector`. Las pruebas se centran en identificar si las subconsultas están siendo utilizadas dentro de la cláusula SELECT, lo cual es considerado una mala práctica que puede afectar el rendimiento de la consulta.

##### Metodología de la Prueba

-   **Preparación (`setUp`):** Se instancia `ExpressionSmellDetector` y `SQLUtilityExpression` para utilizarlos en las pruebas.
-   **Datos de Prueba (`sqlQueries`):** Se proporciona una serie de consultas SQL con una expectativa (`true` o `false`) de si se detectará o no el "bad smell".
-   **Ejecución de la Prueba (`testDetectSubqueriesInSelect`):** Se analiza la consulta SQL proporcionada y se verifica si el resultado coincide con la expectativa. La aserción final compara el resultado de la detección con la expectativa definida.

## 1 - Casos de prueba:  para detectar Subconsultas

Los casos de prueba incluyen ejemplos donde se espera detectar el "bad smell" (expectativa `true`) y ejemplos donde no se espera detectarlo (expectativa `false`). Por ejemplo:

-   **Subconsulta en Where**:

```sql
  SELECT name FROM users WHERE id IN (SELECT user_id FROM orders);
```

El uso de subconsultas en la cláusula `WHERE` puede ser ineficiente porque cada fila de la tabla exterior (`users`) requiere que se ejecute la subconsulta. Esto puede ralentizar significativamente la consulta si la tabla `orders` es grande.

-   **Subconsulta en HAVING**:
    
```sql
    SELECT department_id, COUNT(*) FROM employees GROUP BY department_id HAVING COUNT(*) > (SELECT COUNT(*) / 10 FROM employees);` 
```
    
Similar al anterior, la subconsulta en la cláusula `HAVING` se ejecuta para cada grupo, lo cual puede ser menos eficiente que calcular el valor una vez y referenciarlo.
    
-   **Subconsulta Correlacionada en SELECT**:
    
    
```sql
SELECT name, (SELECT MAX(salary) FROM employees WHERE department = users.department) AS max_salary FROM users;` 
```
Las subconsultas correlacionadas como esta pueden ser particularmente costosas en términos de rendimiento porque para cada fila en `users`, la subconsulta debe ser ejecutada de nuevo, considerando todos los `employees` del mismo `department`.
    
-   **Subconsulta con EXISTS**:
    
```sql
SELECT name FROM users WHERE EXISTS (SELECT * FROM orders WHERE orders.user_id = users.id);
```
    
Aunque `EXISTS` puede ser más eficiente que `IN`, sigue siendo un "bad smell" si se usa incorrectamente o la subconsulta se ejecuta muchas veces.
    
-   **Múltiples Subconsultas en SELECT**:
    
```sql    
SELECT name, (SELECT AVG(salary) FROM employees WHERE department = users.department), (SELECT COUNT(*) FROM orders WHERE user_id = users.id) FROM users;
``` 
    
Múltiples subconsultas en la cláusula `SELECT` pueden ser una señal de que la consulta podría beneficiarse de una reescritura utilizando `JOIN`, lo que podría mejorar el rendimiento.
    

### Consultas sin Bad Smells (False / para Detectar Subconsultas en SELECT)

-   **Uso de JOIN y GROUP BY**:
    
```sql 
SELECT users.name, COUNT(orders.id) AS order_count FROM users JOIN orders ON users.id = orders.user_id GROUP BY users.id;
``` 
    
Las `JOIN` con `GROUP BY` son generalmente más eficientes que las subconsultas y están libres de bad smells en este contexto.
    
-   **Agregación y Agrupación Adecuadas**:
    

```sql    
SELECT department_id, COUNT(*) FROM employees GROUP BY department_id HAVING COUNT(*) > 5;
```
Utilizar `GROUP BY` con una cláusula `HAVING` simple no es un bad smell siempre que se haga de manera eficiente y el número de grupos no sea excesivamente grande.
    
-   **Consultas Simples**:

```sql    
    SELECT name, salary FROM users;
    SELECT name FROM users WHERE id > 1000;
    SELECT name FROM users;` 
```
Estas consultas simples no contienen subconsultas, `JOIN` complicados ni funciones de agregación indebidas, por lo que no se consideran bad smells.
    
-   **Uso de JOIN para Filtrado**:
```sql    
    SELECT users.name FROM users JOIN orders ON users.id = orders.user_id WHERE orders.amount > 1000 GROUP BY users.id;
```
Este patrón es efectivo y eficiente para relacionar y filtrar datos entre tablas, y no representa un bad smell.

Estos casos ayudan a asegurar que el detector no solo identifica correctamente las malas prácticas, sino que también no genera falsos positivos en consultas bien formuladas.

## 2 - Casos de prueba: para detectar COUNT(*)

El método `detectBadSmellCount` se utiliza para identificar prácticas potencialmente ineficientes en el uso de la función de agregación `COUNT(*)` en consultas SQL. Aquí se presentan tres casos de prueba con su correspondiente expectativa de detección de bad smells.  

-   **Caso 1: Uso de COUNT(*) en Subconsultas**:
```sql 
SELECT name, (SELECT COUNT(*) FROM orders WHERE user_id = users.id) AS order_count FROM users;
```
**Expectativa**: Verdadero (detecta un bad smell)

**Razón**: El uso de `COUNT(*)` dentro de una subconsulta puede ser ineficiente. Cuando se ejecuta esta subconsulta para cada fila de la tabla principal, puede llevar a una gran cantidad de cálculos repetitivos, especialmente si la tabla principal (`users`) tiene un número significativo de filas. Esto puede degradar el rendimiento de la consulta.


-   **Caso 2: Uso de COUNT(*) sin Restricciones**:
```sql 
SELECT COUNT(*) FROM users;
```
**Expectativa**: Verdadero (detecta un bad smell)

**Razón**: Aunque el uso de `COUNT(*)` para contar todas las filas de una tabla no es inherentemente un bad smell, en ciertos contextos, como cuando no hay necesidad de contar todas las filas o cuando se podría aplicar una restricción para reducir la carga de trabajo, podría considerarse una práctica ineficiente. Por ejemplo, si el conteo se está utilizando solo para comprobar la existencia de filas, `EXISTS` sería una alternativa más eficiente.

-   **Caso 3: Contar una Columna Específica**:
```sql 
SELECT COUNT(ID) FROM users;
```

**Expectativa**: Falso (no detecta un bad smell)

**Razón**: Contar una columna específica, en este caso, `ID`, generalmente es más eficiente que `COUNT(*)`, ya que puede aprovechar los índices de la columna si existen. Además, especificar una columna sugiere que la intención del conteo es más deliberada, lo que podría estar alineado con los requisitos de la lógica de negocio, como contar solo las filas con un `ID` no nulo.

## 3 - Caso de Prueba: Uso de DISTINCT 

El uso de `DISTINCT` es a menudo un punto de preocupación en la optimización de consultas SQL debido a su impacto potencial en el rendimiento. La cláusula `DISTINCT` puede llevar a una sobre-carga adicional, ya que el sistema de gestión de bases de datos necesita realizar trabajo extra para garantizar que los resultados sean únicos. 

#### Casos Potenciales de Bad Smell  

-   **Caso 1: uso DISTINCT**:
```sql 
SELECT DISTINCT name FROM users;
```
**Expectativa**: Verdadero (detecta un bad smell)

**Razón**: El uso de `DISTINCT` en toda la tabla `users` podría indicar un problema de diseño de la base de datos, donde se esperan nombres duplicados. Si la tabla está correctamente normalizada, el uso de `DISTINCT` podría ser innecesario y perjudicar el rendimiento.

-   **Caso 2: Uso DISTINCT con JOIN**:
- 
```sql 
SELECT DISTINCT name FROM users JOIN orders ON users.id = orders.user_id;
```
**Expectativa**: Verdadero (detecta un bad smell)

**Razón**: Aquí, `DISTINCT` se utiliza en una unión con la tabla `orders`, lo que puede ser una señal de que se están produciendo resultados duplicados debido a la naturaleza de la relación entre usuarios y pedidos. El uso de `DISTINCT` en tales escenarios puede ser un indicativo de una consulta mal optimizada que necesita una revisión, como la inclusión de una cláusula `GROUP BY` o la reestructuración de la relación de las tablas.

-   **Caso 3: Uso GROUP BY**:
```sql 
SELECT name FROM users GROUP BY name;
```
**Expectativa**: Falso (no detecta un bad smell)

**Razón**: El uso de `GROUP BY` sugiere que se está realizando una agregación o que se está asegurando la unicidad a través de un método diferente, que puede ser más eficiente que `DISTINCT`. Esto se considera una práctica aceptable y no es un bad smell.

## 4 - Casos de prueba: para detectar OR 

La cláusula `OR` puede ser un indicador de bad smells en SQL cuando se usa para comparar la misma columna con diferentes valores. Esto se debe a que puede conducir a una ejecución de consulta menos eficiente en comparación con el uso de `IN`. La razón principal de esto es que `OR` puede impedir que el optimizador de consultas utilice índices de manera efectiva.


#### Casos Potenciales de Bad Smell

**Consulta**:
```sql
SELECT name, price FROM products WHERE category = 'fruit' OR category = 'dairy';
```

**Expectativa**: Verdadero (detecta un bad smell)

**Razón**: La consulta utiliza `OR` para filtrar por dos categorías. Es más eficiente usar `IN` cuando se buscan múltiples valores para una sola columna, ya que esto permite el uso de índices y generalmente resulta en un mejor rendimiento.

#### Casos Sin Bad Smell

**Consulta**:
```sql
SELECT name, price FROM products WHERE category = 'fruit' OR ZipCode = '1900';
```
**Expectativa**: Falso (no detecta un bad smell)

**Razón**: Aquí, `OR` se utiliza para comparar dos columnas diferentes, lo cual es un caso de uso legítimo para `OR`. No hay una manera directa de optimizar esta consulta usando `IN`, ya que involucra dos campos diferentes.

**Consulta**:

```sql
SELECT name, price FROM products WHERE category IN ('fruit', 'dairy') OR ZipCode IN (1900, 2000);
```

**Expectativa**: Falso (no detecta un bad smell)

**Razón**: Esta consulta ya utiliza `IN` para la comparación de múltiples valores en una sola columna, lo cual es la práctica recomendada. Además, combina condiciones para dos campos diferentes usando `OR`, lo que es adecuado y no constituye un bad smell.

## 5 - Casos de prueba: Comodines Líderes 

El "bad smell" en SQL relacionado con el uso de comodines líderes se refiere a la práctica de usar el carácter `%` (comodín) al principio de un patrón en una cláusula `LIKE` en lugar de al final. Esto puede tener un impacto negativo en el rendimiento de las consultas, especialmente cuando se trabaja con grandes conjuntos de datos, ya que el motor de la base de datos no puede utilizar índices de manera efectiva para optimizar la búsqueda.

- **Caso 1: donde se espera detectar el bad smell:**

```sql
SELECT * FROM products WHERE name LIKE '%apple%';` 
```
**Expectativa**: Verdadero (detecta un bad smell)

**Razón**: En esta consulta, se utiliza el comodín líder `%` al principio del patrón "apple". Esto significa que la consulta buscará cualquier valor que contenga "apple" en cualquier posición de la columna "name". El uso de un comodín líder al principio de la cadena no permite que se utilicen índices y puede resultar en una búsqueda menos eficiente, especialmente en grandes conjuntos de datos.

**Caso 2: donde no se espera detectar el bad smell:**

```sql
SELECT * FROM products WHERE name LIKE 'apple%';
```
```sql
SELECT * FROM products WHERE name LIKE 'appl%e';
```
**Expectativa**: Falso (no detecta un bad smell)

**Razón**: En estas consultas, el comodín `%` se encuentra al final del patrón en la cláusula `LIKE`. Esto significa que la consulta buscará cualquier valor que comience con "apple" o tenga "appl" al principio, lo cual es una búsqueda más específica. En este caso, los índices pueden utilizarse de manera más eficiente.




## 6 - Casos de prueba: Uso de ORDER BY sin LIMIT

El "bad smell" en SQL relacionado con el uso de la cláusula `ORDER BY` sin la limitación de resultados (`LIMIT`) se refiere a la práctica de ordenar un conjunto de resultados completo sin limitar la cantidad de filas devueltas. Esto puede resultar en consultas ineficientes y consumir recursos innecesarios, especialmente cuando se trabaja con grandes conjuntos de datos.

-   **Caso 1: Uso de ORDER BY sin LIMIT:**


```sql
SELECT name FROM users ORDER BY name;
```

**Expectativa**: Verdadero (detecta el problema)

**Razón**: En esta consulta, se utiliza `ORDER BY` para ordenar la columna "name" de la tabla "users", pero no se incluye una cláusula `LIMIT` para limitar la cantidad de filas devueltas. Esto podría llevar a la ordenación de un gran conjunto de resultados sin necesidad, lo que resultaría en un rendimiento deficiente.

-   **Caso 2: Uso de ORDER BY con LIMIT:**

```sql
SELECT name FROM users ORDER BY name LIMIT 10;
```

**Expectativa**: Falso (no detecta el problema)

**Razón**: En esta consulta, se utiliza `ORDER BY` para ordenar la columna "name", pero se incluye una cláusula `LIMIT 10`, lo que limita la cantidad de filas devueltas a 10. Esto es una práctica recomendada, ya que la ordenación se aplica solo a un conjunto de resultados más pequeño.

-   **Caso 3: Casos donde no se espera detectar el problema:**


```sql
SELECT name FROM users;
```
```sql
SELECT id, date FROM orders;
```
```sql
SELECT product_name FROM products WHERE price > 100;
```

**Expectativa**: Falso (no detecta el problema)

**Razón**: En estos casos, no se utiliza la cláusula `ORDER BY` en las consultas, por lo que no hay un problema relacionado con la falta de limitación de resultados. 


## 7 - Casos de prueba: Uso de SELECT *

El uso de `SELECT *` en consultas SQL se refiere a seleccionar todas las columnas disponibles en una tabla. Esto puede ser problemático en ciertas situaciones, ya que puede llevar a la recuperación de datos innecesarios o al aumento de la complejidad de la consulta. A continuación, se presentan casos de prueba para detectar el uso de `SELECT *` en consultas SQL:

-   **Caso 1: Uso de SELECT** 
```sql
SELECT * FROM users;
```
**Expectativa**: Verdadero (detecta el problema)

**Razón**: En esta consulta, se utiliza `SELECT *`, lo que significa que se seleccionarán todas las columnas de la tabla "users". Esto puede ser problemático si la tabla tiene muchas columnas o si solo se necesitan algunas columnas específicas, ya que se recuperarán datos innecesarios.

-   **Caso 2: Uso de SELECT * con otras columnas:**
```sql
SELECT *, name FROM employees;
```

**Expectativa**: Verdadero (detecta el problema)

**Razón**: En esta consulta, se utiliza `SELECT *` junto con la columna "name". Esto aún seleccionará todas las columnas de la tabla "employees", lo que puede no ser eficiente si solo se necesita la columna "name". Es preferible seleccionar solo las columnas necesarias en lugar de todas.

-   **Caso 3: Casos donde no se espera detectar el problema:**

```sql
SELECT name FROM users;
```
```sql
SELECT id, name FROM employees;
```
```sql
SELECT u.name FROM users u;
```

**Expectativa**: Falso (no detecta el problema)

**Razón**: En estos casos, no se utiliza `SELECT *` en las consultas. En su lugar, se seleccionan columnas específicas, lo que es una práctica recomendada para mejorar la eficiencia de las consultas y reducir la complejidad del código SQL.


## Conclusiones

El desarrollo de este proyecto de detección de bad smells en consultas SQL ha sido una exploración valiosa en la intersección del análisis estático de código y las mejores prácticas de bases de datos. A través de la implementación de una serie de detectores de patrones de consulta ineficientes,  pudiendo identificar y resaltar áreas críticas donde las consultas SQL podrían mejorarse para una mayor eficiencia y rendimiento.

### Resultados Clave

- La utilización de `ANTLR4` y una gramática específica para SQLite ha demostrado ser efectiva para el análisis y la manipulación del AST (Abstract Syntax Tree) generado de las consultas SQL.
- La creación de clases especializadas como `ExpressionSmellDetector` y `SQLSmellDetectorVisitor` ha permitido modularizar la lógica de detección y mantener el código organizado y mantenible.
- Las pruebas unitarias desempeñan un papel crucial en la validación de la lógica de detección de bad smells. Cada detector fue probado con una variedad de consultas SQL, permitiendo que los bad smells fueran identificados.



### Desafíos y Aprendizajes

A lo largo del proyecto, se enfretaron varios desafíos que  ofrecieron valiosos aprendizajes. Uno de los principales retos fue la interpretación correcta de la gramática de SQL y la identificación precisa de los patrones de malas prácticas en una variedad de formas en que se pueden escribir las consultas SQL.

#### Aprendizaje del Patrón Visitor

- Uno de los aprendizajes más significativos fue la comprensión y aplicación del patrón de diseño Visitor. Este patrón nos permitió realizar operaciones sobre los elementos de un objeto estructura sin cambiar las clases de los elementos sobre los que opera.
- En términos sencillos, el patrón Visitor  permitió "visitar" los nodos del AST generado por el análisis de SQL y aplicar operaciones específicas, como la detección de bad smells, sin necesidad de modificar la estructura o las clases de los nodos.
- La habilidad para agregar nuevas operaciones a estas estructuras sin modificarlas es una ventaja clave del patrón Visitor, lo que resultó una gran practica para mantener el código modular.

#### Aprendizaje de SQL

- A través del proyecto, se amplio el conocimiento de SQL, especialmente en lo que respecta a patrones de consultas eficientes versus ineficientes.
- Profundizamos en el entendimiento de cómo ciertas prácticas, aunque válidas sintácticamente, pueden llevar a un rendimiento subóptimo en bases de datos reales.
- Este conocimiento no solo se aplicó en la detección de bad smells sino también en la creación de casos de prueba y en la validación de los resultados de nuestra herramienta.


## Referencias

- [Documentación de ANTLR](https://github.com/antlr/antlr4/blob/master/doc/index.md)
- [Dev.Java](https://dev.java/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Eclipse](https://www.eclipse.org/downloads/)
- [ANTLR Tutorial](https://tomassetti.me/antlr-mega-tutorial/) 
-  [7 Bad Practices to Avoid When Writing SQL Queries for Better Performance](https://dev.to/abdelrahmanallam/7-bad-practices-to-avoid-when-writing-sql-queries-for-better-performance-c87)
- [The Definitive ANTLR 4 Reference](https://pragprog.com/titles/tpantlr2/the-definitive-antlr-4-reference/)
- [SQL Code Smells / Red-gate](https://www.red-gate.com/simple-talk/databases/sql-server/t-sql-programming-sql-server/sql-code-smells/)


# Web Scraping

[![Java Version](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/usuario/proyecto)
[![Coverage](https://img.shields.io/badge/coverage-85%25-green)](https://github.com/usuario/proyecto)


Este repositorio contiene utilidades y herramientas para extraer datos útiles de diversos sitios web. Los scripts están 
 escritos en Java y utilizan bibliotecas como Jsoup, Flexmark y Playwright para un web scraping eficiente.

## 📋 Tabla de Contenidos

- [Características](#características)
- [Requisitos Previos](#requisitos-previos)
- [Preparación](#preparación)
- [Testing](#testing)
- [Tecnologías](#tecnologías)
- [Autor](#autor)

## ✨ Características

- Realiza el web scraping de un URL dado, no sigue enlaces.
- Retorna el contenido como Markdown

## 📦 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- **Java 21** o superior [Temurin® JDK](https://adoptium.net/temurin/releases/?version=21&os=any&arch=any)
- **Maven 3.9+**
- **Git** para clonar el repositorio

Para verificar la versión de Java:
```bash
java -version
```

## 🚀 Preparación

### Clonar el repositorio

```bash
git clone git@github.com:mrocabado/web-scrape.git
cd web-scrape
```

### Instalar navegadores para Playwright

Configurar la siguiente variable de entorno:

```properties
PLAYWRIGHT_BROWSERS_PATH=D:\pw-browsers
```

Ejecutar el siguiente comando para [instalar los navegadores](https://playwright.dev/java/docs/browsers) necesarios:
```bash
# only running tests headlessly
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps --no-shell"
```

### Compilar con Maven

```bash
mvn clean install
```

## 🧪 Testing

### Ejecutar todos los tests

**Con Maven:**
```bash
mvn test
```

### Frameworks de Testing

- **JUnit 5** - Framework de testing
- **AssertJ** - Aserciones fluidas

### Servidor web local
Las páginas web de prueba deben estar en `test\resources\webpages` y para accederlas via HTTP se puede usar un servidor web 
 simple como [`jwebserver`](https://dev.java/learn/jvm/tool/jwebserver/).


```bash
jwebserver -d D:\dev\workspace-agents\web-search\src\test\resources\webpage -p 8080
```

## 🛠️ Tecnologías

### Core

- **Java 21** - Lenguaje de programación
- **Maven** - Gestión de dependencias y build
### Features de Java 21 Utilizadas

- Virtual Threads (Project Loom)
- Pattern Matching para switch
- Record Patterns
- Sequenced Collections

### Frameworks y Librerías

- **JSoup** - Librería para extraer datos desde HTML
- **Flexmark** - Librería para analizar y renderizar Markdown
- **Playwright** - Librería para automatización de pruebas en navegadores y web scraping

### Pruebas

- JUnit 5
- AssertJ

### Estándares de Código

- Sigue las convenciones de código de Java
- Escribe tests para nuevas funcionalidades
- Documenta el código con Javadoc
- Mantén la cobertura de tests por encima del 80%

## 👤 Autor

**Marcelo Rocabado**

- GitHub: [@mrocabado](https://github.com/mrocabado)

---

⭐️ Si este proyecto te fue útil, considera darle una estrella en GitHub
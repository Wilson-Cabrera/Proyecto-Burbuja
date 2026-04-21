### :rocket:Proyecto-Burbuja:rocket:
Diseño y desarrollo de una aplicación móvil para la generación de cuentos personalizados mediante inteligencia artificial.

[![banner.png](https://i.postimg.cc/vZWsCpTx/banner.png)](https://postimg.cc/5XNTCKwf)


### Indice

- [Introducción](#Introducción)
- [Objetivos Específicos de la Aplicación](#Objetivos-Específicos-de-la-Aplicación)
- [Ejemplo de uso](#Ejemplo-de-uso)
- [Estructura aplicación (MAP) para ArtFlow](#Estructura-aplicación-(MAP)-para-ArtFlow)
- [Arquitectura del Sistema](#Arquitectura-del-Sistema)
- [Identidad visual](#Identidad-visual)
- [Contexto Académico](#Contexto-Académico)
- [Enlaces de interes](#Enlaces-de-interes)
- [Colaboradores](#Colaboradores)
- [Responsables](#Responsables)
- [Autores](#Autores)
  


------------
### Introducción
Burbuja es una aplicación para dispositivos móviles diseñada para transformar la lectura pasiva en una experiencia creativa y dinámica. A través de una interfaz de vanguardia, la plataforma permite a jovenes de entre 16 y 21 años sumergirse en historias únicas generadas por Inteligencia Artificial, adaptadas a sus propios intereses y elecciones.
Gracias a su enfoque en el diseño multimedia, tecno y regenerativo, Burbuja no solo entretiene, sino que actúa como un motor de imaginación, desafiando el consumo de contenido lineal y fomentando una formación educativa activa.

------------
### Objetivos Específicos de la Aplicación

- Diseñar una interfaz de usuario para la aplicación que sea atractiva e intuitiva para el público joven.

- Implementar un sistema que permita el escaneo de objetos y lugares a través de la cámara del dispositivo móvil.

- Integrar un motor de inteligencia artificial con la capacidad de generar historias personalizadas basadas en los elementos escaneados por el usuario.

- Diseñar y desarrollar una base de datos eficiente para administrar las cuentas de usuario y asegurar el almacenamiento de los cuentos e historias generadas.

**Características Principales:**
> Captura de entorno: permite a los usuarios utilizar la cámara integrada para fotografiar objetos o lugares cotidianos (como libros, paisajes o llaves) que servirán como el punto de partida de la historia.

> Generación de cuentos únicos: utiliza un sistema de inteligencia artificial generativa para construir narraciones instantáneas y personalizadas basadas en los atributos visuales del objeto capturado.

> Personalización mediante etiquetas: el usuario puede seleccionar variables predefinidas para definir el género (misterio, aventura, fantasía, etc.), el tipo de narrador (primera o tercera persona), el tiempo narrativo y el nivel de complejidad del lenguaje.

> Biblioteca personal ("Mis burbujas"): incluye una sección donde se almacenan y organizan todos los cuentos creados para su lectura posterior.

> Gestión en la nube: utiliza Firebase para la autenticación de usuarios, la sincronización de datos y el almacenamiento seguro de las fotos y relatos generados. 

------------

## Ejemplo de uso
El proceso de uso de Burbuja se inicia cuando el usuario activa la cámara integrada de la aplicación para capturar una fotografía de cualquier objeto, espacio o situación que servirá como base narrativa. Tras realizar la captura, el usuario selecciona etiquetas predefinidas que funcionan como variables de personalización, permitiendo definir el género del cuento, la voz del narrador, el tiempo narrativo y el nivel de complejidad del lenguaje. Estas opciones guían una solicitud estructurada que se envía a una API de inteligencia artificial, la cual procesa la información para devolver un relato único y adaptado a las decisiones tomadas. Finalmente, el cuento generado se almacena automáticamente en una biblioteca personal denominada "Mis burbujas" mediante la infraestructura de Firebase, permitiendo su lectura posterior y la posibilidad de gestionar todas las historias creadas

------------
## Estructura aplicación (MAP) para Burbuja

<a href='https://i.postimg.cc/L6w2mLVv/map2-1.png' target='_blank'><img src='https://i.postimg.cc/L6w2mLVv/map2-1.png' border='0' alt='imagen-2023-11-16-132147246'/></a>

-----------

## Arquitectura del Sistema

1 Capa de Cliente (Frontend Nativo):
El desarrollo se realiza de forma nativa para garantizar la máxima compatibilidad y rendimiento con el sistema operativo Android
- Entorno de Desarrollo: Android Studio como IDE principal.
- Lenguaje de Programación: Kotlin, seleccionado por su eficiencia, seguridad y escalabilidad
- Gestión de Interfaz: Basada en estándares de diseño minimalista prototipados en Figma para reducir la carga cognitiva.
- Salida de Producción: Generación de un archivo APK para facilitar la instalación directa y distribución gratuita inicial.

2 Capa de Backend (Infraestructura Cloud):
Se implementa Firebase utilizando el Spark Plan (gratuito) para centralizar la lógica de negocio y los datos sin costos operativos iniciales.
- Autenticación: Gestión segura de perfiles de usuario y sesiones.
- Firebase Storage: Almacenamiento eficiente de las fotografías capturadas por el usuario.
- Base de Datos: Gestión en tiempo real de las colecciones de cuentos, configuraciones de usuario y etiquetas narrativas.

3 Capa de Procesamiento (Inteligencia Artificial):
Este módulo actúa como el motor creativo de la aplicación, transformando datos visuales y variables en texto literario.
- Integración de API: Conexión con servicios de IA de vanguardia Gemini.
- Ingeniería de Prompts: La app organiza automáticamente los datos de la foto y las etiquetas seleccionadas en una solicitud estructurada, eliminando la necesidad de que el usuario redacte instrucciones complejas.
- Intercambio de Datos: La API procesa la información y retorna el relato en formato JSON para ser interpretado y presentado por la aplicación.

4 Seguridad:

------------

## El Flujo de Datos (System Loop)
La comunicación entre componentes sigue un ciclo cerrado para mantener la integridad de la información:

1 La App captura la imagen y las preferencias del usuario.

2 Firebase almacena la captura y sincroniza los metadatos.

3 La App envía el prompt estructurado a la API de IA.

4 La IA genera el contenido y lo devuelve a la App en formato JSON.

5 La App presenta el cuento y lo guarda permanentemente en la biblioteca del usuario.

------------

**Aviso:**

Esta arquitectura proporciona una visión particular en terminos de los conocimientos basicos presentes en el equipo y se puede ajustar según las necesidades específicas de la aplicación y las competencias adquiridas durante el desarrollo. Es importante considerar que la arquitectura del sistema irá creciendo a medida de los avances. La vision general de la misma y el sistema completo se irá implementando durante el diseño del sistema o mas bien al finalizar el proyecto, debido a lo mencionado anteriormente.

------------

## Identidad visual
El sistema de diseño de Burbuja es moderno y tecno-regenerativo, utilizando una estética minimalista para evitar la sobrecarga cognitiva.

 Paleta de colores 
 #F7F9FC
 #7BCBFF
 #6A5CFF
 #1F2A37
 #EAEAF0

 ------------
 
 ## Contexto Académico
 
 Institución: Universidad Nacional de La Rioja (UNLAR).
 
 Departamento: Ciencias Exactas, Físicas y Naturales.
 
 Autor: Wilson Antonio Cabrera.
 
 Tutor: Ariel Alan Rivadulla

 ------------
 
## Enlaces de interes

- Wireframing: [Figma](https://www.figma.com/design/R02dsS0jttKVTsBIC2m9FO/flujo-de-pantalla--BURBUJA-?node-id=0-1&t=AVNeLDzzktv3kkiH-1)
- UX UI: [Figma](https://www.figma.com/design/R02dsS0jttKVTsBIC2m9FO/flujo-de-pantalla--BURBUJA-?node-id=64-64&t=1JzGkZrKTeG1ibhD-1)
- Wire flow: [Figma](https://www.figma.com/design/R02dsS0jttKVTsBIC2m9FO/flujo-de-pantalla--BURBUJA-?node-id=146-295&t=AVNeLDzzktv3kkiH-1)
- Drive del proyecto: [Drive](https://drive.google.com/drive/folders/10YzvB7wEyixrEVSRyOrqgjHdvAm70NT6?usp=sharing)
- (MAP): [MIRO](https://miro.com/app/board/uXjVNWA5kUA=/?share_link_id=327497059956)

------------

<!-- ## Colaboradores
- [@ppedersoli](https://github.com/ppedersoli)-->
  
------------

  ## Responsables
- Análisis General: Wilson Antonio Cabrera.
- UX/UI: Wilson Antonio Cabrera.
- FrontEnd: FrontEnd: Wilson Antonio Cabrera.
- Backend: Wilson Antonio Cabrera.

------------

## Autor
- [@Wilson-Cabrera](https://github.com/Wilson-Cabrera)

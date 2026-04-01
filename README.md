### :rocket:Proyecto-Burbuja:rocket:
Diseño y desarrollo de una aplicación móvil para la generación de cuentos personalizados mediante inteligencia artificial.

[![banner.png](https://i.postimg.cc/vZWsCpTx/banner.png)](https://postimg.cc/5XNTCKwf)


### Indice

- [Introducción](#Introducción)
- [Objetivos Específicos de la Aplicación](#Objetivos-Específicos-de-la-Aplicación)
- [Ejemplo de uso](#Ejemplo-de-uso)
- [Estructura aplicación (MAP) para ArtFlow](#Estructura-aplicación-(MAP)-para-ArtFlow)
- [Arquitectura del Sistema](#Arquitectura-del-Sistema)
- [Historias de usuario](#Historias-de-usuario)
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
## Estructura aplicación (MAP) para ArtFlow

<a href='https://postimg.cc/m1FNTnd4' target='_blank'><img src='https://i.postimg.cc/brm69ckZ/imagen-2023-11-16-132147246.png' border='0' alt='imagen-2023-11-16-132147246'/></a>

-----------

## Arquitectura del Sistema

1 Cliente (Frontend):
- plataforma: Desarrollo Android Studio
- Framework de IU: A definir

2 Servidor (Backend):
- Lenguaje de Programación: Java
- Base de datos: SQLite

3 Autenticación y Autorización:

4 Almacenamiento en la Nube:

5 Seguridad:



**Aviso:**

Esta arquitectura proporciona una visión particular en terminos de los conocimientos basicos presentes en el equipo y se puede ajustar según las necesidades específicas de la aplicación y las competencias adquiridas durante el desarrollo. Es importante considerar que la arquitectura del sistema irá creciendo a medida de los avances. La vision general de la misma y el sistema completo se irá implementando durante el diseño del sistema o mas bien al finalizar el proyecto, debido a lo mencionado anteriormente.

## Historias de usuario
Historia de usuario  | --
------------- | -------------
ID:  | 1
Nombre de la historia  |  Inicio de Sesión en la Aplicación
Programador responsable | Flores, Kiru
Descripción | Como usuario de la aplicación, quiero tener la capacidad de iniciar sesión de manera segura y conveniente para acceder a mis datos y personalizar mi experiencia.
Validación | El usuario se asegura de que los datos ingresados como los nombres, dirección de correo electrónico o contraseñas sean correctos y cumplan con ciertos estándares.
Criterios | El usuario tendrá dos campos para llenar, el primero con su Email y el segundo con su contraseña, después tendrá que apretar el botón de iniciar sesión para ingresar. O en su defecto si el usuario no cuenta con una cuenta tendrá un enlace que le llevara a una sección para poder registrarse.


Historia de usuario  | --
------------- | -------------
ID:  | 2
Nombre de la historia  |  Registro de Usuario
Programador responsable | Miotto, Lautaro
Descripción | Como usuario diseñador multimedia responsable quiero registrarme con mi Email, nombre, nombre de usuario y contraseña.
Validación | El usuario responsable debe generar una cuenta por lo que deberá poner los datos requeridos por el sistema.
Criterios | El usuario deberá llenar cuatro campos de texto, E.mail, nombre completo, nombre de usuario y contraseña. En segundo lugar deberá hacer click en el botón (Registrate). O como usuario de Google o Facebook podrás registrarte con la cuenta que tengas existentes.


Historia de usuario  | --
------------- | -------------
ID:  | 3
Nombre de la historia  |  Visualización de la feed tendencias
Programador responsable | Cabrera, Wilson
Descripción | Como usuario responsable, quiero ser capaz de poder ver las publicaciones que están en tendencias para que pueda ver las inspiraciones de la comunidad.
Validación | El usuario responsable al acceder mediante la validación debe explorar las publicaciones.
Criterios | La feed muestra las publicaciones más recientes y populares. Se le permite al usuario hacer click en cada post para acceder a otra sección de la publicación donde podrá ver el posteo y tener otras opciones de interacción. Además tendrá un menú en la parte inferior con funcionalidades como refrescar, guardar, crear y perfil. En la parte superior tendrá un buscador en la cual puede escribir palabras específicas para una búsqueda personalizada.



Historia de usuario  | --
------------- | -------------
ID:  | 4
Nombre de la historia  |  Visualización de video  Reel con interacción.
Programador responsable | Waidatt, Samira.
Descripción | Como usuario de la aplicación, quiero poder acceder a una sección que muestre un video Reel. Y tambien ser capaz de interactuar con el contenido.
Validación | El usuario responsable debe visualizar los videos reel, nombre de usuario y descripción en pantalla completa e interactuar con dicho video. También debe abrir la galería si desea subir un reel haciendo click en el icono "+".
Criterios |Esta sección presentará contenido multimedia de manera clara y atractiva. El usuario puede reproducir el video reel y manejarlo mediante un control de reproducción estándar. Para acceder a los comentarios debe pulsar en un icono de nube, para guardar el video pulsar en un icono de bandera y para dar like a un icono de corazón. En la parte superior tendrá un menú donde podrá "Ir a la sección tendencias, guardados, crear y perfil"

------------

## Enlaces de interes

- Mockups: [Figma](https://www.figma.com/file/Mk79vOIMddFzCAAQutru4Z/Mockup-ArtFlow?type=design&t=I7Lh8mMQmfV63vsz-6)
- Proceso del Mockup: [Reel](https://www.instagram.com/p/CzpA0JVuP3U/)
- (MAP): [MIRO](https://miro.com/app/board/uXjVNWA5kUA=/?share_link_id=327497059956)

------------

## Colaboradores
- [@ppedersoli](https://github.com/ppedersoli)
  
------------

  ## Responsables
- Análisis General: Cabrera Wilson Antonio 
- UX/UI: Cabrera Wilson Antonio
- FrontEnd: Miotto González Lautaro, 
- Backend: Waidatt Samira , Flores Kiru Brian Juan

------------

## Autores
- [@Wilson-Cabrera](https://github.com/Wilson-Cabrera)
- Miotto Lautaro Gonzalez
- Flores Juan Kiru
- Waidatt Samira

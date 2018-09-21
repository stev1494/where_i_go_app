# Where I go?

App Android que muestra rutas de buses de la ciudad de Guayaquil, Ecuador. Se compone de tres secciones: "Rutas", "Más cerca" y "Buscar ruta".

### Rutas
Sección que muestra una lista con las rutas de autobuses disponibles, al seleccionar alguna se muestra un mapa desplegando la ruta y las estaciones por donde transita.

### Más cerca
Esta sección muestra las rutas de buses que se encuentran más cerca de la posición actual dentro de un rango de 10 a 2000 m.

### Buscar ruta
En esta sección se busca y muestra la ruta más próxima a dos puntos "origen" y "destino".

## Getting Started
Para compilar la aplicación requiere de las Google APIs Maps SDK Android y Places SDK Android. En la consola de desarrollador se crea un nuevo proyecto y se habilitan las APIs:

* [Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk/intro)
* [Places SDK for Android](https://developers.google.com/places/android-sdk/intro)

[Google API Console](https://console.developers.google.com/) - Consola de desarrollador

Posteriormente se crea una Clave de API accediendo a la sección Credenciales de la consola.

La Clave de API Generada se copia a la variable *google_maps_key* ubicada en el archivo *google_maps_api.xml* de la carpeta **Values**.


------------

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc

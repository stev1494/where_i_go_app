# Where I go?

App hecha en Android que muestra rutas de buses de la ciudad de Guayaquil, Ecuador. 

Se compone de tres secciones: "Rutas", "Más cerca" y "Buscar ruta".

### Rutas
Sección que muestra una lista con las rutas de autobuses disponibles, al seleccionar alguna se muestra un mapa desplegando la ruta y las estaciones por donde transita.

### Más cerca
Esta sección muestra las rutas de buses que se encuentran más cerca de la posición actual dentro de un rango de 10 a 2000 m.

### Buscar ruta
En esta sección se busca y muestra la ruta más próxima a dos puntos "origen" y "destino".

## APIs
Para compilar la aplicación requiere de las Google APIs Maps SDK Android y Places SDK Android. En la consola de desarrollador se crea un nuevo proyecto y se habilitan las APIs:

* [Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk/intro)
* [Places SDK for Android](https://developers.google.com/places/android-sdk/intro)

[Google API Console](https://console.developers.google.com/) - Consola de desarrollador

Posteriormente se crea una Clave de API accediendo a la sección Credenciales de la consola.

La Clave de API Generada se copia a la variable *google_maps_key* ubicada en el archivo *google_maps_api.xml* de la carpeta **Values**.


------------


## Diseño
Posee dos activities la principal contiene 3 fragments o fragmentos.

Un fragmento es una sección “modular” de interfaz de usuario embebida dentro de una actividad anfitriona, el cual permite versatilidad y optimización de diseño.

En cada fragment se muestra una seccion de la aplicacion en especial, que corresponde a :
* Lista de autobuses
* Encontrar la ruta cercana
* Buscar ruta entre dos puntos.

El otro activity se abre cuando en la pestaña de la lista de rutas se selecciona una , haciendo que se cargue un mapa.





## Contribuciones especiales al proyecto

* Alix Huerta 
```
public class PlaceArrayAdapter extends ArrayAdapter<PlaceArrayAdapter.PlaceAutocomplete> implements Filterable {
    private static final String TAG = "PlaceArrayAdapter";
    private GoogleApiClient mGoogleApiClient;
    private AutocompleteFilter mPlaceFilter;
    private LatLngBounds mBounds;
    private ArrayList<PlaceAutocomplete> mResultList;
    
    .
    .
    .
    
  ```
  
  Esta Clase utiliza la Api Places de Google , aquí la reutilizamos para poder generar el autocompletado en los textbox del apartado de "Buscar Ruta".
  
  ------------
  
  * Jeffrey sambells
  
  La url es donde encontramos el método es el siguiente:
  
  
   - http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
   
   
   
   
   
   ## Autor

* **Steven Andrade S.** - [Stev1494](https://github.com/stev1494)
   





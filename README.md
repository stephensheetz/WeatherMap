# WeatherMap
### WeatherMap app demonstrates the use of:

- MVVM Architecture with LiveData

- Activity and multiple Fragment UI that supports alternate views for rotated screen

- Primary / Detail screen navigation

- Kotlin language with coroutines

- View binding

- Dagger Hilt dependency injection

- MapBox interactive geographic map (point markers and map camera movement)

- RecyclerView for scrolling list of forecast details

- Repository pattern to abstract data management, using Flow

- Cached data for previous locations

- Retrofit library for connection to opensource REST API

- Room database for local storage

- Loading remote images from URLs

- Sharing content to other apps

- Basic unit tests

- Basic Espresso UI tests 



## What it does / How to use it

There is a geographic map on which you can click to place a marker and fetch
the weather forecast for the next few days. The forecast gets displayed in a list
below or next to the map, in 3 hour increments.  Clicking a map location also
adds the place name to a drop-down list of recently viewed places.  Clicking
a forecast will open a details screen.  You can return to the main screen with the
back button.  Long-pressing a forecast will offer some sharing options.

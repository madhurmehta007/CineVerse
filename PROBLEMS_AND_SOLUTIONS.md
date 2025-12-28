# Problems & Solutions

This document outlines the key challenges faced during development and how they were solved.

## 1. Shared Element Transitions Between Screens

### Problem
Implementing smooth shared element transitions in Jetpack Compose required the `SharedTransitionLayout` to be a common ancestor of both source and destination screens, but with Navigation Compose, each composable destination is created independently.

### Solution
- Wrapped the entire `NavHost` inside a `SharedTransitionLayout` in MainActivity
- Created `CompositionLocal` providers (`LocalSharedTransitionScope`, `LocalAnimatedVisibilityScope`) to pass the transition scopes down to child composables
- Each screen and component accesses the scopes via `CompositionLocalProvider`

```kotlin
SharedTransitionLayout {
    CompositionLocalProvider(LocalSharedTransitionScope provides this) {
        NavHost(...) {
            composable("movies") {
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    MoviesGridScreen(...)
                }
            }
        }
    }
}
```

### Tradeoffs
- Slightly increased complexity in component signatures
- Alternative: Use `AnimatedNavHost` (less control over transition timing)

---

## 2. Syncing Favorites Across All Screens Instantly

### Problem
Favorites marked on one screen (Grid, Detail, Favorites) needed to reflect immediately on all other screens without manual refresh.

### Solution
- **Single Source of Truth**: `MovieRepository` combines remote movies with favorites from `FavoritesLocalDataSource`
- **Reactive Flows**: Used `Flow.combine()` to merge movies and favorites into a single stream
- **Local Persistence**: SQLDelight stores favorite IDs, and changes emit through the flow

```kotlin
fun getMovies(): Flow<List<Movie>> = combine(
    _movies,
    favoritesDataSource.getAllFavoriteIds()
) { movies, favorites ->
    movies.map { it.copy(isFavorite = favorites.contains(it.id)) }
}
```

### Tradeoffs
- All screens receive updates, even when not visible (mitigated by `WhileSubscribed(5000)`)
- Alternative: Event bus pattern (harder to maintain)

---

## 3. Ktor Client Setup in KMP

### Problem
Needed to share networking code between Android and iOS while handling platform-specific HTTP engines.

### Solution
- Used `Ktor MockEngine` for development/demo with predefined responses
- Platform-specific engines configured via Koin DI:
  - Android: `OkHttp` engine
  - iOS: `Darwin` engine
- Content negotiation with `kotlinx.serialization`

```kotlin
val mockEngine = MockEngine { _ ->
    respond(
        content = Json.encodeToString(mockMovies),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
}
HttpClient(mockEngine) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}
```

### Tradeoffs
- MockEngine for demo means no real API calls (easily swappable)
- Alternative: Use expect/actual for engine (more boilerplate)

---

## 4. Persistence with SQLDelight

### Problem
Needed cross-platform local database for favorites that works on both Android and iOS.

### Solution
- Defined schema in `.sq` files
- Created `DatabaseDriverFactory` with expect/actual for platform drivers
- `FavoritesLocalDataSource` provides reactive Flow of favorite IDs
- SQLDelight generates type-safe Kotlin code

```kotlin
// Schema: Favorite.sq
CREATE TABLE Favorite (
    movieId TEXT NOT NULL PRIMARY KEY
);
```

### Tradeoffs
- Learning curve for SQLDelight syntax
- Alternative: Room (Android-only, not multiplatform)

---

## 5. Long-Press Animation Instead of Hover

### Problem
Assignment specified hover animation on cards, but mobile doesn't have hover. Assignment note clarified to use long-press instead.

### Solution
- Used `pointerInput` with `detectTapGestures(onLongPress = ...)`
- Animated multiple properties: scale, elevation, translationY
- Overlay info slides in with fade animation
- Auto-dismiss after 2 seconds using `LaunchedEffect`

```kotlin
Modifier.pointerInput(Unit) {
    detectTapGestures(
        onTap = { onMovieClick(movie.id) },
        onLongPress = { 
            isPressed = true
            showOverlay = true
        }
    )
}
```

---

## 6. Search with Debounce

### Problem
Live search without debounce caused excessive recompositions and lag during fast typing.

### Solution
- Added `Flow.debounce(300)` to search query flow
- Filtering only happens 300ms after user stops typing

```kotlin
val movies = combine(
    sharedViewModel.movies,
    _searchQuery.debounce(300)
) { allMovies, query ->
    if (query.isBlank()) allMovies
    else allMovies.filter { it.title.contains(query, ignoreCase = true) }
}
```

---

## 7. Detail Screen Staggered Reveal

### Problem
Wanted details to appear sequentially for a polished feel, not all at once.

### Solution
- Used multiple `AnimatedVisibility` with delayed visibility triggers
- `LaunchedEffect` triggers each section with staggered delays
- Combined `fadeIn` + `slideInVertically` for smooth entrance

```kotlin
LaunchedEffect(Unit) {
    delay(200) // Wait for shared element
    showPlayButton = true
    delay(100)
    showSynopsis = true
    delay(100)
    showDirector = true
    // ...
}
```

---

## Key Decisions Summary

| Decision | Chosen | Alternative | Reason |
|----------|--------|-------------|--------|
| Navigation | Jetpack Navigation | Decompose | Simpler setup, assignment focus |
| DI | Koin | Hilt | KMP compatible |
| Images | Coil | Glide | Modern Compose-first API |
| Database | SQLDelight | Room | Cross-platform |
| Networking | Ktor | Retrofit | Cross-platform |

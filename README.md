## Build notes
Create `app-settings.properties` by copying and filling up `example.app-settings.properties`.

## Info

It's my personal app that is using predefined API endpoints/structure 
which is served by my [really dumb file serve written in golang](https://github.com/FluffyDiscord/go-simple-file-serve), it also handles everything I need for this app, 
but you can use your own as long as it supports endpoints bellow:

```kotlin
GET /galleries
```
expects object with array of galleries
```ts
{
  "entries": [
    {
      "name": "GALLERY_IDENTIFICATION" // this will be called later on 
    }
  ]
}
```

App will then create list of these galleries and for each one load cover image when they are visible to the user
```kotlin
GET /galleries/GALLERY_IDENTIFICATION/cover.jpg
```
It expects small image around 300x650.

After opening the gallery, app makes a metadata request
```kotlin
GET /galleries/GALLERY_IDENTIFICATION/metadata.json
```
expected result
```ts
{
  "name_pretty": "Gallery Name", 
  "images": 50, // total amount of images 
}
```
App then loads images in numeric order from 1 to `images` from metadata, eg. `1.jpg`, then `2.jpg`, then `3.jpg` and so on until for example the `50.jpg`

package com.example.storyapp

import com.example.storyapp.api.Story
import java.time.Instant
import java.time.format.DateTimeFormatter

object DataDummy {
    fun generateDummyStories(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..10) {
            val story = Story(
                id = "story-$i",
                name = "Author $i",
                description = "Story description $i",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-$i",
                createdAt = DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                lat = if (i % 2 == 0) -6.8957643 else null,
                lon = if (i % 2 == 0) 107.6338462 else null
            )
            items.add(story)
        }
        return items
    }
}
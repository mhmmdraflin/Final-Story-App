package com.dicoding.first_subsmission_rafli.view.main

import com.dicoding.first_subsmission_rafli.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0 until 100) {
            val story = ListStoryItem(
                i.toString(),
                "createdAt$i",
                "name$i",
                "description$i",
                i * 0.1,
                "id$i",
                i * 0.1
            )
            items.add(story)
        }
        return items
    }
}
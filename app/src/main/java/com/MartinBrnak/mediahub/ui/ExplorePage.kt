package com.MartinBrnak.mediahub.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun ExplorePage(
    modifier: Modifier = Modifier
){
    var value by remember { mutableStateOf("") }
    TextField(
        value = value,
        onValueChange = {value =it},
        label = { Text("Search for Gifs") },
        maxLines = 1,
        placeholder = {"Type to find gifs, for example \"Cats\""},
        modifier = Modifier
            .fillMaxWidth()
    )
    LazyColumn(
        modifier = modifier
    ) {

    }
}



@Preview
@Composable
fun previewExplorePage(){
    ExplorePage()
}
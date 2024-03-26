package com.example.supabasedemo.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AllInbox
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.example.supabasedemo.R
import com.example.supabasedemo.model.MainScreenDest
import com.example.supabasedemo.screens.Persons.PersonScreen
import com.example.supabasedemo.screens.Shelf.ShelfScreen
import com.example.supabasedemo.screens.Things.ThingsMainScreen

@ExperimentalMaterial3AdaptiveNavigationSuiteApi
@Composable
fun MyAdaptiveApp() {
    val destinations = listOf(
        MainScreenDest(stringResource(id = R.string.persons), Icons.Rounded.Person) {
                PersonScreen(it)
        },
        MainScreenDest(stringResource(R.string.things), Icons.Rounded.Build) {
                ThingsMainScreen(it)
        },
        MainScreenDest(stringResource(R.string.shelves), Icons.Rounded.AllInbox) {
                ShelfScreen(it)
        },
    )

    val selected = remember { mutableIntStateOf(0) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
                destinations.forEachIndexed { index, (name, icon) ->
                    this.item(
                        icon = { Icon(imageVector = icon, contentDescription = null) },
                        label = { Text(text = name) },
                        selected = selected.intValue == index,
                        onClick = { selected.intValue = index }
                    )
                }
        }
    ) {
        Crossfade(targetState = selected.intValue, label = "CurrentPage") {
            destinations[it].content
        }
    }
}
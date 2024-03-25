package com.example.supabasedemo.customelements

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.supabasedemo.R
import com.example.supabasedemo.SharedPreference

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SearchBarCustom(navController: NavController) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth())
    {
        var text by remember { mutableStateOf("") } // Query for SearchBar
        var active by remember { mutableStateOf(false) } // Active state for SearchBar
        val searchHistory = remember { mutableStateListOf("") }
        val sharedPreference: SharedPreference = SharedPreference(LocalContext.current)
        LaunchedEffect(Unit) {
            val savedHistory = sharedPreference.sharedPref.getStringSet("searchHistory", emptySet())
            searchHistory.addAll(savedHistory ?: emptySet())
        }
        Column(modifier = Modifier.padding(5.dp)) {
            SearchBar(
                query = text,
                onQueryChange = {
                    text = it
                },
                onSearch = {
                    searchHistory.add(text)
                    active = false
                    navController.navigate("searchResults/$text")
                    with(sharedPreference.sharedPref.edit()) {
                        putStringSet("searchHistory", searchHistory.toSet())
                        apply()
                    }
                },
                active = active,
                onActiveChange = {
                    active = it
                },
                placeholder = {
                    Text(text = stringResource(R.string.search))
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon")
                },
                trailingIcon = {
                    if (active) {
                        Icon(
                            modifier = Modifier.clickable {
                                if (text.isNotEmpty()) {
                                    text = ""
                                } else {
                                    active = false
                                }
                            },
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close icon"
                        )
                    }
                }
            ) {
                searchHistory.forEach { historyItem ->
                    if (historyItem.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .padding(all = 14.dp)
                                .clickable {
                                    text = historyItem
                                    active = false
                                    navController.navigate("searchResults/$historyItem")
                                }
                        ) {
                            Icon(imageVector = Icons.Default.History, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = historyItem)
                        }
                    }
                }

                Divider()
                Text(
                    modifier = Modifier
                        .padding(all = 14.dp)
                        .fillMaxWidth()
                        .clickable {
                            searchHistory.clear()
                            with(sharedPreference.sharedPref.edit()) {
                                putStringSet("searchHistory", searchHistory.toSet())
                                apply()
                            }
                        },
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    text = stringResource(R.string.clear_history)
                )
            }
        }
        IconButton(
            onClick = { navController.navigate("Scanner") },
            modifier = Modifier.size(90.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
fun SearchTest() {
    SearchBarCustom(navController = rememberNavController())
}

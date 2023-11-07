package com.example.supabasedemo.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.supabasedemo.customelements.SearchBarCustom
import com.example.supabasedemo.customelements.ThingCard
import com.example.supabasedemo.customelements.UserHead
import com.example.supabasedemo.model.Contacts
import com.example.supabasedemo.model.Persons
import com.example.supabasedemo.model.Things
import com.example.supabasedemo.viewmodel.PersonsViewmodel
import com.example.supabasedemo.viewmodel.ThingsViewmodel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ThingsMainScreen(navController: NavController, viewModel: ThingsViewmodel = viewModel())
{
    val things by viewModel.things.collectAsState(initial =  mutableListOf() )
    if (things.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBarCustom()
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(60.dp))
            }
        }
    }
    else
    {
        var openDialog = remember { mutableStateOf(false) }
        Scaffold(modifier = Modifier
            .fillMaxSize()
            .padding(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 70.dp),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        openDialog.value = true
                    }
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "add icon")
                }
            },
            content = {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth()) {
                    SearchBarCustom()
                    Spacer(modifier = Modifier.height(8.dp))
                    ThingColumn(navController = navController, viewModel)
                }
            }
        )
        if (openDialog.value)
        {
            //AddPersonDialog(openDialog.value, onDismiss = {openDialog.value = false}, viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThingColumn(navController: NavController, viewModel: ThingsViewmodel = viewModel())
{
    val things by viewModel.things.collectAsState(initial = mutableListOf())
    var columnAppeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        columnAppeared = true
    }
    LazyColumn(modifier = Modifier.fillMaxWidth())
    {
        items(things){ thing ->
            var dismissState = rememberDismissState()
            val coroutineScope = rememberCoroutineScope()
            val dismissDirection = dismissState.dismissDirection
            var isDismissed = dismissState.isDismissed(DismissDirection.EndToStart)
            // check if the user swiped
            if (isDismissed && dismissDirection == DismissDirection.EndToStart) {
                DeleteThingDialog(thing, viewModel, onDismiss = {isDismissed = true; coroutineScope.launch{dismissState.reset()}}, onCancel = {isDismissed = false; coroutineScope.launch{dismissState.reset()}})
            }

            var itemAppeared by remember { mutableStateOf(!columnAppeared) }
            LaunchedEffect(Unit) {
                itemAppeared = true
            }
            if(viewModel.deleteComplete.value && itemAppeared && !isDismissed) {
                viewModel.deleteComplete.value = false
            }
            AnimatedVisibility(
                visible = itemAppeared && !viewModel.deleteComplete.value,
                exit = shrinkVertically(
                    animationSpec = tween(
                        durationMillis = 300,
                    )
                ),
                enter = expandVertically(
                    animationSpec = tween(
                        durationMillis = 300
                    )
                )
            ) {
                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(
                        DismissDirection.EndToStart
                    ),
                    background = {
                        val backgroundColor by animateColorAsState(
                            when (dismissState.targetValue) {
                                DismissValue.DismissedToStart -> MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                else -> MaterialTheme.colorScheme.background
                            }
                        )

                        // icon size
                        val iconScale by animateFloatAsState(
                            targetValue = if (dismissState.targetValue == DismissValue.DismissedToStart) 1.3f else 0.5f
                        )

                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(color = backgroundColor)
                                .padding(end = 16.dp), // inner padding
                            contentAlignment = Alignment.CenterEnd // place the icon at the end (left)
                        ) {
                            Icon(
                                modifier = Modifier.scale(iconScale),
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete",
                                tint = Color.White
                            )
                        }
                    },
                    dismissContent =
                    {
                        ThingCard(thing)
                    })
            }
        }
    }
}

@Composable
fun DeleteThingDialog(thing: Things, viewModel: ThingsViewmodel = viewModel(), onDismiss: () -> Unit, onCancel: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var openDialog by remember { mutableStateOf(true) }
    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                openDialog = false
                onCancel()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        //coroutineScope.launch { viewModel.delete(thing.id!!) }
                        openDialog = false
                        onDismiss()
                    }
                ) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                        onCancel()
                    }
                ) {
                    Text(text = "No")
                }
            },
            title = { Text(text = "Are you sure?") },
            text = { Text(text = "Are you sure you want to delete ${thing.name}?") },
            icon = { Icon(imageVector = Icons.Default.Delete, contentDescription = null) } // add icon
        )
    }
}

@Composable
fun AddThingDialog(open: Boolean, onDismiss: () -> Unit, viewModel: ThingsViewmodel = viewModel()) {
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var telegram by remember { mutableStateOf("") }
    var avito by remember { mutableStateOf("") }
    if (open) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        var new_person = Persons(0, name, surname, 0)
                        var new_contact = Contacts(0, phone, telegram, avito)
                        viewModel.deleteComplete.value = true
                        //coroutineScope.launch {viewModel.insertContact(new_contact, new_person)}
                        onDismiss()
                    }
                ) {
                    Text(text = "Done")
                }
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Name") }
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    OutlinedTextField(
                        value = surname,
                        onValueChange = { surname = it },
                        placeholder = { Text("Surname") }
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        placeholder = { Text("+700000000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    OutlinedTextField(
                        value = telegram,
                        onValueChange = { telegram = it },
                        placeholder = { Text("@Telegram") }
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    OutlinedTextField(
                        value = avito,
                        onValueChange = { avito = it },
                        placeholder = { Text("Avito") }
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(text = "Cancel")
                }
            },
            title = { Text(text = "Add a new person") },
            icon = { Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null) } // add icon
        )
    }
}
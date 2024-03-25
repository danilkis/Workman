package com.example.supabasedemo.screens.Shelf.Dialog

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.supabasedemo.R
import com.example.supabasedemo.model.Shelf.Shelf
import com.example.supabasedemo.viewmodel.Shelf.ShelfViewmodel
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState", "DiscouragedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateShelfDialog(
    open: Boolean,
    onDismiss: () -> Unit,
    viewModel: ShelfViewmodel = viewModel(),
    shelf: Shelf //TODO: Воткнуть
) {
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var availableLevels by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var floor by remember { mutableStateOf("") }
    val ctx = LocalContext.current
    name = shelf.name
    availableLevels = shelf.available_levels.toString()
    room = shelf.room
    floor = shelf.floor.toString()

    if (open) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.updateShelf(
                                Shelf(
                                    shelf.id,
                                    name,
                                    availableLevels.toInt(),
                                    room,
                                    floor.toInt()
                                )
                            )
                        }
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(R.string.done))
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text(stringResource(R.string.name)) }
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    OutlinedTextField(
                        value = availableLevels,
                        onValueChange = { availableLevels = it },
                        placeholder = { Text("Кол-во полок") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    OutlinedTextField(
                        value = room,
                        onValueChange = { room = it },
                        placeholder = { Text("Помещение") }
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    OutlinedTextField(
                        value = floor,
                        onValueChange = { floor = it },
                        placeholder = { Text("Этаж") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            title = { Text(text = "Изменение стелажа") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
            } // add icon
        )
    }
}
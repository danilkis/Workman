package com.example.supabasedemo.model

import kotlinx.serialization.Serializable


/** Модель серериализацией для контактов
 * @param id ID контакта
 * @param phone телефон в строковом представлении
 * @param telegram @ тэг телеграм, может быть пустым
 * @param url ссылка на авито либо сторонний ресурс
 */
@Serializable
data class Contacts(
    val id: Int,
    val phone: String,
    val telegram: String?,
    val url: String?
)